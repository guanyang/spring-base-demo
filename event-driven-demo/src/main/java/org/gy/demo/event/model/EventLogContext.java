package org.gy.demo.event.model;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author gy
 */
@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventLogContext<REQ extends EventMessage<?>, RES> implements Serializable {

    private static final long serialVersionUID = -3716537966923659221L;

    private String requestId;

    private REQ request;

    private RES response;

    private Throwable ex;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static <T, R> R handleWithLog(EventMessage<T> req, Function<T, R> function) {
        Objects.requireNonNull(req, "EventSendReq is required!");
        Objects.requireNonNull(function, "Function is required!");
        EventLogContext<EventMessage<?>, Object> ctx = EventLogContext.builder().requestId(req.getRequestId()).request(req).build();
        try {
            R response = function.apply(req.getData());
            // 保存事件日志（异步）
            ctx.setResponse(response);
            return response;
        } catch (Throwable e) {
            // 保存事件日志（异步）
            ctx.setEx(e);
            throw e;
        } finally {
            handleEventLog(Collections.singletonList(ctx));
        }
    }

    public static void handleEventLog(List<EventLogContext<EventMessage<?>, Object>> ctxList) {
        if (CollectionUtil.isEmpty(ctxList)) {
            log.warn("[handleEventLog]Param ctxList is empty");
            return;
        }
        // 保存事件日志，后续考虑异步保存到DB
        log.info("[handleEventLog]ctxList:{}", ctxList);
    }

    public static <T> void handleEventLog(List<EventMessage<T>> eventMessages, Throwable ex) {
        if (CollectionUtil.isEmpty(eventMessages)) {
            log.warn("[handleEventLog]Param eventMessages is empty");
            return;
        }
        List<EventLogContext<EventMessage<?>, Object>> ctxList = eventMessages.stream().map(msg -> EventLogContext.builder().requestId(msg.getRequestId()).request(msg).ex(ex).build()).collect(Collectors.toList());
        // 保存事件日志（异步）
        handleEventLog(ctxList);
    }

}
