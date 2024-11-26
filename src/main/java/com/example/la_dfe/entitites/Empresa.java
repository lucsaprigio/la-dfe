package com.example.la_dfe.entitites;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@SequenceGenerator(name = "EmpresaSeq", sequenceName = "SEQ_EMPRESA", allocationSize = 1)
@Data // Criando Getters e Setters
@AllArgsConstructor
@NoArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(generator = "EmpresaSeq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "cpf_cnpj")
    private String cpfCnpj;

    @Column(name = "razao_social")
    private String razaoSocial;

    private String uf;

    @Enumerated(EnumType.STRING)
    private AmbienteEnum ambiente;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] certificado;

    @Column(name = "senha_certificado")
    private String senhaCertificado;

    private String nsu;
}
