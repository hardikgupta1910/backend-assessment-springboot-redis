package com.Hardik.Backend_Assesment.Service;

import com.Hardik.Backend_Assesment.Dto.PostRequest;
import com.Hardik.Backend_Assesment.Model.Post;
import com.Hardik.Backend_Assesment.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public Post createPost(PostRequest postRequest) {
        Post post = new Post();
        post.setAuthorId(postRequest.getAuthorId());
        post.setContent(postRequest.getContent());
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void likePost(Long postId){
        redisTemplate.opsForValue().increment("post:"+postId+":virality_score",20);

    }
}
