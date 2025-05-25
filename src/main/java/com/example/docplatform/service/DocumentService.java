package com.example.docplatform.service;

import com.example.docplatform.dto.document.DocumentDTO;
import com.example.docplatform.enums.DocumentStatus;
import com.example.docplatform.enums.DocumentType;
import com.example.docplatform.model.Document;
import com.example.docplatform.model.User;
import com.example.docplatform.repository.DocumentRepository;
import com.example.docplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    private final String uploadDir = "uploads/";

    public Document uploadDocument(
            MultipartFile file,
            String documentNumber,
            LocalDate documentDate,
            DocumentType documentType,
            String uploadedByEmail,
            String addressedToEmail
    ) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Ensure directory exists
        Files.createDirectories(Paths.get(uploadDir));

        String originalFilename = file.getOriginalFilename();
        String storagePath = uploadDir + System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = Paths.get(storagePath);
        Files.write(filePath, file.getBytes());

        User uploadedBy = userRepository.findUserByEmail(uploadedByEmail)
                .orElseThrow(() -> new RuntimeException("Uploader not found"));

        User addressedTo = userRepository.findUserByEmail(addressedToEmail)
                .orElse(null); // можно не указывать

        Document document = Document.builder()
                .fileName(originalFilename)
                .filePath(storagePath)
                .documentNumber(documentNumber)
                .documentDate(documentDate)
                .documentType(documentType)
                .uploadDate(LocalDateTime.now())
                .uploadedBy(uploadedBy)
                .addressedTo(addressedTo)
                .status(DocumentStatus.SENT)


                .build();

        return documentRepository.save(document);
    }

    public List<DocumentDTO> getDocumentsByAddressedTo(String addressedToEmail) {
        List<Document> documents = documentRepository.findByAddressedToEmail(addressedToEmail);

        // Преобразование в DTO, чтобы избежать проблем с ленивыми полями
        return documents.stream().map(document -> {
            DocumentDTO dto = new DocumentDTO();
            dto.setFileName(document.getFileName());
            dto.setDocumentNumber(document.getDocumentNumber());
            dto.setFileType(getFileExtension(document.getFileName()));
            dto.setDocumentDate(document.getDocumentDate());
            dto.setUploadDate(document.getUploadDate());
            dto.setUploadedByEmail(document.getUploadedBy().getEmail());
            dto.setAddressedToEmail(
                    document.getAddressedTo() != null ? document.getAddressedTo().getEmail() : null
            );
            dto.setDocumentType(document.getDocumentType().name());
            dto.setStatus(document.getStatus().name());
            return dto;
        }).collect(Collectors.toList());

    }

    public List<DocumentDTO> getDocumentsByUploader(String uploaderEmail) {
        List<Document> documents = documentRepository.findByUploadedByEmail(uploaderEmail);

        return documents.stream().map(document -> {
            DocumentDTO dto = new DocumentDTO();
            dto.setFileName(document.getFileName());
            dto.setFileType(getFileExtension(document.getFileName()));
            dto.setDocumentNumber(document.getDocumentNumber());
            dto.setDocumentDate(document.getDocumentDate());
            dto.setUploadDate(document.getUploadDate());
            dto.setUploadedByEmail(document.getUploadedBy().getEmail());
            dto.setAddressedToEmail(
                    document.getAddressedTo() != null ? document.getAddressedTo().getEmail() : null
            );
            dto.setDocumentType(document.getDocumentType().name());
            dto.setStatus(document.getStatus().name());
            return dto;
        }).collect(Collectors.toList());
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex != -1) ? fileName.substring(dotIndex + 1).toLowerCase() : "";
    }
    public Document getDocumentByNumber(String documentNumber) {
        return documentRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new RuntimeException("Документ не найден"));
    }
    public DocumentDTO toDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setFileName(document.getFileName());
        dto.setFileType(getFileExtension(document.getFileName()));
        dto.setDocumentNumber(document.getDocumentNumber());
        dto.setDocumentDate(document.getDocumentDate());
        dto.setUploadDate(document.getUploadDate());
        dto.setUploadedByEmail(document.getUploadedBy().getEmail());
        dto.setAddressedToEmail(document.getAddressedTo() != null ? document.getAddressedTo().getEmail() : null);
        dto.setDocumentType(document.getDocumentType().name());
        dto.setFilePath(document.getFilePath());
        dto.setStatus(document.getStatus().name());
        return dto;
    }

    public void save(Document doc) {
        documentRepository.save(doc);
    }

    public void replaceFile(Document doc, MultipartFile newFile) throws IOException {
        String newPath = uploadDir + System.currentTimeMillis() + "_" + newFile.getOriginalFilename();
        Path path = Paths.get(newPath);
        Files.createDirectories(path.getParent());
        Files.write(path, newFile.getBytes());

        doc.setFileName(newFile.getOriginalFilename());
        doc.setFilePath(newPath);
    }

}
