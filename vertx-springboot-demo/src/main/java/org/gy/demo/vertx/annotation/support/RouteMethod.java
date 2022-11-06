package org.gy.demo.vertx.annotation.support;

import io.vertx.core.http.HttpMethod;
import java.util.Optional;

/**
 * Router API 请求处理方式枚举
 *
 * @see HttpMethod
 */
public enum RouteMethod {
    OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT, PATCH;

    public static HttpMethod nameOf(RouteMethod method) {
        return Optional.ofNullable(method).map(RouteMethod::name).map(HttpMethod::valueOf)
            .orElseThrow(() -> new IllegalArgumentException("unknown RouteMethod:" + method));
    }
}
