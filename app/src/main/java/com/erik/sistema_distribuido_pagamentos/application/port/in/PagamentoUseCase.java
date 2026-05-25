package com.erik.sistema_distribuido_pagamentos.application.port.in;

import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import java.math.BigDecimal;

public interface PagamentoUseCase {
    Pagamento criar(String clienteId, BigDecimal valor, String nomeCliente, Integer idadeCliente);
    Pagamento buscarPorId(String correlationId);
}