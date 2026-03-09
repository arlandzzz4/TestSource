package com.project.global.security;

import java.util.ArrayList;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyPlain;
    
    private SecretKey key; // 주입된 문자열을 Key 객체로 변환하여 저장
    private final long AT_EXP = 1000L * 60 * 30; // 30분

    @PostConstruct
    public void init() {
        // [변경 이유] 문자열을 직접 사용하는 대신, Base64로 인코딩된 키를 안전하게 Key 객체로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyPlain);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 생성
    public String createAccessToken(Authentication auth) {
        return Jwts.builder()
                .subject(auth.getName()) 
                .claim("auth", auth.getAuthorities())
                .expiration(new Date(System.currentTimeMillis() + AT_EXP)) 
                .signWith(key) 
                .compact();
    }
    
    public String createAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + AT_EXP))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14))
                .signWith(key)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key) 
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 인증 정보 추출
    public Authentication getAuthentication(String token) {
        // [변경 이유] parseClaimsJws().getBody() -> parseSignedClaims().getPayload()로 용어 변경
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", new ArrayList<>());
    }
}