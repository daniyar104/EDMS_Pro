package com.example.docplatform.controller;

import com.example.docplatform.dto.memo.*;
import com.example.docplatform.enums.MemoStatus;
import com.example.docplatform.model.*;
import com.example.docplatform.repository.MemoAttachmentRepository;
import com.example.docplatform.repository.MemoRepository;
import com.example.docplatform.repository.UserRepository;
import com.example.docplatform.service.MemoService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memos")
public class MemoController {

    @Autowired
    private MemoRepository memoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemoService memoService;

    @Autowired
    private MemoAttachmentRepository attachmentRepository;

    @PostMapping
    public ResponseEntity<?> createMemo(
            @RequestBody MemoRequest dto,
            @RequestHeader("X-User-Email") String email) {

        System.out.println(">> Email из заголовка: " + email);
        User author = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        User approver = userRepository.findById(dto.getApproverId())
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        Memo memo = new Memo();
        memo.setAuthor(author);
        memo.setApprover(approver);
        memo.setContent(dto.getContent());
        memo.setCreatedAt(LocalDateTime.now());
        memo.setStatus(MemoStatus.SENT);

        memoRepository.save(memo);
        return ResponseEntity.ok(memo.getId());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadMemoPdf(@PathVariable Long id) throws Exception {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));

        if (memo.getPdfPath() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Path path = Paths.get(memo.getPdfPath());
        if (!Files.exists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource file = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + path.getFileName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }



    @PostMapping("/{id}/approve")
    public ResponseEntity<byte[]> approveMemo(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String email) throws Exception {

        User approver = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));

        if (!memo.getApprover().getId().equals(approver.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        memo.setStatus(MemoStatus.SIGNED);
        memo.setApprovedAt(LocalDateTime.now());

        byte[] pdf = memoService.generateMemoPdf(memo);

        String pdfUploadDir = "uploads/memos/";
        Files.createDirectories(Paths.get(pdfUploadDir));
        String filename = "memo_" + memo.getId() + "_" + System.currentTimeMillis() + ".pdf";
        Path path = Paths.get(pdfUploadDir + filename);
        Files.write(path, pdf);

        memo.setPdfPath(path.toAbsolutePath().toString());
        System.out.println("📄 PDF сохранён по пути: " + path.toAbsolutePath());

        memoRepository.save(memo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=memo.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/received")
    public ResponseEntity<List<MemoDTO>> getReceivedMemos(@RequestHeader("X-User-Email") String email) {
        User approver = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MemoDTO> result = memoRepository.findByApprover(approver).stream()
                .map(memo -> {
                    List<AttachmentDTO> attachments = memo.getAttachments().stream()
                            .map(att -> new AttachmentDTO(att.getFileName(), att.getFilePath()))
                            .toList();

                    return new MemoDTO(
                            memo.getId(),
                            memo.getContent(),
                            memo.getStatus().name(),
                            memo.getCreatedAt(),
                            attachments
                    );
                }).toList();

        return ResponseEntity.ok(result);
    }



    @GetMapping("/{id}")
    public ResponseEntity<MemoFullDTO> getMemoById(@PathVariable Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));

        List<MemoAttachmentDTO> attachmentDTOs = attachmentRepository.findByMemoId(id).stream()
                .map(att -> new MemoAttachmentDTO(att.getId(), att.getFileName(), att.getFilePath()))
                .toList();

        MemoFullDTO dto = new MemoFullDTO(
                memo.getId(),
                memo.getContent(),
                memo.getStatus(),
                memo.getCreatedAt(),
                memo.getApprovedAt(),
                memo.getAuthor().getLastName() + " " + memo.getAuthor().getFirstName(),
                memo.getApprover().getLastName() + " " + memo.getApprover().getFirstName(),
                attachmentDTOs
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/recent/by-user")
    public ResponseEntity<List<Map<String, Object>>> getRecentMemosByUser(
            @RequestHeader("X-User-Email") String email) {

        User author = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Memo> memos = memoRepository.findTop3ByAuthorOrderByCreatedAtDesc(author);

        List<Map<String, Object>> result = memos.stream().map(memo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", memo.getId());
            map.put("content", memo.getContent());
            map.put("createdAt", memo.getCreatedAt());
            map.put("status", memo.getStatus());
            map.put("approver", memo.getApprover().getLastName() + " " + memo.getApprover().getFirstName());
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }



    @PostMapping("/{id}/attachments")
    public ResponseEntity<?> uploadAttachments(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files
    ) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));

        String uploadDir = "uploads/memo-attachments/";
        List<MemoAttachment> attachments = new ArrayList<>();

        try {
            Files.createDirectories(Paths.get(uploadDir));
            for (MultipartFile file : files) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);
                Files.write(path, file.getBytes());

                MemoAttachment attachment = MemoAttachment.builder()
                        .fileName(file.getOriginalFilename())
                        .filePath(path.toAbsolutePath().toString())
                        .memo(memo)
                        .build();

                attachments.add(attachment);
            }
            attachmentRepository.saveAll(attachments);
            return ResponseEntity.ok("Файлы загружены");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при загрузке файлов");
        }
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<MemoAttachmentDTO>> getAttachments(@PathVariable Long id) {
        List<MemoAttachmentDTO> dtos = attachmentRepository.findByMemoId(id)
                .stream()
                .map(att -> new MemoAttachmentDTO(att.getId(), att.getFileName(), att.getFilePath()))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads/memo-attachments/").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}

