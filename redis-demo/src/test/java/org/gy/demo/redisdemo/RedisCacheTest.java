package org.gy.demo.redisdemo;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/29 16:36
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Threads(2)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class RedisCacheTest {

    @Param({"10000000"})
    private int args;

    private StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(RedisCacheTest.class.getSimpleName()).build();
        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        LettuceConnectionFactory factory = getJedisConnectionFactory();
        stringRedisTemplate = new StringRedisTemplate(factory);
    }

    @Benchmark
    public void put(Blackhole bh) {
        String key = buildKey(args);
        stringRedisTemplate.opsForValue().set(key, key, 300, TimeUnit.SECONDS);
        bh.consume(key);
    }

    @Benchmark
    public void get(Blackhole bh) {
        String key = buildKey(args);
        String value = stringRedisTemplate.opsForValue().get(key);
        bh.consume(value);
    }

    private static String buildKey(int args) {
        return String.join(":", "gy:test", String.valueOf(nextRandom(args)));
    }

    private static int nextRandom(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    private static LettuceConnectionFactory getJedisConnectionFactory() {
        //构建factory
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName("127.0.0.1");
        factory.setPort(6379);
        factory.setDatabase(1);
        factory.setTimeout(2000);
        factory.afterPropertiesSet();
        return factory;
    }

}
