package org.gy.demo.vertx.router;

import static org.gy.demo.vertx.verticle.support.VerticleEnum.MAIN;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.Arrays;
import java.util.Optional;
import org.gy.demo.vertx.handler.HelloHandler;
import org.gy.demo.vertx.handler.TestHandler;
import org.gy.demo.vertx.verticle.support.VerticleEnum;
import org.springframework.context.ApplicationContext;

/**
 * 功能描述：路由定义枚举
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/3 16:09
 */
public enum RouterEnum {

  HELLO(HttpMethod.GET, "/hello", 0, HelloHandler.class, MAIN),

  TEST(HttpMethod.GET, "/api/test", 1, TestHandler.class, MAIN);

  private final HttpMethod method;

  private final String path;

  /**
   * 路由优先级，值越小优先级越高
   */
  private final int order;

  private final Class<? extends Handler<RoutingContext>> requestHandlerClazz;

  private final VerticleEnum verticleEnum;

  RouterEnum(HttpMethod method, String path, int order, Class<? extends Handler<RoutingContext>> requestHandlerClazz,
    VerticleEnum verticleEnum) {
    this.method = method;
    this.path = path;
    this.order = order;
    this.requestHandlerClazz = requestHandlerClazz;
    this.verticleEnum = verticleEnum;
  }

  public static void route(Router router, ApplicationContext applicationContext, VerticleEnum verticleEnum) {
    Arrays.stream(values()).filter(e -> e.verticleEnum.equals(verticleEnum)).forEach(item -> {
      Handler<RoutingContext> handler = applicationContext.getBean(item.requestHandlerClazz);
      Optional.ofNullable(handler).ifPresent(h -> {
        router.route(item.method, item.path).order(item.order).handler(h);
      });
    });
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public int getOrder() {
    return order;
  }

  public Class<? extends Handler<RoutingContext>> getRequestHandlerClazz() {
    return requestHandlerClazz;
  }

  public VerticleEnum getVerticleEnum() {
    return verticleEnum;
  }
}
