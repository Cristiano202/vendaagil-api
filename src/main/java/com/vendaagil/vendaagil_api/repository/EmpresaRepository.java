package com.vendaagil.vendaagil_api.repository;

import com.vendaagil.vendaagil_api.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa , Long> {
    Optional<Empresa> findByCnpj(String cnpj);
}
