package PocketTrack.Serverapp.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import PocketTrack.Serverapp.Domains.Models.UserData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserEmailRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserRoleRequestData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserStatusRequestData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class RedisSenderConfiguration {
    private final LettuceConnectionFactory lettuceConnectionFactory;

    @Bean
    RedisTemplate<String, UserRequest> registerUser() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        RedisTemplate<String, UserRequest> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, UserRequest.class));
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, UserStatusRequestData> updateUserStatus() {
        RedisTemplate<String, UserStatusRequestData> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserStatusRequestData.class));
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, UserRoleRequestData> updateUserRole() {
        RedisTemplate<String, UserRoleRequestData> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserRoleRequestData.class));
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, UserData> updateUserProfile() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        RedisTemplate<String, UserData> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, UserData.class));
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, UserEmailRequest> updateUserEmail() {
        RedisTemplate<String, UserEmailRequest> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserEmailRequest.class));
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    // @Bean
    // RedisTemplate<String, UserPasswordRequestData> updateUserPassword() {
    // RedisTemplate<String, UserPasswordRequestData> redisTemplate = new
    // RedisTemplate<>();
    // redisTemplate.setConnectionFactory(lettuceConnectionFactory);
    // redisTemplate.setValueSerializer(new
    // Jackson2JsonRedisSerializer<>(UserPasswordRequestData.class));
    // redisTemplate.setEnableTransactionSupport(true);
    // return redisTemplate;
    // }
}
