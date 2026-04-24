package com.Hardik.Backend_Assesment.Dto;

import lombok.Data;

@Data
public class CommentRequest {
    private Long authorId;
    private String content;
    private int depthLevel;
    private boolean isBot;
    private Long humanId;
}
