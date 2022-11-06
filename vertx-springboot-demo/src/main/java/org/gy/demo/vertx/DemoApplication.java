package org.gy.demo.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gy.demo.vertx.config.SpringVerticleFactory;
import org.gy.demo.vertx.util.VertxUtil;
import org.gy.demo.vertx.verticle.support.VerticleEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class DemoApplication implements ApplicationListener<ApplicationReadyEvent> {

    private final AtomicBoolean registered = new AtomicBoolean(false);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        VertxUtil.init(vertx);
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!registered.compareAndSet(false, true)) {
            return;
        }
        ApplicationContext context = event.getApplicationContext();
        Vertx vertx = VertxUtil.getVertxInstance();
        VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);
        // The verticle factory is registered manually because it is created by the Spring container
        vertx.registerVerticleFactory(verticleFactory);
        // Deploy verticle
        VerticleEnum.deployVerticle(vertx, verticleFactory);
    }
}
