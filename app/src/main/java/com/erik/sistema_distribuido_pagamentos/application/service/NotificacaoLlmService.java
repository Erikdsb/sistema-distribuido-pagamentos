package com.erik.sistema_distribuido_pagamentos.application.service;

import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificacaoLlmService {

    private static final String PROMPT_V1 = """
            Você é um assistente de notificações de pagamento.
            Gere UMA mensagem curta (máximo 2 frases) para o cliente.
            Nome: %s
            Idade: %d anos
            Status do pagamento: %s
            Tom de comunicação: %s
            Não inclua dados sensíveis (CPF, cartão, conta bancária).
            Responda apenas com o texto da mensagem, sem aspas nem explicações.
            """;

    private static final String MODEL = "gemini-2.5-flash";

    private final Client geminiClient;

    public NotificacaoLlmService(@Value("${gemini.api-key}") String apiKey) {
        this.geminiClient = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String gerarMensagem(Pagamento pagamento) {
        EstiloComunicacao estilo = EstiloComunicacao.porIdade(pagamento.getIdadeCliente());
        String prompt = PROMPT_V1.formatted(
                pagamento.getNomeCliente(),
                pagamento.getIdadeCliente(),
                pagamento.getStatus(),
                estilo.getDescricao()
        );

        try {
            GenerateContentResponse response = geminiClient.models.generateContent(
                    MODEL, prompt, null
            );
            String resposta = response.text();
            if (resposta != null && !resposta.isBlank()) {
                log.info("Mensagem gerada via Gemini. correlationId: {}, estilo: {}, mensagem: {}",
                        pagamento.getCorrelationId(), estilo, resposta.trim());
                return resposta.trim();
            }
        } catch (Exception e) {
            log.warn("Falha na geração via Gemini, usando fallback. correlationId: {}. Erro: {}",
                    pagamento.getCorrelationId(), e.getMessage());
        }

        return mensagemFallback(pagamento, estilo);
    }

    private String mensagemFallback(Pagamento pagamento, EstiloComunicacao estilo) {
        boolean aprovado = "APROVADO".equalsIgnoreCase(pagamento.getStatus());
        String nome = pagamento.getNomeCliente();

        return switch (estilo) {
            case INFORMAL -> aprovado
                    ? "Opa, %s! Seu pagamento foi aprovado 🎉".formatted(nome)
                    : "Eita, %s! Seu pagamento não foi aprovado desta vez.".formatted(nome);
            case SEMI_FORMAL -> aprovado
                    ? "Olá, %s. Seu pagamento foi processado com sucesso.".formatted(nome)
                    : "Olá, %s. Infelizmente seu pagamento não foi aprovado.".formatted(nome);
            case FORMAL -> aprovado
                    ? "Prezado(a) %s, informamos que seu pagamento foi efetuado com sucesso.".formatted(nome)
                    : "Prezado(a) %s, informamos que seu pagamento não foi aprovado.".formatted(nome);
        };
    }
}