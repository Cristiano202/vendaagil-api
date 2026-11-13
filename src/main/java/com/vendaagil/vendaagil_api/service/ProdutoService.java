package com.vendaagil.vendaagil_api.service;

import com.vendaagil.vendaagil_api.dto.ProdutoRequestDTO;
import com.vendaagil.vendaagil_api.dto.ProdutoResponseDTO;
import com.vendaagil.vendaagil_api.model.Produto;
import com.vendaagil.vendaagil_api.model.Usuario;
import com.vendaagil.vendaagil_api.repository.ProdutoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        Usuario usuarioLogado = getUsuarioLogado();

        Produto produto = new Produto();
        produto.setId(gerarIdUnico());
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());
        produto.setEmpresa(usuarioLogado.getEmpresa());

        Produto produtoSalvo = produtoRepository.save(produto);
        return new ProdutoResponseDTO(produtoSalvo);
    }

    private Long gerarIdUnico() {
        long idGerado;
        do {
            idGerado = ThreadLocalRandom.current().nextLong(100_000L, 1_000_000L);
        } while (produtoRepository.existsById(idGerado));
        return idGerado;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        Long empresaId = getUsuarioLogado().getEmpresa().getId();

        return produtoRepository.findAllByEmpresaId(empresaId)
                .stream()
                .map(ProdutoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        Long empresaId = getUsuarioLogado().getEmpresa().getId();

        Produto produto = produtoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));
        return new ProdutoResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Long empresaId = getUsuarioLogado().getEmpresa().getId();

        Produto produto = produtoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));

        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());

        Produto produtoAtualizado = produtoRepository.save(produto);
        return new ProdutoResponseDTO(produtoAtualizado);
    }

    @Transactional
    public void excluir(Long id) {
        Long empresaId = getUsuarioLogado().getEmpresa().getId();

        Produto produto = produtoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));

        produtoRepository.delete(produto);
    }
}