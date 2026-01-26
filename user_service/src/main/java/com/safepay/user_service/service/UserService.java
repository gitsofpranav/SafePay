package com.safepay.user_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safepay.user_service.entity.User;
import com.safepay.user_service.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        // try {
        //     CreateWaletRequest request = new CreateWallextRequest();
        //     request.setUserId(savedUser.getId());
        // } catch (Exception e) {
        //     // TODO: handle exception
        // }
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
                
    }

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
            
    }

}
