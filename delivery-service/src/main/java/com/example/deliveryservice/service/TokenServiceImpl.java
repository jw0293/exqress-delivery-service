package com.example.deliveryservice.service;

import com.example.deliveryservice.StatusEnum;
import com.example.deliveryservice.constants.AuthConstants;
import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.dto.TokenInfo;
import com.example.deliveryservice.utils.CookieUtils;
import com.example.deliveryservice.utils.TokenUtils;
import com.example.deliveryservice.vo.request.RequestToken;
import com.example.deliveryservice.vo.response.ResponseData;
import com.example.deliveryservice.vo.response.ResponseDelivery;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    private final Environment env;
    private final TokenUtils tokenUtils;
    private final CookieUtils cookieUtils;
    private final RedisTemplate redisTemplate;

    @Override
    public String createToken(DeliveryDto deliveryDto){

        log.info("Token Expiration_time : {}", env.getProperty("token.expiration_time"));
        log.info("Token Secret : {}", env.getProperty("token.secret"));

        return Jwts.builder()
                .setSubject(deliveryDto.getDeliveryId())
                .setExpiration(
                        new Date(System.currentTimeMillis() +
                                Long.parseLong(env.getProperty("token.expiration_time")))
                )
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();
    }

    @Override
    public ResponseEntity<ResponseData> reissue(HttpServletRequest request, HttpServletResponse response) {
        Cookie refreshCookie = cookieUtils.getCookie(request, AuthConstants.REFRESH_HEADER);
        String refreshToken = refreshCookie.getValue();
        log.info("Reissue Refresh Token : {}", refreshToken);
        // 1. Refresh Token 검증
        if (!tokenUtils.isValidToken(refreshToken)) {
            return new ResponseEntity<>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "Refresh 토큰이 유효하지 않습니다.", "", ""), HttpStatus.BAD_REQUEST);
        }

        log.info("유효한 토큰 확인");
        // 2. Access Token 에서 User email 을 가져옵니다.
        ResponseDelivery authenticationUser = tokenUtils.getAuthentication(refreshToken);

        log.info("AuthUser Name : {}", authenticationUser.getName());
        log.info("AuthUser Email : {}", authenticationUser.getEmail());
        log.info("AuthUser UserId : {}", authenticationUser.getDeliveryId());

        // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshTokenFromRedis = (String) redisTemplate.opsForValue().get("RT:" + authenticationUser.getDeliveryId());
        // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshTokenFromRedis)) {
            return new ResponseEntity<>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "잘못된 요청입니다.", "", ""), HttpStatus.BAD_REQUEST);
        }
        if(!refreshTokenFromRedis.equals(refreshToken)) {
            return new ResponseEntity<>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "Refresh 토큰이 일치하지 않습니다.", "", ""), HttpStatus.BAD_REQUEST);
        }

        // 4. 새로운 토큰 생성
        TokenInfo newTokenInfo = tokenUtils.generateToken(authenticationUser.getDeliveryId());
        log.info("New Token Success !");

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT:" + authenticationUser.getDeliveryId(), newTokenInfo.getRefreshToken(), newTokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        Cookie cookie = cookieUtils.createCookie(AuthConstants.REFRESH_HEADER, newTokenInfo.getRefreshToken());
        response.addCookie(cookie);
        response.setContentType("application/json;charset=UTF-8");

        log.info("New 토큰 반환");
        return new ResponseEntity<>(new ResponseData(StatusEnum.OK.getStatusCode(), "Token 정보가 갱신되었습니다.", "", newTokenInfo.getAccessToken()), HttpStatus.OK);
    }

    @Override
    public String getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return request.getHeader(AuthConstants.AUTHORIZATION_HEADER).substring(AuthConstants.TOKEN_TYPE.length());
    }
}
