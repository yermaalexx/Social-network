package com.yermaalexx.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yermaalexx.gateway.model.User;
import com.yermaalexx.gateway.model.UserPhoto;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper userMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        hibernateModule.disable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
        userMapper.registerModule(hibernateModule);

        Jackson2JsonRedisSerializer<User> userSerializer = new Jackson2JsonRedisSerializer<>(userMapper, User.class);

        RedisCacheConfiguration userCfg = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))                   // час життя кешу
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(userSerializer) // JSON-серіалізація
                );

        ObjectMapper photoMapper = new ObjectMapper();
        Jackson2JsonRedisSerializer<UserPhoto> photoSerializer = new Jackson2JsonRedisSerializer<>(photoMapper, UserPhoto.class);

        RedisCacheConfiguration photoCfg = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))                   // час життя кешу
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(photoSerializer) // JSON-серіалізація
                );

        // Map cacheName → конфіг
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put("users", userCfg);
        configs.put("photos", photoCfg);


        // створюємо менеджер кешів і вказуємо дефолтну конфігурацію
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(userCfg)
                .withInitialCacheConfigurations(configs)
                .build();
    }

}
