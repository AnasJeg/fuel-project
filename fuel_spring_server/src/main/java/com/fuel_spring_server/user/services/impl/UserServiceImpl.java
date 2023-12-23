package com.fuel_spring_server.user.services.impl;

import com.fuel_spring_server.user.domain.User;
import com.fuel_spring_server.user.repository.UserRepository;
import com.fuel_spring_server.user.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public User saveUser(User user) {
        log.info("Save user : {}",user);
        userRepository.save(user);
        return user;
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        log.info("Get all users page {} size {}",pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable);
    }

    @Override
    public User updateUser(User user) {
        log.info("Updating user with id {}: {}", user.getId(), user.getNom());

        Optional<User> check = userRepository.findById(user.getId());
        if (check.isPresent()) {
            return userRepository.saveAndFlush(user);
        } else {
            log.warn("User with id {} not found", user.getId());
            return null;
        }
    }

    @Override
    public User getUserById(Long id) {
        if(userRepository.existsById(id)){
            log.info("User with id {}",id);
            return userRepository.findById(id).get();
        }else{
            log.warn("User with id {} not found",id);
            return null;
        }
    }

    @Override
    public Boolean deleteUser(User user) {
        if(userRepository.existsById(user.getId())){
            log.info("User {} with id = {}",user.getNom(),user.getId());
            userRepository.delete(user);
            return true;
        }
        return false;
    }
}
