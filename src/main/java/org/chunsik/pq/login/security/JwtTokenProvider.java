package org.chunsik.pq.login.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chunsik.pq.login.dto.TokenClaimsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access_token_expire_time}")
    private Long tokenValidTime;

    @Value("${jwt.refresh_token_expire_time}")
    private Long refreshTokenValidTime;

    private final UserDetailsService userDetailsService;

    private static final String CLAIM_KEY_USER_ID = "userId";

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email, Long userId) {
        Claims claims = makeClaims(email, userId);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String email, Long userId) {
        Claims claims = makeClaims(email, userId);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private Claims makeClaims(String email, Long userId) {
        Claims claims = Jwts.claims()
                .setSubject(email);
        claims.put(CLAIM_KEY_USER_ID, userId);
        return claims;
    }

    public Authentication getAuthentication(String token) {
        TokenClaimsDto claimsFromToken = getClaimsFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claimsFromToken.getEmail());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public TokenClaimsDto getClaimsFromToken(String token) {
        // 만료 토큰 예외를 던져버리면 ControllerExceptionHandler에서 예외 처리함.
        Claims body = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return new TokenClaimsDto(body.getSubject(), (Long) body.get(CLAIM_KEY_USER_ID));
    }

    public boolean validateTokenExpiration(String token) {
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
        return true;
    }
}
