package com.vendaagil.vendaagil_api.dto;

import com.vendaagil.vendaagil_api.model.enums.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VendaRequestDTO(
        @NotNull(message = "A forma de pagamento é obrigatória.")
        FormaPagamento formaPagamento,

        @NotEmpty(message = "A venda deve conter pelo menos um item.")
        @Valid
        List<ItemVendaRequestDTO> itens
) {
}
