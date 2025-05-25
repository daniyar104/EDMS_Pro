package com.example.docplatform.dto.documentLogs;

public record ActionLogDTO(
        String actorEmail,
        String description,
        String timestamp
) {
}
