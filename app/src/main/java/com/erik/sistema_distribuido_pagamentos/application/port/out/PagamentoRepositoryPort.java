package com.erik.sistema_distribuido_pagamentos.application.port.out;

import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;

public interface PagamentoRepositoryPort {
    Pagamento salvar(Pagamento pagamento);
    Pagamento buscarPorId(String correlationId);
    void atualizarStatus(String correlationId, String status);
}