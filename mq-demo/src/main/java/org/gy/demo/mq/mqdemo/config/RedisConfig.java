package org.gy.demo.mq.mqdemo.config;

import org.springframework.context.annotation.Configuration;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/8/16 21:01
 */
@Configuration
public class RedisConfig {

//    @Bean(name = "redisProperties")
//    @ConfigurationProperties(prefix = "redis.default")
//    public RedisProperties defaultRedisProperties() {
//        return new RedisProperties();
//    }
//
//    @Bean(name = "redisTemplate")
//    public StringRedisTemplate redisTemplate(
//        @Qualifier("redisProperties") RedisProperties defaultRedisProperties) {
//        JedisConnectionFactory factory = getJedisConnectionFactory(defaultRedisProperties);
//        return new StringRedisTemplate(factory);
//    }
//
//    private JedisConnectionFactory getJedisConnectionFactory(RedisProperties redisProperties) {
//        //构建factory
//        JedisConnectionFactory factory = new JedisConnectionFactory();
//        factory.setHostName(redisProperties.getHost());
//        factory.setPort(redisProperties.getPort());
//        factory.setPassword(redisProperties.getPassword());
//        factory.setDatabase(redisProperties.getDatabase());
//        factory.setTimeout(redisProperties.getTimeout());
//        factory.setUsePool(true);
//        return factory;
//    }

}
