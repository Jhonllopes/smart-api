# 🤖 Smart API — API Inteligente com Reconhecimento de Fala e Spring Boot

Projeto desenvolvido como entrega do bootcamp **Santander 2026 - Java Backend** na plataforma [DIO](https://www.dio.me).

---

## 📋 Sobre o Projeto

API REST inteligente que integra **Spring Boot 3** com **Spring AI** para oferecer um assistente financeiro capaz de interpretar comandos de voz, registrar transações e responder em áudio.

### Funcionalidades
- 💬 **Chat com IA** — Conversa com GPT-4o via texto com Tool Calling
- 🎙️ **Transcrição de Áudio** — Converte fala em texto usando Whisper (OpenAI)
- 🔊 **Text-to-Speech** — Converte texto em áudio usando TTS (OpenAI)
- 🎯 **Pipeline de Voz** — Fluxo completo: áudio → IA → áudio de resposta
- 💰 **Gestão Financeira** — Registra e consulta transações via comandos de voz ou texto

---

## 🏗️ Arquitetura

```
smart-api/
├── src/main/java/com/dio/smartapi/
│   ├── config/
│   │   └── AiConfig.java              # Configuração ChatClient + Tool Calling
│   ├── controller/
│   │   ├── AssistantController.java   # Endpoints de IA, transcrição e TTS
│   │   └── TransactionController.java # CRUD de transações financeiras
│   ├── model/
│   │   └── Transaction.java           # Entidade JPA
│   ├── repository/
│   │   └── TransactionRepository.java # Spring Data JPA
│   ├── service/
│   │   ├── AssistantService.java      # ChatModel, Whisper, TTS
│   │   └── TransactionService.java    # Lógica + Tool Calling Functions
│   └── SmartApiApplication.java       # Classe principal
└── src/main/resources/
    └── application.properties         # Configurações da aplicação
```

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.2.5 | Framework principal |
| Spring AI | 1.0.0-M1 | Integração com IA |
| Spring Data JPA | - | Persistência de dados |
| H2 Database | - | Banco de dados em memória |
| OpenAI GPT-4o | - | Chat + Tool Calling |
| OpenAI Whisper | - | Transcrição de áudio |
| OpenAI TTS-1 | - | Text-to-Speech |
| Lombok | - | Redução de boilerplate |
| SpringDoc OpenAPI | 2.5.0 | Documentação Swagger |

---

## ▶️ Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.9+
- Chave de API da OpenAI (opcional para testar endpoints de transação)

### 1. Clone o repositório
```bash
git clone https://github.com/Jhonllopes/smart-api.git
cd smart-api
```

### 2. Configure a chave OpenAI (opcional)

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-sua-chave-aqui"
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY=sk-sua-chave-aqui
```

### 3. Execute a aplicação
```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

> ⚠️ Sem a chave OpenAI, os endpoints de IA retornam erro, mas os endpoints de transação funcionam normalmente.

---

## 📡 Endpoints

### 🤖 Assistente de IA

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/assistant/chat` | Chat por texto com Tool Calling |
| POST | `/api/assistant/transcribe` | Transcrição de áudio (Whisper) |
| POST | `/api/assistant/speak` | Texto para áudio (TTS) |
| POST | `/api/assistant/voice-command` | Pipeline completo voz → IA → voz |

### 💰 Transações

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/transactions` | Lista todas as transações |
| GET | `/api/transactions/{id}` | Busca por ID |
| POST | `/api/transactions` | Cria nova transação |
| DELETE | `/api/transactions/{id}` | Remove transação |
| GET | `/api/transactions/balance` | Saldo atual |

### 📖 Documentação
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`
- **H2 Console:** `http://localhost:8080/h2-console`

---

## 💡 Exemplos de Uso

### Chat por texto com Tool Calling
```bash
curl -X POST http://localhost:8080/api/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "registre uma despesa de R$50 de almoço"}'
```

### Consultar saldo via IA
```bash
curl -X POST http://localhost:8080/api/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "qual é meu saldo atual?"}'
```

### Criar transação diretamente
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Salário",
    "amount": 5000.00,
    "type": "INCOME"
  }'
```

### Consultar saldo
```bash
curl http://localhost:8080/api/transactions/balance
```

---

## 🔧 Como Funciona o Tool Calling

O **Tool Calling** permite que a IA execute funções reais no sistema automaticamente:

1. Usuário envia mensagem: *"registre uma compra de R$100 no supermercado"*
2. O GPT-4o identifica a intenção e decide chamar a função `registerTransaction`
3. Spring AI executa o bean correspondente no contexto Spring
4. A transação é salva no banco de dados H2
5. A IA responde ao usuário confirmando o registro

```java
@Bean
@Description("Registra uma nova transação financeira no sistema")
public Function<TransactionRequest, TransactionResponse> registerTransaction(
        TransactionService transactionService) {
    return transactionService.registerTransaction();
}
```

---

## 🔊 Pipeline de Voz Completo

```
Usuário fala → [Arquivo de áudio]
      ↓
Whisper API → [Texto transcrito]
      ↓
GPT-4o + Tool Calling → [Executa ação + gera resposta]
      ↓
TTS API → [Áudio de resposta]
      ↓
Usuário ouve a resposta
```

---

## 👨‍💻 Autor

Desenvolvido por **Jhonathan Lopes** durante o **Santander 2026 - Java Backend** na DIO.

[![GitHub](https://img.shields.io/badge/GitHub-Jhonllopes-black?style=flat&logo=github)](https://github.com/Jhonllopes)
