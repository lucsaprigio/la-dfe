package com.example.la_dfe.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.la_dfe.entitites.Empresa;
import com.example.la_dfe.exception.SistemaException;
import com.example.la_dfe.repository.EmpresaRepository;

import br.com.swconsultoria.nfe.util.ObjetoUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public Empresa salvar(Empresa empresa) {
        validar(empresa);

        empresaRepository.findByCpfCnpj(empresa.getCpfCnpj())
                .ifPresent(e -> {
                    throw new SistemaException("Já existe uma empresa cadastrada com este cpf/cnpj");
                });

        return empresaRepository.save(empresa);
    }

    public void deletar(Long id) {
        empresaRepository.deleteById(id);
    }

    public List<Empresa> listar() {
        return empresaRepository.findAll();
    }

    public Empresa listarPorId(Long igEmpresa) {
        return empresaRepository.findById(igEmpresa).orElseThrow(() -> new SistemaException("Empresa não encontrada"));
    }

    public void validar(Empresa empresa) {
        ObjetoUtil.verifica(empresa.getCpfCnpj())
                .orElseThrow(() -> new SistemaException("Campo cpf/cnpj é obrigatório"));
    }
}
