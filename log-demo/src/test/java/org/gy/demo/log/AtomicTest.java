package org.gy.demo.log;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
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
 * 功能描述：原子变量性能测试，参考文章：https://blog.csdn.net/weixin_43899792/article/details/124575032
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/19 10:47
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Threads(8)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class AtomicTest {

    private int number = 0;
    private AtomicInteger integerValue = new AtomicInteger();
    private AtomicLong longValue = new AtomicLong();
    private LongAdder longAdder = new LongAdder();
    private LongAccumulator longAccumulator = new LongAccumulator((x, y) -> x + y, 0);

    @Setup
    public void setup() {

    }

    @TearDown
    public void tearDown() {

    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(AtomicTest.class.getSimpleName()).build();
        new Runner(opt).run();
    }

    @Benchmark
    public void addSynchronized(Blackhole bh) {
        int value = addSynchronized();
        bh.consume(value);
    }

    @Benchmark
    public void addAtomicInteger(Blackhole bh) {
        int value = addAtomicInteger();
        bh.consume(value);
    }

    @Benchmark
    public void addAtomicLong(Blackhole bh) {
        long value = addAtomicLong();
        bh.consume(value);
    }

    @Benchmark
    public void addLongAdder(Blackhole bh) {
        addLongAdder();
        bh.consume(longAdder);
    }

    @Benchmark
    public void addLongAccumulator(Blackhole bh) {
        addLongAccumulator();
        bh.consume(longAccumulator);
    }

    private synchronized int addSynchronized() {
        return number++;
    }

    private int addAtomicInteger() {
        return integerValue.incrementAndGet();
    }

    private long addAtomicLong() {
        return longValue.incrementAndGet();
    }

    private void addLongAdder() {
        //sum执行时，并没有限制对base和cells的更新。所以LongAdder不是强一致性的，它是最终一致性的（也就是说并发情况下，sum的值并不精确）
        longAdder.increment();
    }

    private void addLongAccumulator() {
        //get执行时，并没有限制对base和cells的更新。所以LongAccumulator不是强一致性的，它是最终一致性的（也就是说并发情况下，get的值并不精确）
        longAccumulator.accumulate(1);
    }


}
