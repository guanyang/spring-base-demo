package org.gy.demo.http.config;

import feign.Client;
import feign.Request;
import feign.Request.Options;
import feign.okhttp.OkHttpClient;
import okhttp3.ConnectionPool;
import org.springframework.cloud.openfeign.clientconfig.FeignClientConfigurer;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * okhttpClient配置，注意：该类不需要加@Configuration注解
 * https://docs.spring.io/spring-cloud-openfeign/docs/3.1.7-SNAPSHOT/reference/html/#spring-cloud-feign
 * <p>
 * 服务调用原理：https://juejin.cn/post/7045098553902907406
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/5/24 20:37
 */
public class DemoClientOkHttpConfig implements FeignClientConfigurer {

    private okhttp3.OkHttpClient client;

    @Bean
    public Request.Options options() {
        return new Options(1000, 2000);
    }

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(50, 5L, TimeUnit.MINUTES);
    }

    @Bean
    public okhttp3.OkHttpClient client(Options options, ConnectionPool connectionPool) {
        client = new okhttp3.OkHttpClient.Builder()
                //设置连接超时
                .connectTimeout(options.connectTimeout(), options.connectTimeoutUnit())
                //设置读超时
                .readTimeout(options.readTimeout(), options.readTimeoutUnit())
                //设置写超时
                .writeTimeout(options.readTimeout(), options.readTimeoutUnit())
                //是否可重定向
                .followRedirects(options.isFollowRedirects())
                //连接池定义
                .connectionPool(connectionPool).build();
        client.dispatcher().setMaxRequests(500);
        client.dispatcher().setMaxRequestsPerHost(50);
        return client;
    }

    @Bean
    public Client feignClient(okhttp3.OkHttpClient client) {
        return new OkHttpClient(client);
    }

//    @Bean
//    public Contract contract() {
//        return new SpringMvcContract();
//    }

//    @Bean
//    public Logger.Level level() {
//        return Level.FULL;
//    }

//    @Bean
//    public Retryer retry() {
//        return Retryer.NEVER_RETRY;
//    }

    @PreDestroy
    public void destroy() {
        if (this.client != null) {
            this.client.dispatcher().executorService().shutdown();
            this.client.connectionPool().evictAll();
        }
    }

}
