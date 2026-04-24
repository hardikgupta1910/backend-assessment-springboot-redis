package com.Hardik.Backend_Assesment.Controller;

import com.Hardik.Backend_Assesment.Dto.PostRequest;
import com.Hardik.Backend_Assesment.Model.Post;
import com.Hardik.Backend_Assesment.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostRequest req) {
        return ResponseEntity.ok(postService.createPost(req));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return ResponseEntity.ok("Liked");
    }
}
