package com.vendaagil.vendaagil_api.dto;

import com.vendaagil.vendaagil_api.model.Venda;
import com.vendaagil.vendaagil_api.model.enums.FormaPagamento;
import com.vendaagil.vendaagil_api.model.enums.StatusVenda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponseDTO(
        Long id,
        LocalDateTime dataHora,
        BigDecimal valorTotal,
        FormaPagamento formaPagamento,
        StatusVenda status,
        String nomeOperador,
        List<ItemVendaResponseDTO> itens
) {
    public VendaResponseDTO(Venda venda) {
        this(
                venda.getId(),
                venda.getDataHora(),
                venda.getValorTotal(),
                venda.getFormaPagamento(),
                venda.getStatus(),
                venda.getUsuario().getNome(),
                venda.getItens().stream()
                        .map(ItemVendaResponseDTO::new)
                        .toList()
        );
    }

}
