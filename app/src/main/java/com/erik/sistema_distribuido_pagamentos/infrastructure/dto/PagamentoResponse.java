package com.erik.sistema_distribuido_pagamentos.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponse {

    private String correlationId;
    private String clienteId;
    private String nomeCliente;
    private Integer idadeCliente;
    private BigDecimal valor;
    private String status;
    private OffsetDateTime criadoEm;
}