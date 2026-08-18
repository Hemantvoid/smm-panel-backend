package com.example.demo.controller;

import com.example.demo.dto.BlogPostRequest;
import com.example.demo.dto.BlogPostResponse;
import com.example.demo.service.BlogPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/admin/blog")
public class AdminBlogController {

    private final BlogPostService blogPostService;

    public AdminBlogController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    // Get all blog posts - including drafts
    @GetMapping
    public ResponseEntity<List<BlogPostResponse>> getAllPosts() {

        return ResponseEntity.ok(
                blogPostService.getAllPosts()
        );
    }

    // Get blog post by ID
    @GetMapping("/{id}")
    public ResponseEntity<BlogPostResponse> getPostById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                blogPostService.getPostById(id)
        );
    }

    // Create new blog post
    @PostMapping
    public ResponseEntity<BlogPostResponse> createPost(
            @Valid @RequestBody BlogPostRequest request) {

        return ResponseEntity.ok(
                blogPostService.createPost(request)
        );
    }

    // Update blog post
    @PutMapping("/{id}")
    public ResponseEntity<BlogPostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody BlogPostRequest request) {

        return ResponseEntity.ok(
                blogPostService.updatePost(id, request)
        );
    }

    // Delete blog post
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id) {

        blogPostService.deletePost(id);

        return ResponseEntity.ok(
                "Blog post deleted successfully"
        );
    }
}
