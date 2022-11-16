package org.gy.demo.vertx.verticle;

import static org.gy.demo.vertx.verticle.VertxHandlerFactory.registerRouter;
import static org.gy.demo.vertx.verticle.support.VerticleConstants.DEFAULT_PACKAGE;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
public class MainVerticle extends AbstractVerticle {

    @Value("${server.port}")
    private int serverPort;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        //路由规则定义
        Router router = Router.router(vertx);
        registerRouter(router, DEFAULT_PACKAGE);
        //创建http服务，监听端口
        vertx.createHttpServer().requestHandler(router).listen(serverPort).onSuccess(server -> {
            log.info("HTTP server started on port {}", server.actualPort());
            startPromise.complete();
        }).onFailure(e -> {
            log.error("HTTP server start fail.", e);
            startPromise.fail(e);
        });
    }
}
