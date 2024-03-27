package PocketTrack.Serverapp.Configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.concurrent.Executors;

import PocketTrack.Serverapp.Domains.Models.RegisterData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserEmailRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserPasswordRequestData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserProfileRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.Redis.UserRoleRequest;
import PocketTrack.Serverapp.Utilities.AuthReceiver;

@Configuration
public class RedisReceiverConfiguration {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Bean
    LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost,
                redisPort);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean("registerMessageListener")
    MessageListenerAdapter registerListenerAdapter(AuthReceiver receiver) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver);
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, RegisterData.class));
        return messageListenerAdapter;
    }

    @Bean("updateUserRoleMessageListener")
    MessageListenerAdapter updateUserRoleMessageListener(AuthReceiver receiver) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver);
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(UserRoleRequest.class));
        return messageListenerAdapter;
    }

    @Bean("updateUserProfileMessageListener")
    MessageListenerAdapter updateUserProfileListenerAdapter(AuthReceiver receiver) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver);
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, UserProfileRequest.class));
        return messageListenerAdapter;
    }

    @Bean("updateUserEmailMessageListener")
    MessageListenerAdapter updateUserEmailMessageListener(AuthReceiver receiver) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver);
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(UserEmailRequest.class));
        return messageListenerAdapter;
    }

    @Bean("updateUserPasswordMessageListener")
    MessageListenerAdapter updateUserPasswordMessageListener(AuthReceiver receiver) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver);
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(UserPasswordRequestData.class));
        return messageListenerAdapter;
    }

    @Bean
    ChannelTopic channelTopicRegister() {
        return new ChannelTopic("register");
    }

    @Bean
    ChannelTopic channelTopicGenerateParticipantAccount() {
        return new ChannelTopic("generate-participant-account");
    }

    @Bean
    ChannelTopic channelTopicUpdateUserStatus() {
        return new ChannelTopic("update-status");
    }

    @Bean
    ChannelTopic channelTopicUpdateUserRole() {
        return new ChannelTopic("update-role");
    }

    @Bean
    ChannelTopic channelTopicUpdateUserProfile() {
        return new ChannelTopic("update-profile");
    }

    @Bean
    ChannelTopic channelTopicUpdateUserEmail() {
        return new ChannelTopic("update-email");
    }

    @Bean
    ChannelTopic channelTopicUpdateUserPassword() {
        return new ChannelTopic("update-password");
    }

    @Bean
    RedisMessageListenerContainer redisContainer(
            @Qualifier("registerMessageListener") MessageListenerAdapter registerListenerAdapter,
            @Qualifier("generateParticipantAccountMessageListener") MessageListenerAdapter generateParticipantAccountMessageListener,
            @Qualifier("updateUserStatusMessageListener") MessageListenerAdapter updateUserStatusMessageListener,
            @Qualifier("updateUserRoleMessageListener") MessageListenerAdapter updateUserRoleMessageListener,
            @Qualifier("updateUserProfileMessageListener") MessageListenerAdapter updateUserProfileListenerAdapter,
            @Qualifier("updateUserEmailMessageListener") MessageListenerAdapter updateUserEmailMessageListener,
            @Qualifier("updateUserPasswordMessageListener") MessageListenerAdapter updateUserPasswordMessageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory());
        container.addMessageListener(registerListenerAdapter,
                channelTopicRegister());
        container.addMessageListener(generateParticipantAccountMessageListener,
                channelTopicGenerateParticipantAccount());
        container.addMessageListener(updateUserStatusMessageListener,
                channelTopicUpdateUserStatus());
        container.addMessageListener(updateUserRoleMessageListener,
                channelTopicUpdateUserRole());
        container.addMessageListener(updateUserProfileListenerAdapter,
                channelTopicUpdateUserProfile());
        container.addMessageListener(updateUserEmailMessageListener,
                channelTopicUpdateUserEmail());
        container.addMessageListener(updateUserPasswordMessageListener,
                channelTopicUpdateUserPassword());
        container.setTaskExecutor(Executors.newFixedThreadPool(4));
        return container;
    }
}
