package com.vendaagil.vendaagil_api.security;

import com.vendaagil.vendaagil_api.repository.UsuarioRepository;
import com.vendaagil.vendaagil_api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Busca o cabeçalho Authorization da requisição
        final String authHeader = request.getHeader("Authorization");

        // 2. Se não houver cabeçalho ou não começar com "Bearer ", segue o fluxo sem autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrai apenas o token (removendo os 7 caracteres de "Bearer ")
        final String token = authHeader.substring(7);
        final String email = jwtService.extrairEmail(token);

        // 4. Se extraiu um e-mail e o usuário ainda não está autenticado no contexto atual
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                // 5. Valida se o token é autêntico e não expirou
                if (jwtService.isTokenValido(token, usuario.getEmail())) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name());

                    // 6. Cria a credencial interna do Spring Security colocando o objeto Usuario como principal
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            Collections.singletonList(authority)
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 7. Registra o usuário como autenticado para esta requisição
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            });
        }

        filterChain.doFilter(request, response);
    }
}