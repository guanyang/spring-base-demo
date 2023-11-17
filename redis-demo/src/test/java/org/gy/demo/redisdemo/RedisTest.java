package org.gy.demo.redisdemo;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

public class RedisTest {

    private static StringRedisTemplate stringRedisTemplate;


    @BeforeAll
    public static void init() {
        stringRedisTemplate = new StringRedisTemplate(getJedisConnectionFactory());
    }

    private static LettuceConnectionFactory getJedisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName("127.0.0.1");
        factory.setPort(6379);
        factory.setDatabase(1);
        factory.setTimeout(2000);
        factory.afterPropertiesSet();
        return factory;
    }

    @Test
    public void testRedisCallback() {
        List<String> result = stringRedisTemplate.execute(new SessionCallback<List<String>>() {
            @Override
            public List<String> execute(RedisOperations operations) throws DataAccessException {
                HashOperations<String, String, String> opsForHash = operations.opsForHash();
                opsForHash.put("test", "key1", "value1");
                opsForHash.put("test", "key2", "value2");
                opsForHash.put("test", "key3", "value3");
                return opsForHash.multiGet("test", Lists.list("key1", "key2", "key3"));
            }
        });
        Assertions.assertEquals(Lists.list("value1", "value2", "value3"), result);
    }


}
