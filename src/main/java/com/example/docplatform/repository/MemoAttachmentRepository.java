package com.example.docplatform.repository;

import com.example.docplatform.model.MemoAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoAttachmentRepository extends JpaRepository<MemoAttachment, Long> {
    List<MemoAttachment> findByMemoId(Long memoId);
}
