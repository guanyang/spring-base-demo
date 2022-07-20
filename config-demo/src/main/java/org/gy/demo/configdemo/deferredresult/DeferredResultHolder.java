package org.gy.demo.configdemo.deferredresult;

import org.springframework.web.context.request.async.DeferredResult;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/19 17:49
 */
public interface DeferredResultHolder<K, V> {

    DeferredResult<V> newDeferredResult(K key, long timeoutMs, V timeoutResult);

    void add(K key, DeferredResult<V> deferredResult);

    DeferredResult<V> get(K key);

    void remove(K key);

    void handleDeferredData(K key, V deferredData);

}
