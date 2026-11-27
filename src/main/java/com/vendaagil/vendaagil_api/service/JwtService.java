package com.vendaagil.vendaagil_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
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

    // Sem valor padrao de proposito: se o JWT_SECRET nao existir, a aplicacao nao sobe
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long tempoExpiracao;

    public String gerarToken(String email, Long empresaId, String role) {
        JwtBuilder builder = Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tempoExpiracao));

        // Usuario novo do Google ainda nao tem empresa: so grava o claim se existir
        if (empresaId != null) {
            builder.claim("empresaId", empresaId);
        }

        return builder.signWith(obterChaveAssinatura()).compact();
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