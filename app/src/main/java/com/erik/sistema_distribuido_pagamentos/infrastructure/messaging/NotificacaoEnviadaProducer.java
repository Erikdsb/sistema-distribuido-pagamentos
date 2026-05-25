package com.erik.sistema_distribuido_pagamentos.infrastructure.messaging;

import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoEnviadaProducer {

    private static final String TOPIC = "notificacao.enviada";
    private final KafkaTemplate<String, Pagamento> kafkaTemplate;

    public void publicar(Pagamento pagamento) {
        log.info("Publicando evento no tópico '{}'. correlationId: {}",
                TOPIC, pagamento.getCorrelationId());
        try {
            kafkaTemplate.send(TOPIC, pagamento.getCorrelationId(), pagamento);
            log.info("Notificação publicada com sucesso. correlationId: {}", pagamento.getCorrelationId());
        } catch (Exception e) {
            log.error("Erro ao publicar notificação. correlationId: {}. Erro: {}",
                    pagamento.getCorrelationId(), e.getMessage(), e);
            throw e;
        }
    }
}
