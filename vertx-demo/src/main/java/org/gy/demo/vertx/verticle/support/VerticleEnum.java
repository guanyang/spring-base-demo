package org.gy.demo.vertx.verticle.support;

import static org.gy.demo.vertx.verticle.support.VerticleConstants.CORE_NUM;
import static org.gy.demo.vertx.verticle.support.VerticleConstants.SPLIT;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import java.util.stream.Stream;
import org.gy.demo.vertx.verticle.MainVerticle;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/4 10:27
 */
public enum VerticleEnum {

  MAIN(CORE_NUM, MainVerticle.class);

  private final int instances;

  private final Class<? extends Verticle> verticleClazz;

  VerticleEnum(int instances, Class<? extends Verticle> verticleClazz) {
    this.instances = instances;
    this.verticleClazz = verticleClazz;
  }

  public static void deployVerticle(Vertx vertx, VerticleFactory verticleFactory) {
    Stream.of(values()).forEach(item -> {
      // Scale the verticles on cores: create 4 instances during the deployment
      // https://vertx.io/docs/vertx-core/java/#_specifying_number_of_verticle_instances
      DeploymentOptions options = new DeploymentOptions().setInstances(item.getInstances());
      vertx.deployVerticle(verticleFactory.prefix() + SPLIT + item.getVerticleClazz().getName(), options);
    });
  }

  public int getInstances() {
    return instances;
  }

  public Class<? extends Verticle> getVerticleClazz() {
    return verticleClazz;
  }

}
