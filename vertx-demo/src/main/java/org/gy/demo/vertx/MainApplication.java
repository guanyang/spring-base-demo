package org.gy.demo.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.gy.demo.vertx.config.SpringVerticleFactory;
import org.gy.demo.vertx.verticle.support.VerticleEnum;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/3 20:27
 */
@Configuration
@ComponentScan(basePackageClasses = MainApplication.class)
public class MainApplication {

  private static final int CORE_NUM = Runtime.getRuntime().availableProcessors();

  public static void main(String[] args) {
    //异步日志支持
    System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

    Vertx vertx = Vertx.vertx();

    ApplicationContext context = new AnnotationConfigApplicationContext(MainApplication.class);
    VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);
    // The verticle factory is registered manually because it is created by the Spring container
    vertx.registerVerticleFactory(verticleFactory);

    // Deploy verticle
    VerticleEnum.deployVerticle(vertx, verticleFactory);
  }

}
