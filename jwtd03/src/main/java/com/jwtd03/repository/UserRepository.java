package com.jwtd03.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtd03.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUsername(String username);
}
