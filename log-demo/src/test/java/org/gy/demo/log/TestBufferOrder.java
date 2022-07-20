package org.gy.demo.log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
 * @date 2022/7/19 11:02
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Threads(2)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class TestBufferOrder {

    @Param({"16"})
    private int capacity;

    private ByteBuffer sameByteBuffer;

    private ByteBuffer diffByteBuffer;

    private ByteOrder currentOrder;

    @Setup
    public void setup() {
        currentOrder = ByteOrder.nativeOrder();

        sameByteBuffer = ByteBuffer.allocate(capacity * 4);
        sameByteBuffer.order(currentOrder);

        ByteOrder diffOrder = ByteOrder.LITTLE_ENDIAN == currentOrder ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        diffByteBuffer = ByteBuffer.allocate(capacity * 4);
        diffByteBuffer.order(diffOrder);
    }

    @TearDown
    public void tearDown() {
        sameByteBuffer.clear();
        diffByteBuffer.clear();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(TestBufferOrder.class.getSimpleName()).build();
        new Runner(opt).run();
    }

    @Benchmark
    public void sameByteOrder(Blackhole bh) {
        int random = nextRandom(capacity);
        sameByteBuffer.putInt(random, random);
        byte value = sameByteBuffer.get(random);
        bh.consume(value);
    }

    @Benchmark
    public void diffByteOrder(Blackhole bh) {
        int random = nextRandom(capacity);
        diffByteBuffer.putInt(random, random);
        byte value = diffByteBuffer.get(random);
        bh.consume(value);
    }

    @Test
    public void testBigOrder() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        // 获取默认的byte顺序
        Assertions.assertEquals(buffer.order(), ByteOrder.BIG_ENDIAN);
        buffer.putShort(0, (short) 1);
        Assertions.assertEquals(buffer.get(0), 0);
        Assertions.assertEquals(buffer.get(1), 1);
    }

    @Test
    public void testLittleOrder() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(0, (short) 1);
        Assertions.assertEquals(buffer.get(0), 1);
        Assertions.assertEquals(buffer.get(1), 0);
    }

    private static int nextRandom(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

}
