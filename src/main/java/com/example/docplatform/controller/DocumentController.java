package com.example.docplatform.controller;

import com.example.docplatform.dto.document.DocumentDTO;
import com.example.docplatform.enums.DocumentType;
import com.example.docplatform.model.Document;
import com.example.docplatform.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

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




}
