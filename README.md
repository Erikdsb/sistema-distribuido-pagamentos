# 💳 Sistema Distribuído de Pagamentos

Plataforma distribuída de pagamentos baseada em microsserviços, mensageria assíncrona e observabilidade moderna, simulando um ambiente corporativo real. O projeto conta com integração de **IA Generativa** utilizando o **Google Gemini** para criação inteligente de notificações personalizadas por perfil de cliente.

---

## 📌 Sobre o Projeto

O sistema foi desenvolvido para simular um fluxo completo de pagamentos em um ambiente distribuído, onde cada responsabilidade é isolada em seu próprio serviço. A comunicação entre os serviços ocorre de forma assíncrona via **Apache Kafka**, garantindo desacoplamento, resiliência e escalabilidade.

A integração com **IA Generativa** é aplicada no serviço de notificações, onde o modelo de linguagem é responsável por humanizar a comunicação com o cliente de acordo com sua faixa etária, sem interferir nas regras críticas de negócio — que permanecem sob total controle do backend.

| Faixa Etária | Estilo de Comunicação | Exemplo |
|---|---|---|
| 18 a 26 anos | Informal | "Opaaa! Seu pagamento foi efetuado 🎉" |
| 27 a 35 anos | Semi-formal | "Seu pagamento foi efetuado com sucesso." |
| 35+ | Formal | "Prezado(a), seu pagamento foi efetuado com sucesso." |

---

## 🏗️ Arquitetura

O sistema utiliza **arquitetura hexagonal** para separação de responsabilidades e comunicação orientada a eventos:

```
Cliente → Gateway → Serviço de Pagamentos → Kafka → Serviço de Fraude → Kafka → Serviço de Notificações (Gemini) → Analytics
```

### Estrutura do Monorepo

```
sistema-distribuido-pagamentos/
├── gateway/
├── servico-pagamentos/
├── servico-fraude/
├── servico-notificacoes/
├── servico-analytics/
├── infraestrutura/
├── observabilidade/
├── shared/
└── docs/
```

---

## 🛠️ Tecnologias

| Categoria | Tecnologias |
|---|---|
| Backend | Java 21, Spring Boot 3.5.6 |
| IA Generativa | Google Gemini 2.5 Flash (google-genai SDK) |
| Mensageria | Apache Kafka |
| Banco de Dados | MariaDB + Redis |
| Observabilidade | Prometheus, Grafana, OpenTelemetry |
| Infraestrutura | Docker, Docker Compose, LocalStack |
| Qualidade | JUnit, Testcontainers, Jacoco |

---

## 🤖 Integração com Google Gemini

O serviço de notificações utiliza o **Google Gemini 2.5 Flash** para gerar mensagens personalizadas por perfil de cliente. A integração é feita diretamente via **Google GenAI SDK para Java**, sem depender do Spring AI.

### Como obter sua API Key (gratuita)

1. Acesse [aistudio.google.com/apikey](https://aistudio.google.com/apikey)
2. Faça login com sua conta Google
3. Clique em **"Create API Key"**
4. Copie a chave gerada

> ⚠️ **Nunca commite sua API Key no repositório.** Use sempre variáveis de ambiente.

### Configurando a variável de ambiente

**Windows (CMD):**
```bash
set GOOGLE_API_KEY=sua_chave_aqui
```

**Windows (PowerShell):**
```powershell
$env:GOOGLE_API_KEY="sua_chave_aqui"
```

**Linux / macOS:**
```bash
export GOOGLE_API_KEY=sua_chave_aqui
```

**IntelliJ IDEA:**
1. Abra **Run/Debug Configurations**
2. Clique em **Modify options** → **Environment variables**
3. Adicione: `GOOGLE_API_KEY=sua_chave_aqui`

### Como está configurado no projeto

No `application.yml`:
```yaml
gemini:
  api-key: ${GOOGLE_API_KEY}
```

No `NotificacaoLlmService`, o cliente é inicializado automaticamente com a chave:
```java
this.geminiClient = Client.builder()
    .apiKey(apiKey)
    .build();
```

### Boas Práticas com IA aplicadas

- ✅ Fallback automático caso o Gemini falhe ou não responda
- ✅ Dados sensíveis (CPF, cartão, conta) nunca enviados ao modelo
- ✅ Prompts versionados (`PROMPT_V1`) para facilitar evolução
- ✅ Logs de latência e resposta para monitoramento
- ✅ Arquitetura permite troca futura de provider (OpenAI, Anthropic, etc.)

---

## 🗄️ Banco de Dados

O projeto utiliza **MariaDB** rodando localmente na porta `3306`.

### Conexão Local

| Parâmetro | Valor |
|---|---|
| Host | `localhost` |
| Porta | `3306` |
| Usuário | `root` |
| Senha | `root` |
| Database | `sistema_distribuido_db` |

String de conexão JDBC:
```
jdbc:mariadb://127.0.0.1:3306/sistema_distribuido_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

### Scripts SQL

Execute os scripts abaixo para criar o banco de dados e as tabelas necessárias:

```sql
CREATE DATABASE IF NOT EXISTS sistema_distribuido_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sistema_distribuido_db;

CREATE TABLE IF NOT EXISTS pagamentos (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    correlation_id CHAR(36),
    nome_cliente   VARCHAR(150) NOT NULL,
    idade_cliente  INT NOT NULL CHECK (idade_cliente >= 0),
    valor          DECIMAL(13,2) NOT NULL CHECK (valor >= 0),
    status         ENUM('APROVADO', 'RECUSADO') NOT NULL DEFAULT 'APROVADO',
    criado_em      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);
```

> ℹ️ O `ddl-auto: update` do Hibernate já cria as tabelas automaticamente ao subir a aplicação. Os scripts acima servem como referência ou para setup manual.

---

## ▶️ Como Rodar o Projeto

### Pré-requisitos

- Java 21
- Maven
- MariaDB rodando na porta `3306`
- Apache Kafka rodando na porta `9092`
- Variável de ambiente `GOOGLE_API_KEY` configurada

### Subindo a aplicação

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/sistema-distribuido-pagamentos.git

# Entre na pasta
cd sistema-distribuido-pagamentos

# Configure a variável de ambiente
export GOOGLE_API_KEY=sua_chave_aqui

# Rode o projeto
./mvnw spring-boot:run
```

### Testando o fluxo completo

```bash
curl -X POST http://localhost:8080/pagamentos \
  -H "Content-Type: application/json" \
  -d '{
    "nomeCliente": "Erik",
    "idadeCliente": 25,
    "valor": 150.00
  }'
```

**Fluxo esperado:**
1. Pagamento criado → evento publicado no Kafka (`pagamento.criado`)
2. Serviço de Fraude consome e aprova/reprova → publica em `pagamento.processado`
3. Serviço de Notificações consome → chama o **Gemini** → gera mensagem personalizada
4. Evento publicado em `notificacao.enviada`

---

## 🎯 Objetivo

Ao final do projeto, o resultado é um sistema distribuído moderno, resiliente, observável e com integração de IA Generativa aplicada em um cenário corporativo real — utilizado como **portfólio técnico avançado**, demonstrando domínio em backend moderno, microsserviços, mensageria, observabilidade e integração com LLMs.
