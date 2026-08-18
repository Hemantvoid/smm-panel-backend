package com.example.demo.controller;

import com.example.demo.dto.BlogPostResponse;
import com.example.demo.service.BlogPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = "*")
public class BlogController {

    private final BlogPostService blogPostService;

    public BlogController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    // Get all published blog posts
    @GetMapping
    public ResponseEntity<List<BlogPostResponse>> getPublishedPosts() {

        return ResponseEntity.ok(
                blogPostService.getPublishedPosts()
        );
    }

    // Get single published blog post by slug
    @GetMapping("/{slug}")
    public ResponseEntity<BlogPostResponse> getPostBySlug(
            @PathVariable String slug) {

        return ResponseEntity.ok(
                blogPostService.getPublishedPostBySlug(slug)
        );
    }

    // Get published posts by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<BlogPostResponse>> getPostsByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                blogPostService.getPublishedPostsByCategory(category)
        );
    }
}
