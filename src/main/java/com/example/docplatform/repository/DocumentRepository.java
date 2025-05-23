package com.example.docplatform.repository;

import com.example.docplatform.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByAddressedToEmail(String addressedToEmail);
    List<Document> findByUploadedByEmail(String email);
    Optional<Document> findByDocumentNumber(String documentNumber);

}
