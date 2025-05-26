package com.example.docplatform.webSoket;

public record MemoNotificationDTO(
        Long id,
        String content,
        String from,
        String to,
        String createdAt
) {}