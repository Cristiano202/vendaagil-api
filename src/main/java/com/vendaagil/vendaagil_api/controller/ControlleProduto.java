package com.vendaagil.vendaagil_api.controller;

import com.vendaagil.vendaagil_api.dto.ProdutoRequestDTO;
import com.vendaagil.vendaagil_api.dto.ProdutoResponseDTO;
import com.vendaagil.vendaagil_api.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produtos")
public class ControlleProduto {
    private final ProdutoService produtoService;

    public ControlleProduto(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }
    //Cadastrar Produto
   @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar (@RequestBody ProdutoRequestDTO dto){
        ProdutoResponseDTO produtoCriado = produtoService.criar(dto);
        return  ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

}
