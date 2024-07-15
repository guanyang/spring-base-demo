package org.gy.demo.mq.mqdemo.mq;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mq.mqdemo.common.Action.ConsumerAction;
import org.gy.demo.mq.mqdemo.mq.RocketMQProperties.RocketMQConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/12/22 14:53
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = RocketMQProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMqConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RocketMqManager rocketMqManager(RocketMQProperties rocketMqProperties) {
        return new RocketMqManager(rocketMqProperties);
    }

//    @Bean(name = "demoProperties")
//    @ConfigurationProperties(prefix = "rocketmq.demo")
//    public RocketMQProperties demoProperties() {
//        return new RocketMQProperties();
//    }
//
//
//    @Bean(name = "demoProducer")
//    public RocketMqProducer demoProducer(@Qualifier("demoProperties") RocketMQProperties demoProperties) {
//        return new RocketMqProducer(demoProperties);
//    }
//
//    @Bean(name = "demoConsumer")
//    public RocketMqConsumer demoConsumer(@Qualifier("demoProperties") RocketMQProperties demoProperties,
//        @Qualifier("demoListener") MessageListener demoListener) {
//        return new RocketMqConsumer(demoProperties, demoListener);
//    }
//
//    @Bean(name = "orderlyProperties")
//    @ConfigurationProperties(prefix = "rocketmq.orderly")
//    public RocketMQProperties orderlyProperties() {
//        return new RocketMQProperties();
//    }
//
//
//    @Bean(name = "orderlyProducer")
//    public RocketMqProducer orderlyProducer(@Qualifier("orderlyProperties") RocketMQProperties orderlyProperties) {
//        return new RocketMqProducer(orderlyProperties);
//    }
//
//    @Bean(name = "orderlyConsumer")
//    public RocketMqConsumer orderlyConsumer(@Qualifier("orderlyProperties") RocketMQProperties orderlyProperties,
//        @Qualifier("orderlyListener") MessageListener orderlyListener) {
//        return new RocketMqConsumer(orderlyProperties, orderlyListener);
//    }

    public static class RocketMqManager implements InitializingBean, DisposableBean {

        private final Map<String, RocketMqProducer> producerMap = Maps.newConcurrentMap();
        private final Map<String, RocketMqConsumer> consumerMap = Maps.newConcurrentMap();

        private final RocketMQProperties properties;

        public RocketMqManager(RocketMQProperties properties) {
            this.properties = properties;
            register(properties);
        }

        public RocketMqProducer getProducer(String name) {
            RocketMqProducer producer = producerMap.get(name);
            Assert.notNull(producer, () -> "RocketMqProducer must not be null: " + name);
            return producer;
        }

        private void register(RocketMQProperties properties) {
            Assert.notNull(properties, () -> "RocketMQProperties must not be null");
            Assert.notEmpty(properties.getItems(), () -> "RocketMQProperties items must not be null");
            Map<String, RocketMQConfig> items = properties.getItems();
            items.forEach((name, config) -> {
                if (config.getProducer() != null) {
                    RocketMqProducer rocketMqProducer = new RocketMqProducer(config);
                    register(name, rocketMqProducer);
                }
                if (config.getConsumer() != null) {
                    RocketMqConsumer rocketMqConsumer = new RocketMqConsumer(config);
                    register(name, rocketMqConsumer);
                }
            });
        }

        private void register(String name, RocketMqProducer producer) {
            producerMap.put(name, producer);
        }

        private void register(String name, RocketMqConsumer consumer) {
            consumerMap.put(name, consumer);
        }

        @Override
        public void destroy() throws Exception {
            producerMap.forEach((name, producer) -> {
                ConsumerAction.accept(producer, RocketMqProducer::destroy);
            });
            producerMap.clear();
            consumerMap.forEach((name, consumer) -> {
                ConsumerAction.accept(consumer, RocketMqConsumer::destroy);
            });
            consumerMap.clear();
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            producerMap.forEach((name, producer) -> {
                ConsumerAction.accept(producer, RocketMqProducer::init);
            });
            consumerMap.forEach((name, consumer) -> {
                ConsumerAction.accept(consumer, RocketMqConsumer::init);
            });
        }
    }

}
