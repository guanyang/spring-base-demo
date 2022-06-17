package org.gy.demo.disruptor;

import org.junit.jupiter.api.Test;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/16 18:30
 */
public class CacheLineEffectTests {


    @Test
    public void test() {
        //考虑一般缓存行大小是64字节，一个 long 类型占8字节
        long[][] arr = new long[1024 * 1024][];
        for (int i = 0; i < 1024 * 1024; i++) {
            arr[i] = new long[8];
            for (int j = 0; j < 8; j++) {
                arr[i][j] = 0L;
            }
        }
        long sum = 0L;
        long marked = System.currentTimeMillis();
        for (int i = 0; i < 1024 * 1024; i += 1) {
            for (int j = 0; j < 8; j++) {
                sum = arr[i][j];
            }
        }
        System.out.println("Loop 1 times:" + (System.currentTimeMillis() - marked) + "ms");

        marked = System.currentTimeMillis();
        for (int i = 0; i < 8; i += 1) {
            for (int j = 0; j < 1024 * 1024; j++) {
                sum = arr[j][i];
            }
        }
        System.out.println("Loop 2 times:" + (System.currentTimeMillis() - marked) + "ms");
    }

}
