package com.example.la_dfe.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.la_dfe.services.DistribuicaoService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@DisallowConcurrentExecution // Garante que a tarefa não será executada simultaneamente
public class AgendatorConsulta {

    @Autowired
    private DistribuicaoService distribuicaoService;

    @Scheduled(initialDelay = (1000 * 60 * 10), fixedDelay = (1000 * 60 * 60)) // Inicia após 10 minutos e executa a
                                                                               // cada 1 hora
    public void efetuaConsulta() {
        try {
            distribuicaoService.consultaNotas();
            log.info("Agendador de consulta de notas executado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao consultar notas", e);
        }
    }
}
