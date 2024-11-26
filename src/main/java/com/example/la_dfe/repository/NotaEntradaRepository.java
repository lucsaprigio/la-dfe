package com.example.la_dfe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.la_dfe.entitites.NotaEntrada;

@Repository
public interface NotaEntradaRepository extends JpaRepository<NotaEntrada, Long> {

    Optional<NotaEntrada> findFirstByChave(String chave);
}
