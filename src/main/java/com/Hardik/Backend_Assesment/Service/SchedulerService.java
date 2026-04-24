package com.Hardik.Backend_Assesment.Service;

import com.Hardik.Backend_Assesment.Model.User;
import com.Hardik.Backend_Assesment.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 300000)
    public void sweepNotification(){
        List<User> users = userRepository.findAll();

        for(User user : users){
            String key="user:"+user.getId()+":pending_notifs";
            Long count=redisTemplate.opsForList().size(key);

            if(count!=null && count>0){
                List<String> messages =
                        redisTemplate.opsForList().range(key, 0, -1);
                String first = messages.get(0);
                if (count == 1) {
                    System.out.println(first);
                } else {
                    System.out.println(first + " and " + (count - 1) + " others...");
                }redisTemplate.delete(key);
            }
        }
    }
}
