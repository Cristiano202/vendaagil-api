package com.vendaagil.vendaagil_api.controller;

import com.vendaagil.vendaagil_api.dto.AuthResponseDTO;
import com.vendaagil.vendaagil_api.dto.LoginRequestDTO;
import com.vendaagil.vendaagil_api.dto.RegistroEmpresaRequestDTO;
import com.vendaagil.vendaagil_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDTO> registrar(@RequestBody @Valid RegistroEmpresaRequestDTO dto) {
        AuthResponseDTO resposta = authService.registrarEmpresa(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        AuthResponseDTO resposta = authService.login(dto);
        return ResponseEntity.ok(resposta);
    }
}