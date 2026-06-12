# 🤖 Smart API — API Inteligente com Reconhecimento de Fala e Spring Boot

Projeto desenvolvido como entrega do bootcamp **Santander 2026 - Java Backend** na plataforma [DIO](https://www.dio.me).

---

## 📋 Sobre o Projeto

API REST inteligente que integra **Spring Boot 3** com **Spring AI** para oferecer:

- 💬 **Chat com IA** — Conversa com GPT-4 via texto com Tool Calling
- 🎙️ **Transcrição de Áudio** — Converte fala em texto usando Whisper (OpenAI)
- 🔊 **Text-to-Speech** — Converte texto em áudio usando TTS (OpenAI)
- 🎯 **Pipeline de Voz** — Fluxo completo: áudio → IA → áudio de resposta
- 💰 **Gestão Financeira** — Registra e consulta transações via comandos de voz ou texto

---

## 🏗️ Arquitetura

```
smart-api/
├── config/
│   └── AiConfig.java              # Configuração ChatClient + Tool Calling
├── controller/
│   ├── AssistantController.java   # Endpoints de IA, transcrição e TTS
│   └── TransactionController.java # CRUD de transações financeiras
├── model/
│   └── Transaction.java           # Entidade JPA
├── repository/
│   └── TransactionRepository.java # Spring Data JPA
└── service/
    ├── AssistantService.java      # ChatModel, Whisper, TTS
    └── TransactionService.java    # Lógica + Tool Calling Functions
```

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.2.5 | Framework |
| Spring AI | 1.0.0-M1 | Integração com IA |
| PostgreSQL | 16 | Banco de dados |
| Docker | - | Infraestrutura |
| OpenAI GPT-4o | - | Chat + Tool Calling |
| OpenAI Whisper | - | Transcrição de áudio |
| OpenAI TTS-1 | - | Text-to-Speech |
| Lombok | - | Redução de boilerplate |
| SpringDoc OpenAPI | 2.5.0 | Documentação |

---

## ▶️ Como Executar

### Pré-requisitos
- Java 21+
- Docker e Docker Compose
- Chave de API da OpenAI

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/smart-api.git
cd smart-api
```

### 2. Suba o banco de dados com Docker
```bash
docker-compose up -d
```

### 3. Configure a chave OpenAI

**Linux/Mac:**
```bash
export OPENAI_API_KEY=sk-sua-chave-aqui
```

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-sua-chave-aqui"
```

Ou edite `src/main/resources/application.properties`:
```properties
spring.ai.openai.api-key=sk-sua-chave-aqui
```

### 4. Execute a aplicação
```bash
./mvnw spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

---

## 📡 Endpoints

### Assistente de IA

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/assistant/chat` | Chat por texto com Tool Calling |
| POST | `/api/assistant/transcribe` | Transcrição de áudio (Whisper) |
| POST | `/api/assistant/speak` | Texto para áudio (TTS) |
| POST | `/api/assistant/voice-command` | Pipeline completo voz → IA → voz |

### Transações

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/transactions` | Lista todas as transações |
| GET | `/api/transactions/{id}` | Busca por ID |
| POST | `/api/transactions` | Cria nova transação |
| DELETE | `/api/transactions/{id}` | Remove transação |
| GET | `/api/transactions/balance` | Saldo atual |

### Documentação
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

---

## 💡 Exemplos de Uso

### Chat por texto
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

---

## 🔧 Como Funciona o Tool Calling

O **Tool Calling** permite que a IA execute funções reais no sistema:

1. Usuário envia mensagem: *"registre uma compra de R$100 no supermercado"*
2. O GPT-4 identifica a intenção e chama a função `registerTransaction`
3. Spring AI executa o bean `registerTransaction` no contexto Spring
4. A transação é salva no PostgreSQL
5. A IA responde confirmando o registro

```java
// Bean exposto como ferramenta para a IA
@Bean
@Description("Registra uma nova transação financeira no sistema")
public Function<TransactionRequest, TransactionResponse> registerTransaction(...) {
    return transactionService.registerTransaction();
}
```

---

## 👨‍💻 Autor

Desenvolvido por **Jhonathan** durante o **Santander 2026 - Java Backend** na DIO.
