package com.vendaagil.vendaagil_api.security;

import com.vendaagil.vendaagil_api.model.AuthProvider;
import com.vendaagil.vendaagil_api.model.PerfilUsuario;
import com.vendaagil.vendaagil_api.model.Usuario;
import com.vendaagil.vendaagil_api.repository.UsuarioRepository;
import com.vendaagil.vendaagil_api.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final String redirectUri;

    public OAuth2AuthenticationSuccessHandler(UsuarioRepository usuarioRepository,
                                              JwtService jwtService,
                                              @Value("${app.oauth2.redirect-uri}") String redirectUri) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.redirectUri = redirectUri;
    }

    // @Transactional: a empresa do usuario e LAZY e precisa de sessao aberta para ser lida
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");
        Boolean emailVerificado = oAuth2User.getAttribute("email_verified");

        // 1. So aceita e-mail que o proprio Google confirmou
        if (email == null || !Boolean.TRUE.equals(emailVerificado)) {
            redirecionar(request, response, "error=email_nao_verificado");
            return;
        }

        // 2. Busca o usuario ou cria um novo (sem empresa, perfil OPERADOR)
        Optional<Usuario> existente = usuarioRepository.findByEmail(email);
        Usuario usuario;

        if (existente.isPresent()) {
            usuario = existente.get();

            // Conta criada com e-mail e senha: nao junta automaticamente com o Google
            if (usuario.getProvider() != AuthProvider.GOOGLE) {
                redirecionar(request, response, "error=conta_local_existente");
                return;
            }
            if (!Boolean.TRUE.equals(usuario.getAtivo())) {
                redirecionar(request, response, "error=conta_inativa");
                return;
            }
        } else {
            usuario = criarUsuarioGoogle(email, nome);
        }

        // 3. Gera o JWT da VendaAgil (empresaId pode ser nulo)
        Long empresaId = usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null;
        String token = jwtService.gerarToken(usuario.getEmail(), empresaId, usuario.getPerfil().name());

        // 4. Devolve o token no fragmento (#token=...), que nao vai para logs de servidor
        redirecionar(request, response, "token=" + token);
    }

    private Usuario criarUsuarioGoogle(String email, String nome) {
        Usuario novo = new Usuario();
        novo.setEmail(email);
        novo.setNome(definirNome(nome, email));
        novo.setPerfil(PerfilUsuario.OPERADOR);
        novo.setProvider(AuthProvider.GOOGLE);
        novo.setAtivo(true);
        return usuarioRepository.save(novo);
    }

    private String definirNome(String nome, String email) {
        String base = (nome != null && !nome.isBlank()) ? nome : email.split("@")[0];
        return base.length() > 100 ? base.substring(0, 100) : base;
    }

    private void redirecionar(HttpServletRequest request,
                              HttpServletResponse response,
                              String fragmento) throws IOException {
        String url = UriComponentsBuilder.fromUriString(redirectUri)
                .fragment(fragmento)
                .build()
                .toUriString();
        getRedirectStrategy().sendRedirect(request, response, url);
    }
}