package com.vendaagil.vendaagil_api.dto;

import com.vendaagil.vendaagil_api.model.Produto;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque
) {
    public ProdutoRequestDTO (Produto produto){
        this(
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque()

                );
    }


}
