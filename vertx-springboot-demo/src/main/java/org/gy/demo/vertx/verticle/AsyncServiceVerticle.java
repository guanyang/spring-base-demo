package org.gy.demo.vertx.verticle;

import static org.gy.demo.vertx.verticle.VertxHandlerFactory.registerAsyncService;
import static org.gy.demo.vertx.verticle.support.VerticleConstants.DEFAULT_PACKAGE;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 异步服务注册
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 20:25
 */
@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
public class AsyncServiceVerticle extends AbstractVerticle {

    private List<MessageConsumer<JsonObject>> messageConsumers;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        //注册异步服务
        messageConsumers = registerAsyncService(vertx, DEFAULT_PACKAGE);
        log.info("registerAsyncService success,package={}", DEFAULT_PACKAGE);
        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        if (ObjectUtils.isEmpty(messageConsumers)) {
            stopPromise.complete();
            return;
        }
        messageConsumers.forEach(s -> s.unregister());
        stopPromise.complete();
    }
}
