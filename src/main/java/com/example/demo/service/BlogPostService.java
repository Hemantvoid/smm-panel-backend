package com.example.demo.service;

import com.example.demo.dto.BlogPostRequest;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.dto.BlogPostResponse;
import com.example.demo.model.BlogPost;
import com.example.demo.repository.BlogPostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    // =========================
    // PUBLIC METHODS
    // =========================

    // Get all published blog posts
    public List<BlogPostResponse> getPublishedPosts() {

        return blogPostRepository
                .findByStatusOrderByPublishedAtDesc("PUBLISHED")
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Get published blog post by slug
    public BlogPostResponse getPublishedPostBySlug(String slug) {

        BlogPost blogPost = blogPostRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                new ResourceNotFoundException("Blog post not found"));

        if (!"PUBLISHED".equalsIgnoreCase(blogPost.getStatus())) {
            throw new ResourceNotFoundException("Blog post not found");
        }

        return convertToResponse(blogPost);
    }

    // Get published posts by category
    public List<BlogPostResponse> getPublishedPostsByCategory(
            String category) {

        return blogPostRepository
                .findByCategoryAndStatusOrderByPublishedAtDesc(
                        category,
                        "PUBLISHED"
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================
    // ADMIN METHODS
    // =========================

    // Get all posts including drafts
    public List<BlogPostResponse> getAllPosts() {

        return blogPostRepository
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Get post by ID
    public BlogPostResponse getPostById(Long id) {

        BlogPost blogPost = blogPostRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Blog post not found"));

        return convertToResponse(blogPost);
    }

    // Create blog post
 // Create blog post
    public BlogPostResponse createPost(
            BlogPostRequest request) {

        BlogPost blogPost = new BlogPost();

        blogPost.setTitle(request.getTitle());

        // Generate slug automatically from title
        String slug = generateSlug(request.getTitle());

        // Check duplicate slug
        if (blogPostRepository.existsBySlug(slug)) {
            throw new RuntimeException(
                    "A blog post with this title already exists"
            );
        }

        // IMPORTANT: Set generated slug
        blogPost.setSlug(slug);

        blogPost.setExcerpt(request.getExcerpt());
        blogPost.setContent(request.getContent());
        blogPost.setFeaturedImage(request.getFeaturedImage());
        blogPost.setMetaTitle(request.getMetaTitle());
        blogPost.setMetaDescription(request.getMetaDescription());
        blogPost.setCategory(request.getCategory());
        blogPost.setAuthor(request.getAuthor());

        if ("PUBLISHED".equalsIgnoreCase(request.getStatus())) {

            blogPost.setStatus("PUBLISHED");
            blogPost.setPublishedAt(LocalDateTime.now());

        } else {

            blogPost.setStatus("DRAFT");
        }

        BlogPost savedPost =
                blogPostRepository.save(blogPost);

        return convertToResponse(savedPost);
    }

    // Update blog post
    public BlogPostResponse updatePost(
            Long id,
            BlogPostRequest request) {

        BlogPost existingPost = blogPostRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Blog post not found"
                        ));

        String newSlug =
                generateSlug(request.getTitle());

        // Check if another post already uses this slug
        if (!existingPost.getSlug().equals(newSlug)
                && blogPostRepository.existsBySlug(newSlug)) {

            throw new DuplicateResourceException(
                    "A blog post with this title already exists"
            );
        }

        existingPost.setTitle(request.getTitle());
        existingPost.setSlug(newSlug);
        existingPost.setExcerpt(request.getExcerpt());
        existingPost.setContent(request.getContent());
        existingPost.setFeaturedImage(request.getFeaturedImage());
        existingPost.setMetaTitle(request.getMetaTitle());
        existingPost.setMetaDescription(request.getMetaDescription());
        existingPost.setCategory(request.getCategory());
        existingPost.setAuthor(request.getAuthor());

        if ("PUBLISHED".equalsIgnoreCase(request.getStatus())) {

            existingPost.setStatus("PUBLISHED");

            if (existingPost.getPublishedAt() == null) {
                existingPost.setPublishedAt(
                        LocalDateTime.now()
                );
            }

        } else {

            existingPost.setStatus("DRAFT");
            existingPost.setPublishedAt(null);
        }

        BlogPost updatedPost =
                blogPostRepository.save(existingPost);

        return convertToResponse(updatedPost);
    }

    // Delete blog post
    public void deletePost(Long id) {

    	if (!blogPostRepository.existsById(id)) {
    	    throw new ResourceNotFoundException(
    	            "Blog post not found"
    	    );
    	}

        blogPostRepository.deleteById(id);
    }


    // =========================
    // ENTITY → RESPONSE DTO
    // =========================

    private BlogPostResponse convertToResponse(
            BlogPost blogPost) {

        BlogPostResponse response =
                new BlogPostResponse();

        response.setId(blogPost.getId());
        response.setTitle(blogPost.getTitle());
        response.setSlug(blogPost.getSlug());
        response.setExcerpt(blogPost.getExcerpt());
        response.setContent(blogPost.getContent());
        response.setFeaturedImage(
                blogPost.getFeaturedImage()
        );
        response.setMetaTitle(
                blogPost.getMetaTitle()
        );
        response.setMetaDescription(
                blogPost.getMetaDescription()
        );
        response.setCategory(
                blogPost.getCategory()
        );
        response.setAuthor(
                blogPost.getAuthor()
        );
        response.setStatus(
                blogPost.getStatus()
        );
        response.setPublishedAt(
                blogPost.getPublishedAt()
        );
        response.setCreatedAt(
                blogPost.getCreatedAt()
        );
        response.setUpdatedAt(
                blogPost.getUpdatedAt()
        );

        return response;
    }
    private String generateSlug(String title) {

        return title
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }
}
