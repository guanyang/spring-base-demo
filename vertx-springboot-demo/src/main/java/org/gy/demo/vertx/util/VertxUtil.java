package org.gy.demo.vertx.util;

import io.vertx.core.Vertx;
import java.util.Objects;

/**
 * vertx单例
 */
public final class VertxUtil {

    private static Vertx singletonVertx;

    private VertxUtil() {

    }

    public static void init(Vertx vertx) {
        singletonVertx = Objects.requireNonNull(vertx, "未初始化Vertx");
    }

    public static Vertx getVertxInstance() {
        return Objects.requireNonNull(singletonVertx, "未初始化Vertx");
    }
}
