package com.erik.sistema_distribuido_pagamentos.infrastructure.messaging;

import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagamentoEventProducer {

    private static final String TOPIC = "pagamento.criado";
    private final KafkaTemplate<String, Pagamento> kafkaTemplate;

    // Garante que o evento só será publicado após a transação ser confirmada
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publicar(Pagamento pagamento) {
        log.info("Publicando evento no tópico '{}'. correlationId: {}", TOPIC, pagamento.getCorrelationId());
        try {
            kafkaTemplate.send(TOPIC, pagamento.getCorrelationId(), pagamento);
            log.info("Evento publicado com sucesso. correlationId: {}", pagamento.getCorrelationId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento. correlationId: {}. Erro: {}", pagamento.getCorrelationId(), e.getMessage(), e);
            throw e;
        }
    }
}