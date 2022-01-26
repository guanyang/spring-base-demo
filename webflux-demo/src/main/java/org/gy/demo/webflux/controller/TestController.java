package org.gy.demo.webflux.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    private WebClient webClient;

    @GetMapping("/hello")
    public Mono<Map> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "test");
        result.put("time", System.currentTimeMillis());
        return Mono.just(result);
    }


    @GetMapping("/hello/{times}")
    public Mono<Map> hello(@PathVariable int times) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "test");
        result.put("time", System.currentTimeMillis());
        return Mono.delay(Duration.ofMillis(times)).thenReturn(result);
    }

    @GetMapping("/v2/hello/{times}")
    public Mono<Map> hello2(@PathVariable int times) {
        CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
            Map<String, Object> result = new HashMap<>();
            result.put("name", "test");
            result.put("time", System.currentTimeMillis());
            try {
                Thread.sleep(times);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        });
        return Mono.fromFuture(future);
    }

    @GetMapping("/list")
    public Mono<String> list() {
        return webClient.get()
            .uri("http://127.0.0.1:8080/api/test/v2/hello/8000")
            .retrieve().bodyToMono(String.class);
    }

}
