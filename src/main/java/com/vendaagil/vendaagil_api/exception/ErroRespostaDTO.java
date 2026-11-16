package com.vendaagil.vendaagil_api.exception;

import java.time.LocalDateTime;

public record ErroRespostaDTO(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem
) {}