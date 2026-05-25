package com.erik.sistema_distribuido_pagamentos.infrastructure.persistence;

import com.erik.sistema_distribuido_pagamentos.application.port.out.PagamentoRepositoryPort;
import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagamentoRepositoryAdapter implements PagamentoRepositoryPort {

    private final PagamentoJpaRepository jpaRepository;

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        log.info("Iniciando persistência do pagamento. correlationId: {}, clienteId: {}, valor: {}",
                pagamento.getCorrelationId(), pagamento.getClienteId(), pagamento.getValor());
        try {
            PagamentoEntity entity = new PagamentoEntity(
                    pagamento.getCorrelationId(),
                    pagamento.getClienteId(),
                    pagamento.getNomeCliente(),
                    pagamento.getIdadeCliente(),
                    pagamento.getValor(),
                    pagamento.getStatus(),
                    pagamento.getCriadoEm()
            );
            PagamentoEntity saved = jpaRepository.save(entity);
            log.info("Pagamento persistido com sucesso. correlationId: {}, status: {}",
                    saved.getCorrelationId(), saved.getStatus());
            return new Pagamento(
                    saved.getCorrelationId(),
                    saved.getClienteId(),
                    saved.getNomeCliente(),
                    saved.getIdadeCliente(),
                    saved.getValor(),
                    saved.getStatus(),
                    saved.getCriadoEm()
            );
        } catch (Exception e) {
            log.error("Erro ao persistir pagamento. correlationId: {}. Erro: {}",
                    pagamento.getCorrelationId(), e.getMessage(), e);
            throw e;
        }
    }

    public Pagamento buscarPorId(String correlationId) {
        log.info("Iniciando busca no banco de dados. correlationId: {}", correlationId);
        try {
            Pagamento pagamento = jpaRepository.findById(correlationId)
                    .map(entity -> new Pagamento(
                            entity.getCorrelationId(),
                            entity.getClienteId(),
                            entity.getNomeCliente(),
                            entity.getIdadeCliente(),
                            entity.getValor(),
                            entity.getStatus(),
                            entity.getCriadoEm()
                    ))
                    .orElse(null);

            if (pagamento == null) {
                log.info("Pagamento não encontrado no banco de dados. correlationId: {}", correlationId);
            } else {
                log.info("Pagamento encontrado no banco de dados. correlationId: {}, status: {}",
                        correlationId, pagamento.getStatus());
            }
            return pagamento;
        } catch (Exception e) {
            log.error("Erro ao buscar pagamento no banco de dados. correlationId: {}. Erro: {}",
                    correlationId, e.getMessage(), e);
            throw e;
        }
    }
}