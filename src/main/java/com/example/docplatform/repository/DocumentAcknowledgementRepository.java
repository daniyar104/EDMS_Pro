package com.example.docplatform.repository;


import com.example.docplatform.model.Document;
import com.example.docplatform.model.DocumentAcknowledgement;
import com.example.docplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentAcknowledgementRepository extends JpaRepository<DocumentAcknowledgement, Long> {

    boolean existsByDocumentAndViewer(Document document, User viewer);

    Optional<DocumentAcknowledgement> findByDocumentAndViewer(Document document, User viewer);

    List<DocumentAcknowledgement> findAllByViewer(User viewer);

    List<DocumentAcknowledgement> findAllByDocument(Document document);

    void deleteByDocumentAndViewer(Document document, User viewer);
}