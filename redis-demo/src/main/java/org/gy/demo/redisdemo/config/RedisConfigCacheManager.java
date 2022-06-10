package org.gy.demo.redisdemo.config;

import java.time.Duration;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

/**
 * 重新定义RedisCache，名称格式：name#expireTime，自动解析cache过期时间
 *
 * @author gy
 * @version 1.0.0
 */
public class RedisConfigCacheManager extends RedisCacheManager {

    public RedisConfigCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        final int lastIndexOf = name.lastIndexOf('#');
        String cacheName = name;
        long ttlSeconds = -1;
        if (lastIndexOf > -1) {
            ttlSeconds = Long.parseLong(name.substring(lastIndexOf + 1));
            cacheName = name.substring(0, lastIndexOf);
        }
        final Duration duration = Duration.ofSeconds(ttlSeconds);
        cacheConfig = cacheConfig.entryTtl(duration);
        return super.createRedisCache(cacheName, cacheConfig);
    }

}
