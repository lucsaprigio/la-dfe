package com.example.la_dfe.entitites;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nota_entrada")
@SequenceGenerator(name = "NotaEstradaSeq", sequenceName = "SEQ_NOTA_ENTRADA", allocationSize = 1)
@Data // Criando Getters e Setters
@AllArgsConstructor
@NoArgsConstructor
public class NotaEntrada {

    @Id
    @GeneratedValue(generator = "NotaEstradaSeq", strategy = GenerationType.SEQUENCE)
    private Long id;

    private String schema;

    private String chave;

    private String nomeEmitente;

    private String cnpjEmitente;

    private BigDecimal valor;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] xml;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
}
