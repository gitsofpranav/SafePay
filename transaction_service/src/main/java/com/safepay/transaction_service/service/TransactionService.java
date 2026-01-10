package com.safepay.transaction_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safepay.transaction_service.entity.Transaction;
import com.safepay.transaction_service.kafka.KafkaEventProducer;
import com.safepay.transaction_service.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    // @Autowired
    // private final ObjectMapper objectMapper;

    @Autowired
    private final KafkaEventProducer kafkaEventProducer;
 
    public Transaction createTransaction(Transaction request){
        System.out.println("🚀 Entered createTransaction()");

        String senderId = request.getSenderName();
        String receiverId = request.getReceiverName();
        Double amount = request.getAmount();

        Transaction transaction = new Transaction();
        transaction.setSenderName(senderId);
        transaction.setReceiverName(receiverId);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");

        System.out.println("📥 Incoming Transaction object: " + transaction);

         Transaction saved = transactionRepository.save(transaction);
        System.out.println("💾 Saved Transaction from DB: " + saved);

        try {
            String key = String.valueOf(saved.getId());
             kafkaEventProducer.sendTransactionEvent(key, saved);
        } catch (Exception e) {
             System.err.println("❌ Failed to send Kafka event: " + e.getMessage());
            e.printStackTrace();
        }
        return saved;

    }

    public List<Transaction> getAllTransaction(){
        return transactionRepository.findAll();
    }
}
