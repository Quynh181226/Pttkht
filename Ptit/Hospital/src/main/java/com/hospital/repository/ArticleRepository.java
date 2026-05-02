package com.hospital.repository;

import com.hospital.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findTop3ByOrderByCreatedAtDesc();
    List<Article> findByCategoryOrderByCreatedAtDesc(String category);
}