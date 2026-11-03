package com.vendaagil.vendaagil_api.service;

import com.vendaagil.vendaagil_api.dto.ProdutoRequestDTO;
import com.vendaagil.vendaagil_api.dto.ProdutoResponseDTO;
import com.vendaagil.vendaagil_api.model.Produto;
import com.vendaagil.vendaagil_api.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

import java.util.List;

public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;

    }
    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());

        Produto produtoSalvo = produtoRepository.save(produto);
        return new ProdutoResponseDTO(produtoSalvo);
    }
 //   @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(ProdutoResponseDTO::new)
                .toList();
    }
}
