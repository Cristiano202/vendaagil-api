package com.vendaagil.vendaagil_api.controller;

import com.vendaagil.vendaagil_api.dto.VendaRequestDTO;
import com.vendaagil.vendaagil_api.dto.VendaResponseDTO;
import com.vendaagil.vendaagil_api.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> registrarVenda(@RequestBody @Valid VendaRequestDTO dto) {
        VendaResponseDTO response = vendaService.registrarVenda(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> listarVendas() {
        return ResponseEntity.ok(vendaService.listarVendas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<VendaResponseDTO> cancelarVenda(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.cancelarVenda(id));
    }
}