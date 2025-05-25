package com.example.docplatform.service;

import com.example.docplatform.model.Document;
import com.example.docplatform.model.DocumentAcknowledgement;
import com.example.docplatform.model.User;
import com.example.docplatform.repository.DocumentAcknowledgementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentAcknowledgementService {

    private final DocumentAcknowledgementRepository acknowledgementRepository;

    // Ознакомить пользователя с документом
    public void acknowledge(Document document, User viewer) {
        boolean exists = acknowledgementRepository.existsByDocumentAndViewer(document, viewer);
        if (!exists) {
            DocumentAcknowledgement ack = DocumentAcknowledgement.builder()
                    .document(document)
                    .viewer(viewer)
                    .acknowledgedAt(LocalDateTime.now())
                    .build();
            acknowledgementRepository.save(ack);
        }
    }

    // Получить всех, кто ознакомлен с данным документом
    public List<DocumentAcknowledgement> getAcknowledgedUsers(Document document) {
        return acknowledgementRepository.findAllByDocument(document);
    }

    // Получить все документы, с которыми ознакомлен конкретный пользователь
    public List<DocumentAcknowledgement> getAcknowledgedDocuments(User viewer) {
        return acknowledgementRepository.findAllByViewer(viewer);
    }

    // Удалить ознакомление (по желанию)
    public void removeAcknowledgement(Document document, User viewer) {
        acknowledgementRepository.deleteByDocumentAndViewer(document, viewer);
    }
}

