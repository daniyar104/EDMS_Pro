package com.example.docplatform.service;

import com.example.docplatform.model.Document;
import com.example.docplatform.model.DocumentActionLog;
import com.example.docplatform.model.User;
import com.example.docplatform.repository.DocumentActionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentActionLogService {

    private final DocumentActionLogRepository repository;

    public void log(Document document, User actor, String action, String description) {
        DocumentActionLog log = DocumentActionLog.builder()
                .document(document)
                .actor(actor)
                .action(action)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();
        repository.save(log);
    }

    public List<DocumentActionLog> getLogs(Document document) {
        return repository.findByDocumentOrderByTimestampAsc(document);
    }
    public List<DocumentActionLog> getLogsForDocument(Document document) {
        return repository.findByDocumentOrderByTimestampAsc(document);
    }



}
