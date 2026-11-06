package com.vendaagil.vendaagil_api.dto;

public record AuthResponseDTO(
        String token,
        String tipo,
        String nome,
        String email,
        Long empresaId,
        String perfil
) {
    public AuthResponseDTO(String token, String nome, String email, Long empresaId, String perfil) {
        this(token, "Bearer", nome, email, empresaId, perfil);
    }
}