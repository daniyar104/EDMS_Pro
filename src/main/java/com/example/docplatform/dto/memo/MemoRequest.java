package com.example.docplatform.dto.memo;

import lombok.Data;

@Data
public class MemoRequest {
    private String content;
    private Long approverId;
}
