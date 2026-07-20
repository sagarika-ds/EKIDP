package com.ekidp.knowledge.repository;

import com.ekidp.knowledge.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByDepartment(String department);

    List<Document> findByUploadedBy(String uploadedBy);

    List<Document> findByCategory(String category);

    List<Document> findByStatus(String status);

    @Query("SELECT d FROM Document d WHERE " +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.extractedText) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Document> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT d FROM Document d WHERE d.department = :department AND d.status = 'ACTIVE'")
    List<Document> findActiveByDepartment(@Param("department") String department);

    long countByDepartment(String department);

    long countByFileType(String fileType);
}
