package org.gy.demo.mq.mqdemo.mq;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/12/22 18:44
 */
@ExtRocketMQTemplateConfiguration(nameServer = "${rocketmq.demo.nameServer}", group = "${rocketmq.demo.producer.groupName}")
public class ExtRocketMQTemplate extends RocketMQTemplate {

}
