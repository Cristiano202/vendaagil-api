package com.vendaagil.vendaagil_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        @NotBlank(message = "O nome do produto é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser superior a zero")
        BigDecimal preco,

        @NotNull(message = "A quantidade de stock é obrigatória")
        @PositiveOrZero(message = "A quantidade de stock não pode ser negativa")
        Integer quantidadeEstoque
) {

}

