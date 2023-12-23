package com.fuel_spring_server.user.repository;

import com.fuel_spring_server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
