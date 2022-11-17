package org.gy.demo.vertx.handler;

import static io.vertx.ext.web.validation.builder.Bodies.formUrlEncoded;
import static io.vertx.ext.web.validation.builder.Parameters.param;
import static io.vertx.json.schema.common.dsl.Schemas.intSchema;
import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.vertx.annotation.RouteMapping;
import org.gy.demo.vertx.util.VertxUtil;
import org.springframework.stereotype.Component;

/**
 * 功能描述：全局配置handler
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/15 15:19
 */
@Slf4j
@Component
@RouteMapping(order = Integer.MAX_VALUE)
public class GlobalConfigHandler extends BaseHandler {

    @RouteMapping(value = "/api/v1/*", order = Integer.MAX_VALUE)
    public Handler<RoutingContext> bodyHandler() {
        return BodyHandler.create();
    }

    @RouteMapping(value = "/static/*", order = Integer.MAX_VALUE)
    public Handler<RoutingContext> staticHandler() {
        return StaticHandler.create();
    }

    @RouteMapping(value = "/api/v1/*", order = Integer.MAX_VALUE - 1)
    public Handler<RoutingContext> validationHandler() {
        SchemaParser parser = SchemaParser.createDraft7SchemaParser(
            SchemaRouter.create(VertxUtil.getVertxInstance(), new SchemaRouterOptions()));
        ValidationHandler validationHandler = ValidationHandler.builder(parser)
            .queryParameter(param("parameterName", intSchema())).body(formUrlEncoded(
                objectSchema().property("r", intSchema()).property("g", intSchema()).property("b", intSchema())))
            .build();
        return validationHandler;
    }

    @RouteMapping(value = "/health*")
    public Handler<RoutingContext> healthHandler() {
        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(VertxUtil.getVertxInstance());
        healthCheckHandler.register("my-procedure-name", promise -> {
            promise.complete(Status.OK(new JsonObject().put("time", System.currentTimeMillis())));
        });
        return healthCheckHandler;
    }

}
