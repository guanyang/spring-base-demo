package org.gy.demo.vertx.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.gy.framework.core.dto.Response;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/3 17:02
 */
@Slf4j
@Component
public class TestHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    Response<Long> response = new Response<>();
    response.setData(System.currentTimeMillis());
    ctx.json(response);
    log.info("TestHandler exec success");
  }
}
