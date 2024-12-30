package org.gy.demo.kafka.kafkademo.listener.support;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.gy.demo.kafka.kafkademo.trace.TraceEnum;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanyang
 */
public class ProducerContextInterceptor implements ProducerInterceptor<String, Object> {
    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> producerRecord) {
        wrapTrace(producerRecord, TraceEnum.TRACE);
        wrapTrace(producerRecord, TraceEnum.SPAN);
        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }

    private static <K, V> void wrapTrace(ProducerRecord<K, V> message, TraceEnum keyEnum) {
        String name = keyEnum.getName();
        Optional.ofNullable(MDC.get(name)).ifPresent(v -> message.headers().add(name, v.getBytes(StandardCharsets.UTF_8)));
    }
}
