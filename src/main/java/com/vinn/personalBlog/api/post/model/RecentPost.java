package com.vinn.personalBlog.api.post.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the recent posts.
 */
@Entity
@Table(name = "recent_posts")
@Getter
@Setter
public class RecentPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
