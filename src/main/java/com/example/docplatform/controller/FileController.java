package com.example.docplatform.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
public class FileController {

    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) throws IOException {
        Path filePath = Paths.get("uploads/memo-attachments").resolve(fileName).normalize();

        System.out.println("📄 Пытаемся открыть файл: " + filePath.toAbsolutePath());

        if (!Files.exists(filePath)) {
            System.out.println("❌ Файл не найден!");
            return ResponseEntity.notFound().build();
        }

        Resource file = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}