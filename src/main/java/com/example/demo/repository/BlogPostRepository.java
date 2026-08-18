package com.example.demo.repository;

import com.example.demo.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    Optional<BlogPost> findBySlug(String slug);

    List<BlogPost> findByStatusOrderByPublishedAtDesc(String status);

    List<BlogPost> findByCategoryAndStatusOrderByPublishedAtDesc(
            String category,
            String status
    );

    boolean existsBySlug(String slug);
}
