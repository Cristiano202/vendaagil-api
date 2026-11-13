package com.vendaagil.vendaagil_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${api.security.token.secret:sua-chave-secreta-padrao-muito-segura-com-mais-de-32-chars}")
    private String secretKey;

    private static final long TEMPO_EXPIRACAO = 1000 * 60 * 60 * 24;

    public String gerarToken(String email, Long empresaId, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("empresaId", empresaId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TEMPO_EXPIRACAO))
                .signWith(obterChaveAssinatura())
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public Long extrairEmpresaId(String token) {
        Claims claims = extrairTodosClaims(token);
        return claims.get("empresaId", Long.class);
    }

    public boolean isTokenValido(String token, String email) {
        final String emailToken = extrairEmail(token);
        return (emailToken.equals(email) && !isTokenExpirado(token));
    }

    private boolean isTokenExpirado(String token) {
        return extrairClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(obterChaveAssinatura())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obterChaveAssinatura() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}