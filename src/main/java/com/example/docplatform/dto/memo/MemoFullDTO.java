// MemoFullDTO.java
package com.example.docplatform.dto.memo;

import com.example.docplatform.enums.MemoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MemoFullDTO {
    private Long id;
    private String content;
    private MemoStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String authorName;
    private String approverName;
    private List<MemoAttachmentDTO> attachments;
}
