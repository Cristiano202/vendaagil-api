package com.vendaagil.vendaagil_api.service;

import com.vendaagil.vendaagil_api.dto.AuthResponseDTO;
import com.vendaagil.vendaagil_api.dto.LoginRequestDTO;
import com.vendaagil.vendaagil_api.dto.RegistroEmpresaRequestDTO;
import com.vendaagil.vendaagil_api.model.Empresa;
import com.vendaagil.vendaagil_api.model.PerfilUsuario;
import com.vendaagil.vendaagil_api.model.Usuario;
import com.vendaagil.vendaagil_api.repository.EmpresaRepository;
import com.vendaagil.vendaagil_api.repository.UsuarioRepository;
import com.vendaagil.vendaagil_api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService{

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(EmpresaRepository empresaRepository,
                       UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponseDTO registrarEmpresa(RegistroEmpresaRequestDTO dto) {
        if (empresaRepository.findByCnpj(dto.cnpj()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma empresa cadastrada com este CNPJ.");
        }

        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("O email informado já está em uso.");
        }

        // 1. Cadastra a Empresa (Tenant)
        Empresa empresa = new Empresa(dto.razaoSocial(), dto.cnpj());
        empresa = empresaRepository.save(empresa);

        // 2. Cadastra o primeiro usuário como ADMIN dessa empresa
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario admin = new Usuario(
                dto.nomeAdmin(),
                dto.email(),
                senhaCriptografada,
                PerfilUsuario.ADMIN,
                empresa
        );
        admin = usuarioRepository.save(admin);

        // 3. Emite o token JWT com o ID da nova empresa
        String token = jwtService.gerarToken(admin.getEmail(), empresa.getId(), admin.getPerfil().name());

        return new AuthResponseDTO(token, admin.getNome(), admin.getEmail(), empresa.getId(), admin.getPerfil().name());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        if (!usuario.getAtivo() || !usuario.getEmpresa().getAtivo()) {
            throw new IllegalArgumentException("Conta ou empresa inativa.");
        }

        String token = jwtService.gerarToken(
                usuario.getEmail(),
                usuario.getEmpresa().getId(),
                usuario.getPerfil().name()
        );

        return new AuthResponseDTO(
                token,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getEmpresa().getId(),
                usuario.getPerfil().name()
        );
    }
}