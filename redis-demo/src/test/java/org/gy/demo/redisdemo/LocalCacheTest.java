package org.gy.demo.redisdemo;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/29 17:46
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Threads(2)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class LocalCacheTest {

    @Param({"10000000"})
    private int args;

    private Cache<String, String> cache;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(RedisCacheTest.class.getSimpleName()).build();
        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        cache = Caffeine.newBuilder()
            // 设置最后一次写入或访问后经过固定时间过期
            .expireAfterAccess(300, TimeUnit.SECONDS)
            // 初始的缓存空间大小
            .initialCapacity(1000)
            // 缓存的最大条数
            .maximumSize(10000).build();
    }

    @Benchmark
    public void put(Blackhole bh) {
        String key = buildKey(args);
        cache.put(key, key);
        bh.consume(key);
    }

    @Benchmark
    public void get(Blackhole bh) {
        String key = buildKey(args);
        String value = cache.getIfPresent(key);
        bh.consume(value);
    }

    private static String buildKey(int args) {
        return String.join(":", "gy:test", String.valueOf(nextRandom(args)));
    }

    private static int nextRandom(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

}
