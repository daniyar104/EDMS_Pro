package com.example.docplatform.controller;

import com.example.docplatform.dto.document.DocumentDTO;
import com.example.docplatform.enums.DocumentStatus;
import com.example.docplatform.enums.DocumentType;
import com.example.docplatform.model.Document;
import com.example.docplatform.model.User;
import com.example.docplatform.repository.UserRepository;
import com.example.docplatform.service.DocumentAcknowledgementService;
import com.example.docplatform.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.docplatform.model.DocumentAcknowledgement;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentAcknowledgementService acknowledgementService;


    private final UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam String documentNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate documentDate,
            @RequestParam DocumentType documentType,
            @RequestParam String uploadedByEmail,
            @RequestParam(required = false) String addressedToEmail
    ) {
        try {
            Document saved = documentService.uploadDocument(
                    file, documentNumber, documentDate, documentType, uploadedByEmail, addressedToEmail
            );
            return ResponseEntity.ok(saved);
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/addressedTo/{email}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByAddressedTo(@PathVariable String email) {
        List<DocumentDTO> documents = documentService.getDocumentsByAddressedTo(email);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/uploadedBy/{email}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsUploadedBy(@PathVariable String email) {
        List<DocumentDTO> documents = documentService.getDocumentsByUploader(email);
        return ResponseEntity.ok(documents);
    }
    @GetMapping("/{documentNumber}")
    public ResponseEntity<DocumentDTO> getDocumentByNumber(@PathVariable String documentNumber) {
        Document document = documentService.getDocumentByNumber(documentNumber);
        DocumentDTO dto = documentService.toDTO(document);
        return ResponseEntity.ok(dto);
    }




    @PostMapping("/{documentNumber}/accept")
    public ResponseEntity<?> accept(@PathVariable String documentNumber) {
        Document doc = documentService.getDocumentByNumber(documentNumber);
        if (doc.getStatus() != DocumentStatus.SENT && doc.getStatus() != DocumentStatus.UPDATED) {
            return ResponseEntity.badRequest().body("Нельзя принять документ в статусе: " + doc.getStatus());
        }
        doc.setStatus(DocumentStatus.ACCEPTED);
        documentService.save(doc);
        return ResponseEntity.ok("Документ принят");
    }

    @PostMapping("/{documentNumber}/reject")
    public ResponseEntity<?> reject(@PathVariable String documentNumber) {
        Document doc = documentService.getDocumentByNumber(documentNumber);
        if (doc.getStatus() != DocumentStatus.SENT && doc.getStatus() != DocumentStatus.UPDATED) {
            return ResponseEntity.badRequest().body("Нельзя отклонить документ в статусе: " + doc.getStatus());
        }
        doc.setStatus(DocumentStatus.REJECTED);
        documentService.save(doc);
        return ResponseEntity.ok("Документ отклонён на доработку");
    }



    @PostMapping("/{documentNumber}/edit")
    public ResponseEntity<?> editDocument(
            @PathVariable String documentNumber,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String documentNumberNew,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate documentDate,
            @RequestParam DocumentType documentType
    ) throws IOException {
        Document document = documentService.getDocumentByNumber(documentNumber);

        // обновление данных
        document.setDocumentNumber(documentNumberNew);
        document.setDocumentDate(documentDate);
        document.setDocumentType(documentType);
        document.setStatus(DocumentStatus.UPDATED);

        // если файл передан — перезаписываем
        if (file != null && !file.isEmpty()) {
            documentService.replaceFile(document, file);
        }

        documentService.save(document);
        return ResponseEntity.ok("Документ обновлён и повторно отправлен");
    }



    @PostMapping("/{documentNumber}/acknowledge")
    public ResponseEntity<?> acknowledgeDocument(
            @PathVariable String documentNumber,
            @RequestParam String viewerEmail
    ) {
        Document doc = documentService.getDocumentByNumber(documentNumber);
        User viewer = userRepository.findUserByEmail(viewerEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        acknowledgementService.acknowledge(doc, viewer);
        return ResponseEntity.ok("Пользователь ознакомлен с документом");
    }

    @GetMapping("/acknowledged/by-user")
    public ResponseEntity<List<DocumentDTO>> getAcknowledgedDocumentsByUser(
            @RequestParam String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Document> docs = acknowledgementService
                .getAcknowledgedDocuments(user)
                .stream()
                .map(DocumentAcknowledgement::getDocument)
                .toList();

        List<DocumentDTO> dtos = docs.stream()
                .map(documentService::toDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }


}
