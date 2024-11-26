package com.example.la_dfe.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.la_dfe.entitites.NotaEntrada;
import com.example.la_dfe.exception.SistemaException;
import com.example.la_dfe.repository.NotaEntradaRepository;
import com.example.la_dfe.utils.ArquivoUtil;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class NotaEntradaService {

    @Autowired
    private NotaEntradaRepository notaEntradaRepository;

    public NotaEntrada salvar(NotaEntrada notaEntrada) {
        return notaEntradaRepository.save(notaEntrada);
    }

    public void salvarNotas(List<NotaEntrada> notaEntrada) {
        notaEntradaRepository.saveAll(notaEntrada);
    }

    public List<NotaEntrada> listar() {
        return notaEntradaRepository.findAll();
    }

    public NotaEntrada listarPorId(Long idLong) {
        return notaEntradaRepository.findById(idLong)
                .orElseThrow(() -> new SistemaException("Nota de entrada não encontrada"));
    }

    public String getXml(Long idNota) {
        NotaEntrada notaEntrada = listarPorId(idNota);
        // TODO testar descompactar arquivo
        return ArquivoUtil.descompactaXml(notaEntrada.getXml());
    }

    public NotaEntrada getChave(String chave) {
        return notaEntradaRepository.findFirstByChave(chave)
                .orElseThrow(
                        () -> new SistemaException("Nota de entrada não encontrada com a chave informada: " + chave));
    }
}