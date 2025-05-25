// MemoAttachmentDTO.java
package com.example.docplatform.dto.memo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoAttachmentDTO {
    private Long id;
    private String fileName;
    private String filePath;
}
