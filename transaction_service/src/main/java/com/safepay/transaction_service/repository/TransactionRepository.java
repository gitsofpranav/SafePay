package com.safepay.transaction_service.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.safepay.transaction_service.entity.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction,String> {
    
    
}
