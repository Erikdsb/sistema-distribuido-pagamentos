package com.erik.sistema_distribuido_pagamentos.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequest {

    @NotBlank
    private String clienteId;

    @NotBlank
    private String nomeCliente;

    @NotNull
    private Integer idadeCliente;

    @NotNull
    @Positive
    private BigDecimal valor;
}
