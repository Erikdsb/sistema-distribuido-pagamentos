package com.erik.sistema_distribuido_pagamentos.application.service;

import com.erik.sistema_distribuido_pagamentos.application.port.in.PagamentoUseCase;
import com.erik.sistema_distribuido_pagamentos.application.port.out.PagamentoRepositoryPort;
import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentoService implements PagamentoUseCase {

    private final PagamentoRepositoryPort repositoryPort;

    @Override
    @Transactional
    public Pagamento criar(String clienteId, BigDecimal valor, String nomeCliente, Integer idadeCliente) {
        log.info("Iniciando criação do pagamento. clienteId: {}, nomeCliente: {}, valor: {}", clienteId, nomeCliente, valor);
        try {
            Pagamento pagamento = new Pagamento(
                    UUID.randomUUID().toString(),
                    clienteId,
                    nomeCliente,
                    idadeCliente,
                    valor,
                    "APROVADO",
                    OffsetDateTime.now()
            );
            log.info("Pagamento montado com correlationId: {}, status: {}", pagamento.getCorrelationId(), pagamento.getStatus());

            Pagamento salvo = repositoryPort.salvar(pagamento);
            log.info("Pagamento criado e persistido com sucesso. correlationId: {}, clienteId: {}, valor: {}",
                    salvo.getCorrelationId(), salvo.getClienteId(), salvo.getValor());
            return salvo;
        } catch (Exception e) {
            log.error("Erro ao criar pagamento. clienteId: {}, valor: {}. Erro: {}", clienteId, valor, e.getMessage(), e);
            throw e;
        }
    }

    public Pagamento buscarPorId(String correlationId) {
        log.info("Iniciando busca do pagamento no service. correlationId: {}", correlationId);
        try {
            Pagamento pagamento = repositoryPort.buscarPorId(correlationId);
            if (pagamento == null) {
                log.info("Pagamento não encontrado no service. correlationId: {}", correlationId);
            } else {
                log.info("Pagamento encontrado no service. correlationId: {}, status: {}", correlationId, pagamento.getStatus());
            }
            return pagamento;
        } catch (Exception e) {
            log.error("Erro ao buscar pagamento no service. correlationId: {}. Erro: {}", correlationId, e.getMessage(), e);
            throw e;
        }
    }
}