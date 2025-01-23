package org.gy.demo.mq.mqdemo.executor.support;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mq.mqdemo.executor.EventMessageService;
import org.gy.demo.mq.mqdemo.model.EventType;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class EventMessageServiceManager implements InitializingBean, DisposableBean, Ordered {

    private static final ThreadLocal<EventMessageService> CURRENT_SERVICE = new NamedThreadLocal<>("Current EventMessageService");
//    private static final ThreadLocal<EventMessageService> CURRENT_SERVICE = new TransmittableThreadLocal<>();

    private static final Map<EventType, EventMessageService> SERVICE_MAP = Maps.newConcurrentMap();

    @Resource
    private List<EventMessageService> eventMessageServices;

    public static boolean isCurrentServiceActive() {
        return getCurrentService() != null;
    }

    public static void setCurrentService(EventMessageService service) {
        CURRENT_SERVICE.set(service);
    }

    public static EventMessageService getCurrentService() {
        return CURRENT_SERVICE.get();
    }

    public static void clearCurrentService() {
        CURRENT_SERVICE.remove();
    }

    public static <T, R> Optional<EventMessageService<T, R>> getService(EventType eventType) {
        return Optional.ofNullable(eventType).map(SERVICE_MAP::get);
    }

    public static void register(EventMessageService service) {
        if (service == null || service.getEventType() == null) {
            return;
        }
        //禁止注册相同的事件，避免覆盖导致业务错误
        EventType eventType = service.getEventType();
        if (SERVICE_MAP.containsKey(eventType)) {
            throw new IllegalArgumentException("Event type already registered: " + eventType);
        }
        SERVICE_MAP.put(eventType, service);
    }

    @Override
    public void afterPropertiesSet() {
        for (EventMessageService actionService : eventMessageServices) {
            register(actionService);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void destroy() throws Exception {
        SERVICE_MAP.clear();
    }
}
