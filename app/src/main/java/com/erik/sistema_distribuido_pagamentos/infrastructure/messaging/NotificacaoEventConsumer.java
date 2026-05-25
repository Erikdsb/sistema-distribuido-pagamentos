package com.erik.sistema_distribuido_pagamentos.infrastructure.messaging;

import com.erik.sistema_distribuido_pagamentos.application.service.NotificacaoService;
import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoEventConsumer {

    private final NotificacaoService notificacaoService;

    @KafkaListener(topics = "pagamento.processado", groupId = "notificacoes-group")
    public void consumir(Pagamento pagamento) {
        log.info("Evento pagamento.processado recebido. correlationId: {}, status: {}",
                pagamento.getCorrelationId(), pagamento.getStatus());
        try {
            notificacaoService.processarPagamento(pagamento);
        } catch (Exception e) {
            log.error("Erro ao processar notificação. correlationId: {}. Erro: {}",
                    pagamento.getCorrelationId(), e.getMessage(), e);
            throw e;
        }
    }
}
