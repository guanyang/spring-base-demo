package org.gy.demo.configdemo.deferredresult;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/19 17:55
 */
public class DefaultDeferredResultHolder<K, V> implements DeferredResultHolder<K, ResponseEntity<V>> {

    public static final long TIMEOUT = 30 * 1000;//30 seconds

    private final Map<K, DeferredResult<ResponseEntity<V>>> deferredResults = new ConcurrentHashMap<>();

    @Override
    public DeferredResult<ResponseEntity<V>> newDeferredResult(K key, long timeoutMs, ResponseEntity<V> timeoutResult) {
        Objects.requireNonNull(key, "key must not be null");
        DeferredResult<ResponseEntity<V>> deferredResult = new DeferredResult<>(timeoutMs, timeoutResult);
        add(key, deferredResult);
        deferredResult.onCompletion(() -> remove(key));
        return deferredResult;
    }

    @Override
    public void add(K key, DeferredResult<ResponseEntity<V>> deferredResult) {
        Optional.ofNullable(key).ifPresent(k -> deferredResults.put(k, deferredResult));
    }

    @Override
    public DeferredResult<ResponseEntity<V>> get(K key) {
        return Optional.ofNullable(key).map(deferredResults::get).orElse(null);
    }

    @Override
    public void remove(K key) {
        Optional.ofNullable(key).ifPresent(deferredResults::remove);
    }

    @Override
    public void handleDeferredData(K key, ResponseEntity<V> deferredData) {
        Optional.ofNullable(get(key)).ifPresent(r -> r.setResult(deferredData));
    }

}
