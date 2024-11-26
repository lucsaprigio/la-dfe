package com.example.la_dfe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.la_dfe.services.DistribuicaoService;
import com.example.la_dfe.services.NotaEntradaService;

import br.com.swconsultoria.nfe.exception.NfeException;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/nota-entrada")
@Slf4j
public class NotaEntradaCotroller {

    @Autowired
    public NotaEntradaService notaEntradaService;

    @Autowired
    public DistribuicaoService distribuicaoService;

    @GetMapping("/consultar")
    public ResponseEntity<?> listarNotas() throws NfeException {
        try {
            distribuicaoService
                    .consultaNotas();
            return ResponseEntity.ok(notaEntradaService.listar());
        } catch (Exception e) {
            log.error("Erro ao listar notas", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/xml/{id}")
    public ResponseEntity<?> getXml(@PathVariable("id") Long idNota) {
        try {
            return ResponseEntity.ok(notaEntradaService.getXml(idNota));
        } catch (Exception e) {
            log.error("Erro ao buscar xml", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
