package com.example.docplatform.repository;

import com.example.docplatform.dto.company.UserWithCompanyDTO;
import com.example.docplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск пользователя по email
    Optional<User> findUserByEmail(String email);

    // Проверка существования пользователя с таким email
    Boolean existsByEmail(String email);

    @Query("SELECT new com.example.docplatform.dto.company.UserWithCompanyDTO(" +
            "u.email, u.firstName, u.lastName, u.position, c.fullName, c.address) " +
            "FROM User u JOIN u.company c WHERE u.email = :email")
    Optional<UserWithCompanyDTO> findUserWithCompanyInfoByEmail(String email);

}
