# 💳 Sistema Distribuído de Pagamentos

Plataforma distribuída de pagamentos baseada em microsserviços, mensageria assíncrona e observabilidade moderna, simulando um ambiente corporativo real. O projeto conta com integração de **IA Generativa** utilizando LLMs para criação inteligente de notificações personalizadas por perfil de cliente.

---

## 📌 Sobre o Projeto

O sistema foi desenvolvido para simular um fluxo completo de pagamentos em um ambiente distribuído, onde cada responsabilidade é isolada em seu próprio serviço. A comunicação entre os serviços ocorre de forma assíncrona via **Apache Kafka**, garantindo desacoplamento, resiliência e escalabilidade.

A integração com **IA Generativa** é aplicada no serviço de notificações, onde o modelo de linguagem é responsável por humanizar a comunicação com o cliente de acordo com sua faixa etária, sem interferir nas regras críticas de negócio — que permanecem sob total controle do backend.

| Faixa Etária | Estilo de Comunicação | Exemplo |
|---|---|---|
| 18 a 26 anos | Informal | "Opaaa! Seu pagamento foi efetuado 🎉" |
| 27 a 35 anos | Semi-formal | "Seu pagamento foi efetuado com sucesso." |
| 35+ | Formal | "Olá senhor(a), seu pagamento foi efetuado com sucesso." |

---

## 🏗️ Arquitetura

O sistema utiliza **arquitetura hexagonal** para separação de responsabilidades e comunicação orientada a eventos:

```
Cliente → Gateway → Serviço de Pagamentos → Kafka → Serviço de Fraude → Kafka → Serviço de Notificações (LLM) → Analytics
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
| Backend | Java 17+, Spring Boot, Spring AI |
| Mensageria | Apache Kafka |
| Banco de Dados | MySQL + Redis |
| Observabilidade | Prometheus, Grafana, OpenTelemetry |
| Infraestrutura | Docker, Docker Compose, LocalStack |
| Qualidade | JUnit, Testcontainers, Jacoco |

---

## 🗄️ Banco de Dados

### Conexão Local

Ao subir o ambiente com Docker Compose, o MySQL ficará disponível na seguinte configuração:

| Parâmetro | Valor |
|---|---|
| Host | `localhost` |
| Porta | `3306` |
| Usuário | `root` |
| Senha | definida no `docker-compose.yml` (ex: `root` ou `senha123`) |
| Database | `sistema_distribuido_db` |

> ⚠️ Confira as variáveis `MYSQL_ROOT_PASSWORD` e `MYSQL_DATABASE` no seu `docker-compose.yml` para confirmar os valores exatos utilizados no seu ambiente.

Exemplo de string de conexão JDBC:

```
jdbc:mysql://localhost:3306/sistema_distribuido_db?useSSL=false&serverTimezone=UTC
```

---

### Scripts SQL

Execute os scripts abaixo para criar o banco de dados e as tabelas necessárias:

```sql
CREATE DATABASE sistema_distribuido_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sistema_distribuido_db;

CREATE TABLE pagamentos (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    correlation_id CHAR(36),                          -- UUID no formato textual
    cliente_id     BIGINT NOT NULL,
    nome_cliente   VARCHAR(150) NOT NULL,
    idade_cliente  INT NOT NULL CHECK (idade_cliente >= 0),
    valor          DECIMAL(13,2) NOT NULL CHECK (valor >= 0),
    status         ENUM('APROVADO', 'RECUSADO') NOT NULL DEFAULT 'APROVADO',
    criado_em      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);
```

---

## 🤖 Boas Práticas com IA

- Implementar fallback caso a IA falhe
- Não enviar dados sensíveis ao modelo
- Controlar tokens e limites de resposta
- Monitorar latência e custo das chamadas
- Versionar prompts
- Permitir troca futura de provider (OpenAI, Ollama, Anthropic)

---

## 🎯 Objetivo

Ao final do projeto, o resultado é um sistema distribuído moderno, resiliente, observável e com integração de IA Generativa aplicada em um cenário corporativo real — utilizado como **portfólio técnico avançado**, demonstrando domínio em backend moderno, microsserviços, mensageria, observabilidade e integração com LLMs.