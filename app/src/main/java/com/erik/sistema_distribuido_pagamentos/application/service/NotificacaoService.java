package com.erik.sistema_distribuido_pagamentos.application.service;

import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import com.erik.sistema_distribuido_pagamentos.infrastructure.messaging.NotificacaoEnviadaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoLlmService notificacaoLlmService;
    private final NotificacaoEnviadaProducer notificacaoEnviadaProducer;

    public void processarPagamento(Pagamento pagamento) {
        log.info("Gerando notificação. correlationId: {}, idade: {}, status: {}",
                pagamento.getCorrelationId(), pagamento.getIdadeCliente(), pagamento.getStatus());

        String mensagem = notificacaoLlmService.gerarMensagem(pagamento);

        Pagamento pagamentoComNotificacao = new Pagamento(
                pagamento.getCorrelationId(),
                pagamento.getClienteId(),
                pagamento.getNomeCliente(),
                pagamento.getIdadeCliente(),
                pagamento.getValor(),
                pagamento.getStatus(),
                pagamento.getCriadoEm(),
                mensagem
        );

        notificacaoEnviadaProducer.publicar(pagamentoComNotificacao);
    }
}
