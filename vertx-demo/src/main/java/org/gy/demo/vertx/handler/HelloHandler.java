package org.gy.demo.vertx.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/3 16:06
 */
@Component
public class HelloHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    String param = ctx.request().getParam("key", "defaultValue");
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("msg", "ok");
    result.put("data", param);
    ctx.json(result);
  }
}
