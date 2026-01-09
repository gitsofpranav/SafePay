package com.safepay.transaction_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safepay.transaction_service.entity.Transaction;
import com.safepay.transaction_service.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private final ObjectMapper objectMapper;

    public Transaction createTransaction(Transaction transaction){
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");
        return transactionRepository.save(transaction);

    }

    public List<Transaction> getAllTransaction(){
        return transactionRepository.findAll();
    }
}
