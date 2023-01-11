package org.gy.demo.mq.mqdemo.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/1/11 10:10
 */
@Slf4j
@Configuration
public class PulsarConfiguration {

    @Bean(name = "pulsarProperties")
    @ConfigurationProperties(prefix = "pulsar")
    public PulsarProperties pulsarProperties() {
        return new PulsarProperties();
    }

    @Bean(name = "pulsarClient", destroyMethod = "close")
    public PulsarClient pulsarClient(@Qualifier("pulsarProperties") PulsarProperties pulsarProperties)
        throws PulsarClientException {
        return PulsarClient.builder().serviceUrl(pulsarProperties.getServerUrl()).build();
    }

    @Bean(name = "demoPulsarProducer", destroyMethod = "close")
    public Producer<String> demoPulsarProducer(@Qualifier("pulsarClient") PulsarClient pulsarClient,
        @Qualifier("pulsarProperties") PulsarProperties pulsarProperties) throws PulsarClientException {
        return pulsarClient.newProducer(Schema.STRING).topic(pulsarProperties.getTopic()).create();
    }


    @Bean(name = "demoPulsarConsumer", destroyMethod = "close")
    public Consumer<String> demoPulsarConsumer(@Qualifier("pulsarClient") PulsarClient pulsarClient,
        @Qualifier("pulsarProperties") PulsarProperties pulsarProperties,
        @Qualifier("pulsarDemoListener") MessageListener<String> pulsarDemoListener) throws PulsarClientException {
        return pulsarClient.newConsumer(Schema.STRING).topic(pulsarProperties.getTopic())
            .messageListener(pulsarDemoListener).subscriptionName(pulsarProperties.getSubscriptionName()).subscribe();
    }

}
