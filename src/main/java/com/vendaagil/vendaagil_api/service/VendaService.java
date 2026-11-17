package com.vendaagil.vendaagil_api.service;

import com.vendaagil.vendaagil_api.dto.ItemVendaRequestDTO;
import com.vendaagil.vendaagil_api.dto.VendaRequestDTO;
import com.vendaagil.vendaagil_api.dto.VendaResponseDTO;
import com.vendaagil.vendaagil_api.model.ItemVenda;
import com.vendaagil.vendaagil_api.model.Produto;
import com.vendaagil.vendaagil_api.model.Usuario;
import com.vendaagil.vendaagil_api.model.Venda;
import com.vendaagil.vendaagil_api.model.enums.StatusVenda;
import com.vendaagil.vendaagil_api.repository.ProdutoRepository;
import com.vendaagil.vendaagil_api.repository.VendaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public VendaResponseDTO registrarVenda(VendaRequestDTO dto) {
        Usuario operador = getUsuarioLogado();
        Long empresaId = operador.getEmpresa().getId();

        Venda venda = Venda.builder()
                .formaPagamento(dto.formaPagamento())
                .status(StatusVenda.CONCLUIDA)
                .usuario(operador)
                .empresa(operador.getEmpresa())
                .valorTotal(BigDecimal.ZERO)
                .build();

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemVendaRequestDTO itemDto : dto.itens()) {
            Produto produto = produtoRepository.findByIdAndEmpresaId(itemDto.produtoId(), empresaId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto não encontrado ou não pertence à sua empresa. ID: " + itemDto.produtoId()
                    ));

            if (produto.getQuantidadeEstoque() < itemDto.quantidade()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto '" + produto.getNome() +
                                "'. Disponível: " + produto.getQuantidadeEstoque()
                );
            }

            // Baixa no estoque
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemDto.quantidade());
            produtoRepository.save(produto);

            BigDecimal precoUnitario = produto.getPreco();
            BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(itemDto.quantidade()));

            ItemVenda itemVenda = ItemVenda.builder()
                    .produto(produto)
                    .quantidade(itemDto.quantidade())
                    .precoUnitario(precoUnitario)
                    .subtotal(subtotal)
                    .build();

            venda.adicionarItem(itemVenda);
            valorTotal = valorTotal.add(subtotal);
        }

        venda.setValorTotal(valorTotal);
        Venda vendaSalva = vendaRepository.save(venda);

        return new VendaResponseDTO(vendaSalva);
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarVendas() {
        Usuario operador = getUsuarioLogado();
        Long empresaId = operador.getEmpresa().getId();

        return vendaRepository.findAllByEmpresaIdComDetalhes(empresaId)
                .stream()
                .map(VendaResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarPorId(Long id) {
        Usuario operador = getUsuarioLogado();
        Long empresaId = operador.getEmpresa().getId();

        Venda venda = vendaRepository.findByIdAndEmpresaIdComDetalhes(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada. ID: " + id));

        return new VendaResponseDTO(venda);
    }

    @Transactional
    public VendaResponseDTO cancelarVenda(Long id) {
        Usuario operador = getUsuarioLogado();
        Long empresaId = operador.getEmpresa().getId();

        Venda venda = vendaRepository.findByIdAndEmpresaIdComDetalhes(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada. ID: " + id));

        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new IllegalArgumentException("Esta venda já se encontra cancelada.");
        }

        // Estorna a quantidade de cada item de volta ao estoque
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        venda.setStatus(StatusVenda.CANCELADA);
        Venda vendaAtualizada = vendaRepository.save(venda);

        return new VendaResponseDTO(vendaAtualizada);
    }

    private Usuario getUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario usuario) {
            return usuario;
        }
        throw new IllegalStateException("Usuário autenticado não encontrado no contexto de segurança.");
    }
}