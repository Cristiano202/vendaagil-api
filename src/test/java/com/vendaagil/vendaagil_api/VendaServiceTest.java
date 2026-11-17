package com.vendaagil.vendaagil_api.service;

import com.vendaagil.vendaagil_api.dto.ItemVendaRequestDTO;
import com.vendaagil.vendaagil_api.dto.VendaRequestDTO;
import com.vendaagil.vendaagil_api.dto.VendaResponseDTO;
import com.vendaagil.vendaagil_api.model.Empresa;
import com.vendaagil.vendaagil_api.model.Produto;
import com.vendaagil.vendaagil_api.model.Usuario;
import com.vendaagil.vendaagil_api.model.Venda;
import com.vendaagil.vendaagil_api.model.enums.FormaPagamento;
import com.vendaagil.vendaagil_api.model.enums.StatusVenda;
import com.vendaagil.vendaagil_api.repository.ProdutoRepository;
import com.vendaagil.vendaagil_api.repository.VendaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private VendaService vendaService;

    private Empresa empresa;
    private Usuario operador;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setRazaoSocial("Supermercado Bom Preço");

        operador = new Usuario();
        operador.setId(1L);
        operador.setNome("Cristiano Carvalho");
        operador.setEmail("cristiano@bompreco.com");
        operador.setEmpresa(empresa);

        var auth = new UsernamePasswordAuthenticationToken(operador, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve registrar venda com sucesso, calcular total e dar baixa no estoque")
    void deveRegistrarVendaComSucesso() {
        // 1. ARRANGE (Preparação dos dados e comportamento dos Mocks)
        Produto arroz = new Produto();
        arroz.setId(1L);
        arroz.setNome("Arroz 5kg");
        arroz.setPreco(new BigDecimal("25.00"));
        arroz.setQuantidadeEstoque(10);
        arroz.setEmpresa(empresa);

        Produto feijao = new Produto();
        feijao.setId(2L);
        feijao.setNome("Feijão 1kg");
        feijao.setPreco(new BigDecimal("8.50"));
        feijao.setQuantidadeEstoque(20);
        feijao.setEmpresa(empresa);

        when(produtoRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(arroz));
        when(produtoRepository.findByIdAndEmpresaId(2L, 1L)).thenReturn(Optional.of(feijao));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> {
            Venda vendaSalva = invocation.getArgument(0);
            vendaSalva.setId(100L);
            return vendaSalva;
        });

        VendaRequestDTO request = new VendaRequestDTO(
                FormaPagamento.PIX,
                List.of(
                        new ItemVendaRequestDTO(1L, 2), // 2 x 25.00 = 50.00
                        new ItemVendaRequestDTO(2L, 1)  // 1 x  8.50 =  8.50
                )
        );

        // 2. ACT (Execução do método real que estamos testando)
        VendaResponseDTO response = vendaService.registrarVenda(request);

        // 3. ASSERT (Verificação dos resultados e efeitos colaterais)
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(new BigDecimal("58.50"), response.valorTotal());
        assertEquals(StatusVenda.CONCLUIDA, response.status());
        assertEquals("Cristiano Carvalho", response.nomeOperador());
        assertEquals(8, arroz.getQuantidadeEstoque());  // 10 - 2
        assertEquals(19, feijao.getQuantidadeEstoque()); // 20 - 1

        verify(produtoRepository, times(2)).save(any(Produto.class));
        verify(vendaRepository, times(1)).save(any(Venda.class));
    }

}