package org.gy.demo.kafka.kafkademo.listener.support;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.gy.demo.kafka.kafkademo.trace.TraceContext;
import org.gy.demo.kafka.kafkademo.trace.TraceEnum;
import org.springframework.kafka.listener.RecordInterceptor;

import java.util.Optional;

/**
 * @author guanyang
 */
public class ConsumerContextInterceptor implements RecordInterceptor<Object, Object> {

    @Override
    public ConsumerRecord<Object, Object> intercept(ConsumerRecord<Object, Object> consumerRecord) {
        Header header = Optional.ofNullable(consumerRecord.headers().lastHeader(TraceEnum.TRACE.getName())).orElse(null);
        if (header != null) {
            TraceContext.setTrace(new String(header.value()));
        }
        return consumerRecord;
    }

    @Override
    public void afterRecord(ConsumerRecord<Object, Object> record, Consumer<Object, Object> consumer) {
        TraceContext.clearTrace();
    }
}
