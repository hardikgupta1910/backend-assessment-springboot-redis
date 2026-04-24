package com.Hardik.Backend_Assesment.Controller;

import com.Hardik.Backend_Assesment.Dto.CommentRequest;
import com.Hardik.Backend_Assesment.Model.Comment;
import com.Hardik.Backend_Assesment.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId,
                                              @RequestBody CommentRequest req) {
        return ResponseEntity.ok(commentService.addComment(postId, req));
    }

}
