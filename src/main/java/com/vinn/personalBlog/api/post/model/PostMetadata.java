package com.vinn.personalBlog.api.post.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a metadata of content in the blog.
 */
@Entity
@Table(name = "post_metadata")
@Getter
@Setter
public class PostMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "key_name", nullable = false, length = 255)
    private String keyName;

    @Column(columnDefinition = "TEXT")
    private String value;
}
