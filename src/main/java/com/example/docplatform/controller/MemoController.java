package com.example.docplatform.controller;

import com.example.docplatform.dto.memo.MemoRequest;
import com.example.docplatform.enums.MemoStatus;
import com.example.docplatform.model.*;
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


import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/memos")
public class MemoController {

    @Autowired
    private MemoRepository memoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemoService memoService;

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
        return ResponseEntity.ok("Memo created");
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
    public ResponseEntity<List<Memo>> getReceivedMemos(@RequestHeader("X-User-Email") String email) {
        User approver = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Memo> memos = memoRepository.findByApprover(approver);
        return ResponseEntity.ok(memos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Memo> getMemoById(@PathVariable Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));
        return ResponseEntity.ok(memo);
    }

}

