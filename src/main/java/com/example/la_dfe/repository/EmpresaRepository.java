package com.example.la_dfe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.la_dfe.entitites.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByCpfCnpj(String cpfCnpj);
}
