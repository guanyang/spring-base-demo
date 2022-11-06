package org.gy.demo.vertx.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.vertx.annotation.AsyncReference;
import org.gy.demo.vertx.annotation.RouteMapping;
import org.gy.demo.vertx.annotation.support.RouteMethod;
import org.gy.demo.vertx.service.async.AsyncHelloService;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 21:33
 */
@Slf4j
@Component
@RouteMapping("/async/api/v1")
public class AsyncServiceConsumerHandler extends BaseHandler {

    @AsyncReference
    private AsyncHelloService asyncHelloService;

    @RouteMapping(value = "/test1", method = RouteMethod.GET)
    public Handler<RoutingContext> test1() {
        return ctx -> invoke(asyncHelloService::hello, ctx);
    }

    @RouteMapping(value = "/test2", method = RouteMethod.GET)
    public Handler<RoutingContext> test2() {
        return ctx -> invoke(asyncHelloService::hello2, ctx);
    }

}
