package com.erik.sistema_distribuido_pagamentos.application.service;

import com.erik.sistema_distribuido_pagamentos.domain.Pagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoLlmService {

    // Prompt para quando tiver uma IA melhor, mais avançada, capaz de entender nuances e gerar mensagens mais personalizadas.
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

    private final ChatClient chatClient;

    public String gerarMensagem(Pagamento pagamento) {
        EstiloComunicacao estilo = EstiloComunicacao.porIdade(pagamento.getIdadeCliente());
        String prompt = PROMPT_V1.formatted(
                pagamento.getNomeCliente(),
                pagamento.getIdadeCliente(),
                pagamento.getStatus(),
                estilo.getDescricao()
        );

        try {
            String resposta = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            if (resposta != null && !resposta.isBlank()) {
                log.info("Mensagem gerada via LLM. correlationId: {}, estilo: {}, mensagem: {}",
                        pagamento.getCorrelationId(), estilo, resposta.trim());
                return resposta.trim();
            }
        } catch (Exception e) {
            log.warn("Falha na geração via Ollama, usando fallback. correlationId: {}. Erro: {}",
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
