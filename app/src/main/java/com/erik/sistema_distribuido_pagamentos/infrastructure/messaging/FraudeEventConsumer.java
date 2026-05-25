package com.erik.sistema_distribuido_pagamentos.infrastructure.messaging;

import com.erik.sistema_distribuido_pagamentos.application.port.out.PagamentoRepositoryPort;
import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudeEventConsumer {

    private final PagamentoRepositoryPort repositoryPort;

    @KafkaListener(topics = "pagamento.criado", groupId = "fraude-group")
    public void consumir(Pagamento pagamento) {
        log.info("Iniciando análise de fraude. correlationId: {}, clienteId: {}, valor: {}",
                pagamento.getCorrelationId(), pagamento.getClienteId(), pagamento.getValor());
        try {
            boolean fraude = analisarFraude(pagamento);

            if (fraude) {
                log.warn("Pagamento suspeito detectado! correlationId: {}, valor: {}",
                        pagamento.getCorrelationId(), pagamento.getValor());
                repositoryPort.atualizarStatus(pagamento.getCorrelationId(), "RECUSADO");
            } else {
                log.info("Pagamento aprovado na análise de fraude. correlationId: {}",
                        pagamento.getCorrelationId());
                repositoryPort.atualizarStatus(pagamento.getCorrelationId(), "APROVADO");
            }
        } catch (Exception e) {
            log.error("Erro na análise de fraude. correlationId: {}. Erro: {}",
                    pagamento.getCorrelationId(), e.getMessage(), e);
            throw e;
        }
    }

    private boolean analisarFraude(Pagamento pagamento) {
        return pagamento.getValor().doubleValue() > 10000;
    }
}