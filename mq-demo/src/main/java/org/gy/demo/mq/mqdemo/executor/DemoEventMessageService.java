package org.gy.demo.mq.mqdemo.executor;


import org.gy.framework.core.dto.Response;

/**
 * @author guanyang
 */
public interface DemoEventMessageService extends EventMessageService<String, Response<Object>> {

    void dynamicEventTest(String data);
}
