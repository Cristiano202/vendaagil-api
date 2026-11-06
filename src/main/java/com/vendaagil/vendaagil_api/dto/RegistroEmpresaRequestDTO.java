package com.vendaagil.vendaagil_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroEmpresaRequestDTO(
        @NotBlank(message = "A razão social é obrigatória.")
        String razaoSocial,

        @NotBlank(message = "O CNPJ é obrigatório.")
        String cnpj,

        @NotBlank(message = "O nome do administrador é obrigatório.")
        String nomeAdmin,

        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "Email inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
        String senha
) {}