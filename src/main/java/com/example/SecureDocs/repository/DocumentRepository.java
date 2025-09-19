package com.example.SecureDocs.repository;

import com.example.SecureDocs.model.Document;
import com.example.SecureDocs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserId(Long userId);
    Optional<Document> findByIdAndUserId(Long id, Long userId);
    List<Document> findByUserIdAndFileNameContainingIgnoreCase(Long userId, String fileName);

    List<Document> findByUser(User user);

    List<Document> findByFileNameContainingIgnoreCaseAndUser(String fileName, User user);
}