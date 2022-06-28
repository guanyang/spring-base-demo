package org.gy.demo.ignite;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.ClientException;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
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
import org.openjdk.jmh.annotations.TearDown;
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
 * @date 2022/6/27 16:55
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Threads(2)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
public class IgniteClientTest1 {

    @Param({"10000000"})
    private int args;

    private IgniteClient client;

    private ClientCache<Integer, String> cache;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(IgniteClientTest1.class.getSimpleName()).build();
        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        //开启分区感知，提升性能
        ClientConfiguration cfg = new ClientConfiguration().setAddresses("127.0.0.1:10801", "127.0.0.1:10802",
            "127.0.0.1:10803").setPartitionAwarenessEnabled(true);
        client = Ignition.startClient(cfg);
        cache = client.getOrCreateCache("myCache");
    }

    @TearDown
    public void tearDown() {
        if (client != null) {
            client.close();
        }
    }

    @Benchmark
    public void put(Blackhole bh) {
        int key = nextRandom(args);
        cache.put(key, String.valueOf(key));
        bh.consume(key);
    }

    @Benchmark
    public void get(Blackhole bh) {
        int key = nextRandom(args);
        String value = cache.get(key);
        bh.consume(value);
    }

    public static int nextRandom(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    /**
     * @param min Minimum key in range.
     * @param max Maximum key in range.
     * @return Next key.
     */
    public static int nextRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min) + min;
    }

}
