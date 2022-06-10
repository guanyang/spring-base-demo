package org.gy.demo.redisdemo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@EnableCaching
@Configuration
public class RedisCacheConfig {

    public static final int BATCH_SIZE = 1000;

    @Bean
    @Primary
    public RedisCacheManager redisCacheManager(final RedisConnectionFactory factory) {
        //定义缓存配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//            .disableCachingNullValues()
            .serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer()));
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(factory,
            BatchStrategies.scan(BATCH_SIZE));

        return new RedisConfigCacheManager(cacheWriter, config);
    }

    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
            Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        //属性为NULL不被序列化
        om.setSerializationInclusion(Include.NON_NULL);
        //关闭空对象不让序列化功能
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //忽略未知字段
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        om.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

}
