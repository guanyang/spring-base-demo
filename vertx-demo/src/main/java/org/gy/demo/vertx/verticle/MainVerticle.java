package org.gy.demo.vertx.verticle;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.vertx.router.RouterEnum;
import org.gy.demo.vertx.verticle.support.VerticleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
public class MainVerticle extends AbstractVerticle {

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    //路由规则定义
    Router router = Router.router(vertx);
    RouterEnum.route(router, applicationContext, VerticleEnum.MAIN);
    //创建http服务，监听端口
    vertx.createHttpServer().requestHandler(router).listen(8888).onSuccess(server -> {
      log.info("HTTP server started on port {}", server.actualPort());
      startPromise.complete();
    }).onFailure(e -> {
      log.error("HTTP server start fail.", e);
      startPromise.fail(e);
    });
  }
}
