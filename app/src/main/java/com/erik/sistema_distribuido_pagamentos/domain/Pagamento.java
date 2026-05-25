package com.erik.sistema_distribuido_pagamentos.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento {
    private String correlationId;
    private String clienteId;
    private String nomeCliente;
    private Integer idadeCliente;
    private BigDecimal valor;
    private String status;
    private OffsetDateTime criadoEm;
}