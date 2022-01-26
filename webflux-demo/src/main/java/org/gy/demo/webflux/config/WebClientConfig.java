package org.gy.demo.webflux.config;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/1/24 19:07
 */
@Configuration
public class WebClientConfig {

    @Bean
    ReactorResourceFactory resourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);
        ConnectionProvider provider = ConnectionProvider.create("webflux", 500);
        factory.setConnectionProvider(provider);
        factory.setLoopResources(LoopResources.create("webflux-http"));
        return factory;
    }

    @Bean
    WebClient webClient() {
        Function<HttpClient, HttpClient> mapper = client ->
            client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.SO_TIMEOUT, 3000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new ReadTimeoutHandler(3));
                    conn.addHandlerLast(new WriteTimeoutHandler(3));
                });
        ClientHttpConnector connector = new ReactorClientHttpConnector(resourceFactory(), mapper);
        return WebClient.builder().clientConnector(connector).build();
    }

}
