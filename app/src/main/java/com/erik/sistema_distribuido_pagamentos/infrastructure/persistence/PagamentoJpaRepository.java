package com.erik.sistema_distribuido_pagamentos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoJpaRepository extends JpaRepository<PagamentoEntity, String> {
}