package org.gy.demo.vertx.config;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import java.util.concurrent.Callable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/3 20:29
 */
@Component
public class SpringVerticleFactory implements VerticleFactory, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public String prefix() {
    // Just an arbitrary string which must uniquely identify the verticle factory
    return SpringVerticleFactory.class.getSimpleName();
  }

  @Override
  public void createVerticle(String verticleName, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {
    // Our convention in this example is to give the class name as verticle name
    String clazz = VerticleFactory.removePrefix(verticleName);
    promise.complete(() -> (Verticle) applicationContext.getBean(Class.forName(clazz)));
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
