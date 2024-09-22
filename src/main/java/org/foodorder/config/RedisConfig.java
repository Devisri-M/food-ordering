package org.foodorder.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericToStringSerializer;
//
//@Configuration
//public class RedisConfig {
//
//    // Configure RedisConnectionFactory using Lettuce (or you can use Jedis)
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        // Here, specify the Redis host and port for the Docker setup
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory("redis", 6379);
//        lettuceConnectionFactory.afterPropertiesSet(); // Ensure properties are set correctly
//        return lettuceConnectionFactory;
//    }
//
//    // RedisTemplate for custom Redis operations
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        final RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory());
//        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
//        return template;
//    }
//}