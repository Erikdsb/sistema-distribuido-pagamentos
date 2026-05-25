package com.erik.sistema_distribuido_pagamentos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Entity
@Table(name = "pagamentos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoEntity {

    @Id
    @Column(name = "correlation_id")
    private String correlationId;

    @Column(nullable = false)
    private String clienteId;

    @Column(nullable = false)
    private String nomeCliente;

    @Column(nullable = false)
    private Integer idadeCliente;

    @Column(nullable = false)
    private BigDecimal valor;

    @Setter
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private OffsetDateTime criadoEm;
}