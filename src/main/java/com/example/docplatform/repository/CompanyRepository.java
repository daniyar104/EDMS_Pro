package com.example.docplatform.repository;

import com.example.docplatform.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCompanyCode(String companyCode);
}