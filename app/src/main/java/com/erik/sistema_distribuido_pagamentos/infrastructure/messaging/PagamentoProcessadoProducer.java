package com.erik.sistema_distribuido_pagamentos.infrastructure.messaging;

import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagamentoProcessadoProducer {

    private static final String TOPIC = "pagamento.processado";
    private final KafkaTemplate<String, Pagamento> kafkaTemplate;

    public void publicar(Pagamento pagamento) {
        log.info("Publicando evento no tópico '{}'. correlationId: {}, status: {}", TOPIC, pagamento.getCorrelationId(), pagamento.getStatus());
        try {
            kafkaTemplate.send(TOPIC, pagamento.getCorrelationId(), pagamento);
            log.info("Evento publicado com sucesso. correlationId: {}", pagamento.getCorrelationId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento. correlationId: {}. Erro: {}", pagamento.getCorrelationId(), e.getMessage(), e);
            throw e;
        }
    }
}