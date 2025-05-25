package com.example.docplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "memo_attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemoAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "memo_id")
    private Memo memo;
}
