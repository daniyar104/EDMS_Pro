package com.example.docplatform.dto.memo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoDTO {
    private Long id;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private List<AttachmentDTO> attachments;
}
