package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class BlogPostRequest {

	@NotBlank(message = "Title is required")
	@Size(max = 200, message = "Title cannot exceed 200 characters")
	private String title;

	@Size(max = 250, message = "Slug cannot exceed 250 characters")
	private String slug;

	@Size(max = 500, message = "Excerpt cannot exceed 500 characters")
	private String excerpt;

	@NotBlank(message = "Content is required")
	private String content;

	@Size(max = 500, message = "Featured image URL cannot exceed 500 characters")
	private String featuredImage;

	@Size(max = 200, message = "Meta title cannot exceed 200 characters")
	private String metaTitle;

	@Size(max = 500, message = "Meta description cannot exceed 500 characters")
	private String metaDescription;

	@Size(max = 100, message = "Category cannot exceed 100 characters")
	private String category;

	@Size(max = 100, message = "Author cannot exceed 100 characters")
	private String author;
	@Pattern(
		    regexp = "DRAFT|PUBLISHED",
		    message = "Status must be DRAFT or PUBLISHED"
		)
		private String status;

    public BlogPostRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
