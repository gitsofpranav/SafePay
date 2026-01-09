package com.safepay.user_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.safepay.user_service.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String>{
    
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
}
