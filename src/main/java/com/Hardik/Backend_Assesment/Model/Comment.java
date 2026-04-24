package com.Hardik.Backend_Assesment.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "post_id")
    private Long postId;
    @Column(name = "author_id")
    private Long authorId;
    private String content;
    @Column(name = "depth_level")

    private int depthLevel;
    @Column(name = "created_at")

    private LocalDateTime createdAt;
}
