package com.basestudy.rewards.security;

import java.time.ZonedDateTime;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class JwtTokenUtil {
    
    private final long expiraion;
    private final SecretKey jwtSecret;

    JwtTokenUtil(@Value("${jwt.expiration}") long expiraion, @Value("${jwt.secret}") String jwtSecret){
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.jwtSecret = Keys.hmacShaKeyFor(keyBytes);
        this.expiraion = expiraion;
    }
  
    public String generateJwtToken(Authentication authentication) {
        //Member userPrincipal = (Member) authentication.getPrincipal();
        String userName = (String) authentication.getPrincipal();
        
        ZonedDateTime issuedAt = ZonedDateTime.now();
        ZonedDateTime expiredAt = issuedAt.plusSeconds(expiraion);

        return Jwts.builder()
            .subject(userName)
            .issuedAt(Date.from(issuedAt.toInstant()))
            .expiration(Date.from(expiredAt.toInstant()))
            .signWith(this.jwtSecret)
            .compact();
    }

    public boolean validateToken(String jwtToken){
            //TODO: 갱신, 탈취 시나리오 생각해보기
        try {
            Jwts.parser()
                .verifyWith(this.jwtSecret)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
            return true;
        } catch (MalformedJwtException e) {
            log.error("catch exception in jwtTokenUtil = {}", "Invalid JWT Token", e.getMessage());
        } catch (UnsupportedJwtException e){
            log.error("catch exception in jwtTokenUtil = {}", "Unsupported JWT Token", e.getMessage());
        } catch (ExpiredJwtException e){
            log.error("catch exception in jwtTokenUtil = {}", "Expired JWT Token", e.getMessage());
        } catch (IllegalArgumentException e){
            log.error("catch exception in jwtTokenUtil = {}", "JWT claims string is empty.", e.getMessage());
        }
        return false;
    }

    public String getUserName(String jwtToken){
        Claims claims = Jwts.parser().verifyWith(this.jwtSecret).build().parseSignedClaims(jwtToken).getPayload();
        return claims.getSubject();
    }

    // public String getClaims(String jwtToken){
        //필요에 따라 구현
    // }
}
