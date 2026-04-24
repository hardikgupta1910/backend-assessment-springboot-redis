package com.Hardik.Backend_Assesment.Service;

import com.Hardik.Backend_Assesment.Dto.CommentRequest;
import com.Hardik.Backend_Assesment.Model.Comment;
import com.Hardik.Backend_Assesment.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RedisTemplate<String,String> redisTemplate;
    private final NotificationService notificationService;

    public Comment addComment(Long postId, CommentRequest commentRequest) {
        if(commentRequest.getDepthLevel()>20){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Depth level exceeded");
        }
        if ((commentRequest.isBot())){
            Long botCount=redisTemplate.opsForValue().increment("post:"+postId+":bot_count");

            if(botCount != null && botCount > 100){
                redisTemplate.opsForValue()
                        .decrement("post:" + postId + ":bot_count");
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Bot reply limit reached for this post");
            }
            String cooldownKey="cooldown:bot_"+commentRequest.getAuthorId()+":human_"+ commentRequest.getHumanId();

            Boolean cooldownExists = redisTemplate.hasKey(cooldownKey);

            if(Boolean.TRUE.equals(cooldownExists)){
                redisTemplate.opsForValue().increment("post:"+postId+":bot_count",-1);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"cooldown active. Try after 10 minutes.");
            }

            redisTemplate.opsForValue().set(cooldownKey,"1", Duration.ofMinutes(10));

            redisTemplate.opsForValue().increment("post:"+postId+":virality_score",1);

            notificationService.notify(commentRequest.getHumanId(),"Bot "
                    + commentRequest.getAuthorId()+"replied to your post ");
        } else {
            redisTemplate.opsForValue()
                    .increment("post:" + postId + ":virality_score", 50);
        }
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(commentRequest.getAuthorId());
        comment.setContent(commentRequest.getContent());
        comment.setDepthLevel(commentRequest.getDepthLevel());
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}
