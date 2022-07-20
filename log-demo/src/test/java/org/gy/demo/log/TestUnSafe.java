package org.gy.demo.log;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
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
import sun.misc.Unsafe;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/13 16:20
 */
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Threads(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class TestUnSafe {

    static final Unsafe unsafe;
    static final long stateOffset;
    static final long volStateOffset;
    private volatile long volState = 0;
    private long state = 0;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            stateOffset = unsafe.objectFieldOffset(TestUnSafe.class.getDeclaredField("state"));
            volStateOffset = unsafe.objectFieldOffset(TestUnSafe.class.getDeclaredField("volState"));
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
            throw new Error(ex);
        }
    }

    private int total = 100000;

    private TestUnSafe test;

    @Setup
    public void setup() {
        test = new TestUnSafe();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(TestUnSafe.class.getSimpleName()).build();
        new Runner(opt).run();
    }

    @Benchmark
    public void unsafePutNonVolatile(Blackhole bh) {
        int random = nextRandom(total);
        unsafe.putLong(test, stateOffset, random);
        bh.consume(test);
    }

    @Benchmark
    public void pojoPutNonVolatile(Blackhole bh) {
        int random = nextRandom(total);
        test.state = random;
        bh.consume(test);
    }

    @Benchmark
    public void unsafePutVolatile(Blackhole bh) {
        int random = nextRandom(total);
        unsafe.putLong(test, volStateOffset, random);
        bh.consume(test);
    }

    @Benchmark
    public void pojoPutVolatile(Blackhole bh) {
        int random = nextRandom(total);
        test.volState = random;
        bh.consume(test);
    }

    private static int nextRandom(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

}
