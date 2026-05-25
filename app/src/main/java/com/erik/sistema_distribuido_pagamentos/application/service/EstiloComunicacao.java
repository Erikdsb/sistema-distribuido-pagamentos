package com.erik.sistema_distribuido_pagamentos.application.service;

public enum EstiloComunicacao {
    INFORMAL("informal e descontraído, linguagem jovem, pode usar emoji"),
    SEMI_FORMAL("semi-formal, cordial e profissional, sem gírias"),
    FORMAL("formal e respeitoso, tratamento senhor/senhora, linguagem corporativa");

    private final String descricao;

    EstiloComunicacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static EstiloComunicacao porIdade(int idade) {
        if (idade >= 18 && idade <= 26) {
            return INFORMAL;
        }
        if (idade >= 27 && idade <= 35) {
            return SEMI_FORMAL;
        }
        return FORMAL;
    }
}
