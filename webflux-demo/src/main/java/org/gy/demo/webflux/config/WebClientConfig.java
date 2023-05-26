package org.gy.demo.webflux.config;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;

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
        //连接池配置
        ConnectionProvider provider = ConnectionProvider.create("custom-connect", 500);
        factory.setConnectionProvider(provider);
        //选择器线程数、工作线程数配置
        LoopResources loopResources = LoopResources.create("custom-loop", 8, 64, true);
        factory.setLoopResources(loopResources);
        return factory;
    }

    @Bean
    HttpClient httpClient(ReactorResourceFactory resourceFactory) {
        TcpClient tcpClient = TcpClient.create(resourceFactory.getConnectionProvider());
        tcpClient.runOn(resourceFactory.getLoopResources());
        tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
//        tcpClient.option(ChannelOption.SO_TIMEOUT, 3000);
        tcpClient.option(ChannelOption.SO_KEEPALIVE, true);
        tcpClient.option(ChannelOption.TCP_NODELAY, true);
        tcpClient.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
//        tcpClient.doOnConnected(conn -> {
//            conn.addHandlerLast(new ReadTimeoutHandler(3));
//            conn.addHandlerLast(new WriteTimeoutHandler(3));
//        });
        return HttpClient.from(tcpClient).responseTimeout(Duration.ofMillis(3000));
    }

    @Bean
    WebClient webClient(HttpClient httpClient) {
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

}
