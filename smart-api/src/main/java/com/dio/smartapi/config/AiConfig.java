package com.dio.smartapi.config;

import com.dio.smartapi.service.TransactionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class AiConfig {

    /**
     * Configura o ChatClient com o modelo OpenAI.
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        return ChatClient.builder(model).build();
    }

    /**
     * Tool Calling: Bean que a IA pode chamar para registrar transações.
     * O nome do bean ("registerTransaction") é usado no .functions() do ChatClient.
     */
    @Bean
    @Description("Registra uma nova transação financeira no sistema. Use quando o usuário pedir para registrar, adicionar ou lançar uma receita ou despesa.")
    public Function<TransactionService.TransactionRequest, TransactionService.TransactionResponse> registerTransaction(
            TransactionService transactionService) {
        return transactionService.registerTransaction();
    }

    /**
     * Tool Calling: Bean que a IA pode chamar para consultar o saldo.
     * O nome do bean ("getBalance") é usado no .functions() do ChatClient.
     */
    @Bean
    @Description("Consulta o saldo financeiro atual, total de receitas e total de despesas.")
    public Function<Void, TransactionService.BalanceResponse> getBalance(
            TransactionService transactionService) {
        return transactionService.getBalance();
    }
}
