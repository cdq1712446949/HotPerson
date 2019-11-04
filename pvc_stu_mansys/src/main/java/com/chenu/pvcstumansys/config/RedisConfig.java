package com.chenu.pvcstumansys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 基本作用：Redis配置类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@Configuration
public class RedisConfig {

    /**
     * redis json模版
     * @param redisConnectionFactory redis连接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> jsonRedisTemplate(
            RedisConnectionFactory redisConnectionFactory
    )
    {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    /**
     * 自定义的 RedisCacheManager
     * CacheManagerCustomizers可以自己定制缓存的一些规则
     */
    @Bean
    public RedisCacheManager jsonRedisCacheManager(
            RedisTemplate<String, Object> jsonRedisTemplate
    ){
        RedisCacheManager cacheManager = new RedisCacheManager(jsonRedisTemplate);
        // 使用前缀，默认会将CacheName作为key的前缀
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }

}
