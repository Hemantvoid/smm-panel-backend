package com.example.demo.controller;

import com.example.demo.dto.BlogPostResponse;
import com.example.demo.service.BlogPostService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SitemapController {

    private final BlogPostService blogPostService;

    public SitemapController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping(
    	    value = "/sitemap.xml",
    	    produces = MediaType.APPLICATION_XML_VALUE
    	)
    public ResponseEntity<String> getSitemap() {

        List<BlogPostResponse> posts =
                blogPostService.getPublishedPosts();

        StringBuilder sitemap =
                new StringBuilder();

        sitemap.append(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        );

        sitemap.append(
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
        );

        // =========================
        // MAIN WEBSITE
        // =========================

        sitemap.append("<url>");
        sitemap.append(
                "<loc>https://smmlover.in/</loc>"
        );
        sitemap.append("</url>");

        // =========================
        // BLOG PAGE
        // =========================

        sitemap.append("<url>");
        sitemap.append(
                "<loc>https://smmlover.in/blog</loc>"
        );
        sitemap.append("</url>");

        // =========================
        // BLOG POSTS
        // =========================

        for (BlogPostResponse post : posts) {

            if (post.getSlug() == null ||
                post.getSlug().isBlank()) {

                continue;
            }

            sitemap.append("<url>");

            sitemap.append("<loc>");
            sitemap.append(
                    "https://smmlover.in/blog/"
            );
            sitemap.append(
                    escapeXml(post.getSlug())
            );
            sitemap.append("</loc>");

            if (post.getUpdatedAt() != null) {

                sitemap.append("<lastmod>");
                sitemap.append(
                        post.getUpdatedAt()
                );
                sitemap.append("</lastmod>");

            } else if (
                    post.getPublishedAt() != null
            ) {

                sitemap.append("<lastmod>");
                sitemap.append(
                        post.getPublishedAt()
                );
                sitemap.append("</lastmod>");

            }

            sitemap.append("</url>");
        }

        sitemap.append("</urlset>");

        return ResponseEntity.ok(
                sitemap.toString()
        );
    }


    private String escapeXml(String value) {

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
