package com.example.la_dfe.controller;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;

import com.example.la_dfe.entitites.Empresa;
import com.example.la_dfe.services.EmpresaService;
import com.example.la_dfe.utils.ArquivoUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/empresa")
public class EmpresaController {

    private static final Logger log = LoggerFactory.getLogger(EmpresaController.class);

    @Autowired
    private EmpresaService empresaService;

    @PostMapping("/nova-empresa")
    public ResponseEntity<?> salvarEmpresa(@RequestBody Empresa empresa) {
        try {
            byte[] certificadoBytes = ArquivoUtil.localizaCertificado("C:\\teste\\GR.pfx");

            System.out.println(certificadoBytes);

            empresa.setCertificado(certificadoBytes);

            empresaService.salvar(empresa);
            return ResponseEntity.ok(empresa);
        } catch (Exception e) {
            log.error("Erro ao salvar empresa", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/deletar-empresa/{id}")
    public ResponseEntity<?> deletarEmpresa(@PathVariable("id") Long idEmpresa) {
        try {
            empresaService.deletar(idEmpresa);
            return ResponseEntity.ok().body("Empresa excluída com sucesso");
        } catch (Exception e) {
            log.error("Erro ao deletar empresa", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> listarEmpresas() {
        try {
            return ResponseEntity.ok(empresaService.listar());
        } catch (Exception e) {
            log.error("Erro ao listar empresas", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/empresa/{id}")
    public ResponseEntity<?> listarEmpresas(@PathVariable Long idEmpresa) {
        try {
            return ResponseEntity.ok(empresaService.listarPorId(idEmpresa));
        } catch (Exception e) {
            log.error("Erro ao listar empresas", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
