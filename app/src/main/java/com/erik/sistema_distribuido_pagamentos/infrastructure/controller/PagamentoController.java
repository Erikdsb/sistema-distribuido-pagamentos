package com.erik.sistema_distribuido_pagamentos.infrastructure.controller;

import com.erik.sistema_distribuido_pagamentos.application.port.in.PagamentoUseCase;
import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import com.erik.sistema_distribuido_pagamentos.infrastructure.dto.PagamentoRequest;
import com.erik.sistema_distribuido_pagamentos.infrastructure.dto.PagamentoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {
    private final PagamentoUseCase pagamentoUseCase;

    @PostMapping
    public ResponseEntity<PagamentoResponse> criarPagamento(@Valid @RequestBody PagamentoRequest request) {
        log.info("Iniciando criação de pagamento para clienteId: {}, valor: {}, nomeCliente: {}, idadeCliente: {}",
                request.getClienteId(), request.getValor(), request.getNomeCliente(), request.getIdadeCliente());
        try {
            Pagamento pagamento = pagamentoUseCase.criar(
                    request.getClienteId(),
                    request.getValor(),
                    request.getNomeCliente(),
                    request.getIdadeCliente()
            );
            PagamentoResponse response = new PagamentoResponse(
                    pagamento.getCorrelationId(),
                    pagamento.getClienteId(),
                    pagamento.getNomeCliente(),
                    pagamento.getIdadeCliente(),
                    pagamento.getValor(),
                    pagamento.getStatus(),
                    pagamento.getCriadoEm()
            );
            log.info("Pagamento criado com sucesso. correlationId: {}, status: {}", pagamento.getCorrelationId(), pagamento.getStatus());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao criar pagamento para clienteId: {}. Erro: {}", request.getClienteId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{correlationId}")
    public ResponseEntity<PagamentoResponse> buscarPagamento(@PathVariable String correlationId) {
        log.info("Iniciando busca de pagamento. correlationId: {}", correlationId);
        try {
            Pagamento pagamento = pagamentoUseCase.buscarPorId(correlationId);
            if (pagamento == null) {
                log.info("Pagamento não encontrado. correlationId: {}", correlationId);
                return ResponseEntity.notFound().build();
            }
            PagamentoResponse response = new PagamentoResponse(
                    pagamento.getCorrelationId(),
                    pagamento.getClienteId(),
                    pagamento.getNomeCliente(),
                    pagamento.getIdadeCliente(),
                    pagamento.getValor(),
                    pagamento.getStatus(),
                    pagamento.getCriadoEm()
            );
            log.info("Pagamento encontrado com sucesso. correlationId: {}, status: {}", correlationId, pagamento.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao buscar pagamento. correlationId: {}. Erro: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}