package com.example.docplatform.repository;
import org.springframework.data.domain.Pageable;


import com.example.docplatform.model.Document;
import com.example.docplatform.model.DocumentActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentActionLogRepository extends JpaRepository<DocumentActionLog, Long> {
    List<DocumentActionLog> findByDocumentOrderByTimestampAsc(Document document);


}
