package com.project.global.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        // parseClaimsJws().getBody() -> parseSignedClaims().getPayload()로 용어 변경
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        // 클레임에서 권한 정보 가져오기 (예: "ROLE_ADMIN,ROLE_USER")
        Object authClaim = claims.get("auth");
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        
        if (authClaim != null) {
            authorities = Arrays.stream(authClaim.toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
    }
}