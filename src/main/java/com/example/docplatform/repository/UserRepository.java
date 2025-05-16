package com.example.docplatform.repository;

import com.example.docplatform.dto.company.UserWithCompanyDTO;
import com.example.docplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("SELECT new com.example.docplatform.dto.company.UserWithCompanyDTO(" +
            "u.email, u.firstName, u.lastName, u.position, c.fullName, c.address, c.companyCode) " +
            "FROM User u JOIN u.company c WHERE u.email = :email")
    Optional<UserWithCompanyDTO> findUserWithCompanyInfoByEmail(String email);

    List<User> findAllByIndividualCompanyNumber(String companyNumber);

}
