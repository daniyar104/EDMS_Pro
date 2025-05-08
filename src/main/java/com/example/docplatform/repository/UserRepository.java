package com.example.docplatform.repository;

import com.example.docplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск пользователя по email
    Optional<User> findUserByEmail(String email);

    // Проверка существования пользователя с таким email
    Boolean existsByEmail(String email);
}
