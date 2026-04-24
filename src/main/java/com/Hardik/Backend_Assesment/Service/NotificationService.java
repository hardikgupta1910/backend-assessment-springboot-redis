package com.Hardik.Backend_Assesment.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RedisTemplate redisTemplate;

    public void notify(Long userId, String message){

        String cooldownKey="notif:cooldown:user_"+userId;
        Boolean onCooldown=redisTemplate.hasKey(cooldownKey);

        if(Boolean.TRUE.equals(onCooldown)){
            redisTemplate.opsForList().rightPush("user:"+userId+":pending_notifs",message);
        }else{
            System.out.println("Push notification sent to user"+userId);
            redisTemplate.opsForValue().setIfAbsent(cooldownKey,"1", Duration.ofMinutes(15));
        }
    }

}
