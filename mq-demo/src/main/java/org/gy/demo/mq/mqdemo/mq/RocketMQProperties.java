package org.gy.demo.mq.mqdemo.mq;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/12/22 11:54
 */
@Data
@Accessors(chain = true)
public class RocketMQProperties {

    private static final String DEFAULT_INSTANCE_NAME = "DEFAULT";
    private static final String DEFAULT_TAG = "*";
    private static final String DEFAULT_MESSAGE_MODEL = MessageModel.CLUSTERING.name();
    private static final String DEFAULT_CONSUME_FROM = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET.name();

    private String nameServer;

    private String topic;

    private Producer producer;

    private Consumer consumer;

    @Data
    @Accessors(chain = true)
    public static class Producer {

        /**
         * Group name of producer.
         */
        private String groupName;

        private String instanceName = DEFAULT_INSTANCE_NAME;

        /**
         * Namespace for this MQ Producer instance.
         */
        private String namespace;

        /**
         * Millis of send message timeout.
         */
        private int sendMessageTimeout = 3000;

        /**
         * Compress message body threshold, namely, message body larger than 4k will be compressed on default.
         */
        private int compressMessageBodyThreshold = 1024 * 4;

        /**
         * Maximum number of retry to perform internally before claiming sending failure in synchronous mode. This may
         * potentially cause message duplication which is up to application developers to resolve.
         */
        private int retryTimesWhenSendFailed = 2;

        /**
         * <p> Maximum number of retry to perform internally before claiming sending failure in asynchronous mode. </p>
         * This may potentially cause message duplication which is up to application developers to resolve.
         */
        private int retryTimesWhenSendAsyncFailed = 2;

        /**
         * Indicate whether to retry another broker on sending failure internally.
         */
        private boolean retryNextServer = false;

        /**
         * Maximum allowed message size in bytes.
         */
        private int maxMessageSize = 1024 * 1024 * 4;

    }

    @Data
    @Accessors(chain = true)
    public static final class Consumer {

        /**
         * Group name of consumer.
         */
        private String groupName;

        private String instanceName = DEFAULT_INSTANCE_NAME;

        /**
         * Namespace for this MQ Consumer instance.
         */
        private String namespace;

        /**
         * Control message mode, if you want all subscribers receive message all message, broadcasting is a good
         * choice.
         */
        private String messageModel = DEFAULT_MESSAGE_MODEL;

        /**
         * Control which message can be select.
         */
        private String selectorExpression = DEFAULT_TAG;

        /**
         * Batch consumption size
         */
        private int consumeMessageBatchMaxSize = 1;

        /**
         * Minimum consumer thread number
         */
        private int consumeThreadMin = 20;

        /**
         * Max consumer thread number
         */
        private int consumeThreadMax = 20;

        /**
         * @see ConsumeFromWhere
         */
        private String consumeFromWhere = DEFAULT_CONSUME_FROM;

    }

}
