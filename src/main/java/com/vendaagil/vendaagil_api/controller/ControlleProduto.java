package com.vendaagil.vendaagil_api.controller;

import com.vendaagil.vendaagil_api.dto.ProdutoRequestDTO;
import com.vendaagil.vendaagil_api.dto.ProdutoResponseDTO;
import com.vendaagil.vendaagil_api.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ControlleProduto {
    private final ProdutoService produtoService;

    public ControlleProduto(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }
    //Cadastrar Produto
   @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar (@RequestBody @Valid ProdutoRequestDTO dto){
        ProdutoResponseDTO produtoCriado = produtoService.criar(dto);
        return  ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }
    // GET: Listar todos os produtos -> Retorna 200 OK
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    // Buscar produto por ID -> Retorna 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    // Atualizar produto por ID -> Retorna 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody  @Valid ProdutoRequestDTO dto
    ) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizar(id, dto);
        return ResponseEntity.ok(produtoAtualizado);
    }

    // DELETE: Excluir produto por ID -> Retorna 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
