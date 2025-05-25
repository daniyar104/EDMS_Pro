package com.example.docplatform.repository;

import com.example.docplatform.model.Memo;
import com.example.docplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findByAuthorOrApprover(User author, User approver);
    List<Memo> findByApprover(User approver);

    List<Memo> findTop3ByAuthorOrderByCreatedAtDesc(User author);
}