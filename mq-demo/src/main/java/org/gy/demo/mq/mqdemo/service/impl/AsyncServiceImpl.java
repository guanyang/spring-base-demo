package org.gy.demo.mq.mqdemo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mq.mqdemo.service.AsyncService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author gy
 */
@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {
    @Async
    @Override
    public void async(String msg) {
        log.info("AsyncService async: {}", msg);
    }
}
