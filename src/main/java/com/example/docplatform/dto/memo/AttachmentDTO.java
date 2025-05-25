package com.example.docplatform.dto.memo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO {
    private String fileName;
    private String filePath; // можно только имя файла, если нужно скрыть путь
}
