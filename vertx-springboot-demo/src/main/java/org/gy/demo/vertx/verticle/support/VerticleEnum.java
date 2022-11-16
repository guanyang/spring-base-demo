package org.gy.demo.vertx.verticle.support;

import static org.gy.demo.vertx.verticle.support.VerticleConstants.DEFAULT_OPTIONS;
import static org.gy.demo.vertx.verticle.support.VerticleConstants.SPLIT;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gy.demo.vertx.verticle.AsyncServiceVerticle;
import org.gy.demo.vertx.verticle.MainVerticle;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/4 10:27
 */
@Getter
@AllArgsConstructor
public enum VerticleEnum {

    MAIN(MainVerticle.class, DEFAULT_OPTIONS),

    ASYNC(AsyncServiceVerticle.class, new DeploymentOptions(DEFAULT_OPTIONS).setWorker(true));

    private final Class<? extends Verticle> verticleClazz;

    private final DeploymentOptions options;

    public static void deployVerticle(Vertx vertx, VerticleFactory verticleFactory) {
        Stream.of(values()).forEach(item -> {
            // Scale the verticles on cores: create 4 instances during the deployment
            // https://vertx.io/docs/vertx-core/java/#_specifying_number_of_verticle_instances
            vertx.deployVerticle(verticleFactory.prefix() + SPLIT + item.verticleClazz.getName(), item.options);
        });
    }

}
