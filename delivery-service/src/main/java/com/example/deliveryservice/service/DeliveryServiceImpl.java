package com.example.deliveryservice.service;

import com.example.deliveryservice.StatusEnum;
import com.example.deliveryservice.constants.AuthConstants;
import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.dto.TokenInfo;
//import com.example.deliveryservice.dto.kafka.DeliveryInfoWithQRId;
import com.example.deliveryservice.entity.DeliveryEntity;
import com.example.deliveryservice.entity.QRcode;
import com.example.deliveryservice.repository.DeliveryRepository;
import com.example.deliveryservice.repository.QRcodeRepository;
import com.example.deliveryservice.utils.CookieUtils;
import com.example.deliveryservice.utils.TokenUtils;
import com.example.deliveryservice.vo.request.RequestLogin;
import com.example.deliveryservice.vo.request.RequestQRcode;
import com.example.deliveryservice.dto.DeliveryQRDto;
import com.example.deliveryservice.vo.response.ResponseData;
import com.example.deliveryservice.vo.response.ResponseDelivery;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final Environment env;
    private final TokenUtils tokenUtils;
    private final CookieUtils cookieUtils;
    private final TokenServiceImpl tokenService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final QRcodeRepository qRcodeRepository;
    private final DeliveryRepository deliveryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    // private final DeliveryServiceClient deliveryServiceClient;
    private ModelMapper mapper;

    @PostConstruct
    public void initMapper(){
        mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DeliveryEntity user =  deliveryRepository.findByEmail(username);

        if(user == null) {
            //throw new UsernameNotFoundException("Not");
            //throw new CustomException(UNAUTHORIZED_MEMBER);
            return null;
        }

        return new User(user.getEmail(), user.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public ResponseEntity<ResponseData> login(HttpServletRequest request, HttpServletResponse response, RequestLogin login) {
        DeliveryEntity entity = deliveryRepository.findByEmail(login.getEmail());
        if(entity == null){
            return new ResponseEntity<>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "존재하지 않는 배송기사 이메일입니다.", "", ""), HttpStatus.BAD_REQUEST);
        }
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        //Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        UserDetails userDetails = loadUserByUsername(authenticationToken.getName());
        if(userDetails == null){
            return new ResponseEntity<>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "존재하지 않는 배송기사 이메일입니다.", "", ""), HttpStatus.BAD_REQUEST);
        }

        if(!bCryptPasswordEncoder.matches(login.getPassword(), entity.getEncryptedPwd())) {
            log.error("비밀번호오류");
            return new ResponseEntity<>(new ResponseData(StatusEnum.Unauthorized.getStatusCode(), "비밀번호 오류입니다.", "", ""), HttpStatus.UNAUTHORIZED);
        }

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = tokenUtils.generateToken(entity.getDeliveryId());

        log.info("Access : {}", tokenInfo.getAccessToken());
        log.info("Refresh :{}", tokenInfo.getRefreshToken());

        Cookie cookie = cookieUtils.createCookie(AuthConstants.REFRESH_HEADER, tokenInfo.getRefreshToken());
        response.addCookie(cookie);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        log.info("Cookie에 담김");

        // 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
        redisTemplate.opsForValue()
                .set("RT:" + entity.getDeliveryId(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return new ResponseEntity<>(new ResponseData(StatusEnum.OK.getStatusCode(), "로그인 성공", "", tokenInfo.getAccessToken()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseData> logout(String accessToken) {
        // 1. Access Token 검증
        if (!tokenUtils.isValidToken(accessToken)) {
            return new ResponseEntity<>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "잘못된 요청입니다.", "", ""), HttpStatus.BAD_REQUEST);
        }
        log.info("유효한 토큰 확인");

        // 2. Access Token 에서 User email 을 가져옵니다.
        ResponseDelivery authentication = tokenUtils.getAuthentication(accessToken);

        log.info("AuthUser Name : {}", authentication.getName());
        log.info("AuthUser Email : {}", authentication.getEmail());
        log.info("AuthUser UserId : {}", authentication.getDeliveryId());

        // 3. Redis 에서 해당 User ID로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getDeliveryId()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getDeliveryId());
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = tokenService.getExpiration(accessToken);
        log.info(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        return new ResponseEntity<>(new ResponseData(StatusEnum.OK.getStatusCode(), "로그아웃 되었습니다.", "", ""), HttpStatus.OK);
    }

    @Override
    public DeliveryDto createUser(DeliveryDto userDto) {
        userDto.setDeliveryId(UUID.randomUUID().toString());

        DeliveryEntity userEntity = mapper.map(userDto, DeliveryEntity.class);
        userEntity.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPassword()));

        deliveryRepository.save(userEntity);

        DeliveryDto returnDeliveryDto = mapper.map(userEntity, DeliveryDto.class);

        return returnDeliveryDto;
    }

    @Override
    public DeliveryDto getUserDetailsByEmail(String email) {
        DeliveryEntity deliveryEntity = deliveryRepository.findByEmail(email);

        if(deliveryEntity == null){
            throw new IllegalArgumentException();
        }

        return new ModelMapper().map(deliveryEntity, DeliveryDto.class);
    }

    @Override
    public DeliveryQRDto mappingQRcode(String deliveryId, RequestQRcode qRcode) {
        DeliveryEntity deliveryEntity = deliveryRepository.findByDeliveryId(deliveryId);
        QRcode qrCodeEntity = mapper.map(qRcode, QRcode.class);
        qrCodeEntity.setDeliveryEntity(deliveryEntity);

        deliveryEntity.getQRcodeList().add(qrCodeEntity);

        // save를 해야 변경감지로 저장이 되나?
        DeliveryQRDto deliveryMapQr = new DeliveryQRDto();
        deliveryMapQr.setDeliveryId(deliveryEntity.getDeliveryId());
        deliveryMapQr.setQrId(qRcode.getQrId());

        deliveryRepository.save(deliveryEntity);
        qRcodeRepository.save(qrCodeEntity);

        return deliveryMapQr;
    }

    @Override
    public String getDeliveryIdThroughRequest(HttpServletRequest request) {
        String author = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        String token = author.substring(7, author.length());
        return tokenUtils.getAuthentication(token).getDeliveryId();
    }

    @Override
    public boolean isDuplicatedUser(String email) {
        DeliveryEntity deliveryEntity = deliveryRepository.findByEmail(email);
        if(deliveryEntity == null) {
            return false;
        }
        return true;
    }

    @Override
    public ResponseEntity<ResponseData> updateParcelCompleteState(RequestQRcode qRcode) {
        QRcode qr = qRcodeRepository.findByQrId(qRcode.getQrId());
        if(qr == null){
            return new ResponseEntity<ResponseData>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "존재하지 않는 QR_ID입니다.", "", ""), HttpStatus.BAD_REQUEST);
        }
        if(qr.isComplete()){
            return new ResponseEntity<ResponseData>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "이미 배송 완료된 물품입니다.", "", ""), HttpStatus.BAD_REQUEST);
        }
        qr.setComplete(true);
        qRcodeRepository.save(qr);

        return new ResponseEntity<ResponseData>(new ResponseData(StatusEnum.OK.getStatusCode(), "배송 완료로 상태 업데이트 완료", "", ""), HttpStatus.OK);
    }
//
//    @Override
//    public DeliveryInfoWithQRId getDeliveryInfoThroughId(String deliveryId) {
//        DeliveryEntity deliveryEntity = deliveryRepository.findByDeliveryId(deliveryId);
//        DeliveryInfoWithQRId deliveryInfoWithQRId = new DeliveryInfoWithQRId();
//
//        deliveryInfoWithQRId.setState("배송 시작");
//        deliveryInfoWithQRId.setDeliveryName(deliveryEntity.getName());
//        deliveryInfoWithQRId.setDeliveryPhoneNumber(deliveryEntity.getPhoneNumber());
//
//        return deliveryInfoWithQRId;
//    }
}
