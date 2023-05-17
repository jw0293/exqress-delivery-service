package com.example.deliveryservice.utils;

import com.example.deliveryservice.StatusEnum;
import com.example.deliveryservice.dto.TokenInfo;
import com.example.deliveryservice.entity.DeliveryEntity;
import com.example.deliveryservice.repository.DeliveryRepository;
import com.example.deliveryservice.vo.response.ResponseData;
import com.example.deliveryservice.vo.response.ResponseDelivery;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenUtils {

    private final Environment env;
    private static String secretKey;
    private final RedisTemplate redisTemplate;
    private final DeliveryRepository deliveryRepository;

    @PostConstruct
    protected void init(){
        secretKey = env.getProperty("token.secret");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public TokenInfo generateToken(String uid){

        Claims claims = Jwts.claims().setSubject(uid);

        Date now = new Date();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(System.currentTimeMillis() +
                Long.parseLong(env.getProperty("token.access_expiration_time")));

        log.info("AccessTokenExpiresIn : {}", accessTokenExpiresIn);

        String accessToken = Jwts.builder()
                .setSubject(uid)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();


        Date refreshExpirationTime = new Date(System.currentTimeMillis() +
                Long.parseLong(env.getProperty("token.refresh_expiration_time")));

        log.info("RefreshExpirationTime : {}", refreshExpirationTime);

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(uid)
                .setExpiration(refreshExpirationTime)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(Long.parseLong(env.getProperty("token.refresh_expiration_time")))
                .build();
    }

    public boolean isValidToken(String token){
        try {
            log.info("Valid Token : {}", token);
            Date expiration;
            try {
                expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
            } catch (Exception e) {
                log.info("Expiration Error", e);
                return false;
            }
            log.info("Get expiration : {}", expiration.getTime());
            Long now = System.currentTimeMillis();
            if (expiration.getTime() - now > 0) {
                return true;
            }
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        } catch (SignatureException e){
            log.info("JWT signature does not match");
        } catch (Exception e){
            log.info("EXCEPTION");
        }

        log.info("False로 반환");
        return false;
    }

    public ResponseDelivery getAuthentication(String token) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        DeliveryEntity deliveryEntity = deliveryRepository.findByDeliveryId(Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody().getSubject());

        return mapper.map(deliveryEntity, ResponseDelivery.class);
    }

}
