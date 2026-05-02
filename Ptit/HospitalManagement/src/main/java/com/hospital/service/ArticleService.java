// service/ArticleService.java
package com.hospital.service;

import com.hospital.entity.Article;
import com.hospital.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public List<Article> getLatestArticles() {
        return articleRepository.findTop3ByOrderByCreatedAtDesc();
    }

    public List<Article> getArticlesByCategory(String category) {
        return articleRepository.findByCategoryOrderByCreatedAtDesc(category);
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }
}