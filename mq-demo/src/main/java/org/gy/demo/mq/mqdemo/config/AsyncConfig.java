package org.gy.demo.mq.mqdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.gy.framework.core.exception.BizException;
import org.gy.framework.core.util.JsonUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author gy
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(20);
        // 设置最大线程数
        executor.setMaxPoolSize(50);
        // 设置队列容量
        executor.setQueueCapacity(200);
        // 设置空闲秒数
        executor.setKeepAliveSeconds(30);
        // 设置默认线程名称
        executor.setThreadNamePrefix("async-task-");
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(60);
        executor.setTaskDecorator(new ContextCopyingDecorator());
        // 初始化
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            if (throwable instanceof BizException) {
                log.warn("异步任务异常WARN：方法：{} 参数：{} ", method.getName(), JsonUtils.toJson(params), throwable);
            } else {
                log.error("异步任务异常：方法：{} 参数：{} ", method.getName(), JsonUtils.toJson(params), throwable);
            }
        };
    }
}
