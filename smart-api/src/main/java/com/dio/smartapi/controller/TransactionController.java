package com.dio.smartapi.controller;

import com.dio.smartapi.model.Transaction;
import com.dio.smartapi.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Gerenciamento de transações financeiras")
public class TransactionController {

    private final TransactionService service;

    @GetMapping
    @Operation(summary = "Lista todas as transações")
    public ResponseEntity<List<Transaction>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca transação por ID")
    public ResponseEntity<Transaction> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cria uma nova transação")
    public ResponseEntity<Transaction> create(@Valid @RequestBody Transaction transaction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(transaction));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma transação")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/balance")
    @Operation(summary = "Retorna o saldo financeiro atual")
    public ResponseEntity<Map<String, BigDecimal>> getBalance() {
        return ResponseEntity.ok(Map.of(
                "balance", service.getTotalBalance(),
                "totalIncomes", service.getTotalIncomes(),
                "totalExpenses", service.getTotalExpenses()
        ));
    }
}
