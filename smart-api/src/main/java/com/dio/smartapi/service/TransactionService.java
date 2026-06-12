package com.dio.smartapi.service;

import com.dio.smartapi.model.Transaction;
import com.dio.smartapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    // ========================================================
    // CRUD padrão
    // ========================================================

    public Transaction save(Transaction transaction) {
        log.info("Salvando transação: {}", transaction.getDescription());
        return repository.save(transaction);
    }

    public List<Transaction> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Transaction findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com id: " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public BigDecimal getTotalBalance() {
        BigDecimal incomes = repository.sumIncomes();
        BigDecimal expenses = repository.sumExpenses();
        return incomes.subtract(expenses);
    }

    public BigDecimal getTotalIncomes() {
        return repository.sumIncomes();
    }

    public BigDecimal getTotalExpenses() {
        return repository.sumExpenses();
    }

    // ========================================================
    // Tool Calling Functions — usadas pelo Spring AI
    // ========================================================

    /**
     * Função chamada pela IA para registrar uma nova transação.
     */
    @Description("Registra uma nova transação financeira (receita ou despesa)")
    public Function<TransactionRequest, TransactionResponse> registerTransaction() {
        return request -> {
            Transaction transaction = Transaction.builder()
                    .description(request.description())
                    .amount(new BigDecimal(request.amount()))
                    .type(Transaction.TransactionType.valueOf(request.type().toUpperCase()))
                    .build();
            Transaction saved = repository.save(transaction);
            log.info("IA registrou transação via Tool Calling: {}", saved);
            return new TransactionResponse(
                    saved.getId(),
                    "Transação registrada com sucesso! ID: " + saved.getId()
            );
        };
    }

    /**
     * Função chamada pela IA para consultar o saldo atual.
     */
    @Description("Consulta o saldo financeiro atual")
    public Function<Void, BalanceResponse> getBalance() {
        return ignored -> {
            BigDecimal balance = getTotalBalance();
            BigDecimal incomes = getTotalIncomes();
            BigDecimal expenses = getTotalExpenses();
            return new BalanceResponse(balance, incomes, expenses);
        };
    }

    // ========================================================
    // Records auxiliares para Tool Calling
    // ========================================================

    public record TransactionRequest(String description, String amount, String type) {}
    public record TransactionResponse(Long id, String message) {}
    public record BalanceResponse(BigDecimal balance, BigDecimal totalIncomes, BigDecimal totalExpenses) {}
}
