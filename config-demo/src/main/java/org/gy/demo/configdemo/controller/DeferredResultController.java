package org.gy.demo.configdemo.controller;

import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.configdemo.deferredresult.DefaultDeferredResultHolder;
import org.gy.demo.configdemo.deferredresult.DeferredResultHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/19 18:43
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class DeferredResultController {

    private DeferredResultHolder<String, ResponseEntity<Object>> sampleholder = new DefaultDeferredResultHolder<>();

    @GetMapping("/call/{key}")
    public DeferredResult<ResponseEntity<Object>> call(@PathVariable String key) {
        Map<String, Object> map = new HashMap<>();
        map.put("time", System.currentTimeMillis());
        map.put("msg", "timeout");
        map.put("key", key);
        return sampleholder.newDeferredResult(key, 10000, ResponseEntity.status(REQUEST_TIMEOUT).body(map));
    }

    @GetMapping("/set/{key}")
    public Object set(@PathVariable String key) {
        Map<String, Object> map = new HashMap<>();
        map.put("time", System.currentTimeMillis());
        map.put("msg", "success");
        map.put("key", key);
        sampleholder.handleDeferredData(key, ResponseEntity.ok(map));
        return map;
    }

}
