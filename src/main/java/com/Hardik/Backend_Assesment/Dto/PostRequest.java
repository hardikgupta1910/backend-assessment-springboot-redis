package com.Hardik.Backend_Assesment.Dto;

import lombok.Data;

@Data
public class PostRequest {
    private Long authorId;
    private String content;
}
