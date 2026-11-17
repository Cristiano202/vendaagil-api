package com.vendaagil.vendaagil_api.repository;

import com.vendaagil.vendaagil_api.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    @Query("SELECT DISTINCT v FROM Venda v " +
            "LEFT JOIN FETCH v.itens i " +
            "LEFT JOIN FETCH i.produto " +
            "JOIN FETCH v.usuario " +
            "WHERE v.empresa.id = :empresaId " +
            "ORDER BY v.dataHora DESC")
    List<Venda> findAllByEmpresaIdComDetalhes(@Param("empresaId") Long empresaId);

    @Query("SELECT v FROM Venda v " +
            "LEFT JOIN FETCH v.itens i " +
            "LEFT JOIN FETCH i.produto " +
            "JOIN FETCH v.usuario " +
            "WHERE v.id = :id AND v.empresa.id = :empresaId")
    Optional<Venda> findByIdAndEmpresaIdComDetalhes(@Param("id") Long id, @Param("empresaId") Long empresaId);
}