package com.example.docplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_acknowledgements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentAcknowledgement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "viewer_id")
    private User viewer;

    private LocalDateTime acknowledgedAt; // можно обновлять, если нужно
}
