package com.erik.sistema_distribuido_pagamentos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Entity
@Table(name = "pagamentos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoEntity {

    @Id
    private String correlationId;

    @Column(nullable = false)
    private String clienteId;

    @Column(nullable = false)
    private String nomeCliente;

    @Column(nullable = false)
    private Integer idadeCliente;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private OffsetDateTime criadoEm;
}