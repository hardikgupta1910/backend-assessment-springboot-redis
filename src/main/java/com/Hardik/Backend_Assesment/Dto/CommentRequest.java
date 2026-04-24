package com.Hardik.Backend_Assesment.Dto;

import lombok.Data;

@Data
public class CommentRequest {
    private Long authorId;      // ID of commenter (user or bot)
    private String content;
    private int depthLevel;
    private boolean isBot;      // true if comment is from bot
    private Long humanId;       // required ONLY if bot = true (cooldown logic)
}
