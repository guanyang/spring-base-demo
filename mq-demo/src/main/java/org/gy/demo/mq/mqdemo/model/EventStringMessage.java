package org.gy.demo.mq.mqdemo.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author gy
 */

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class EventStringMessage extends EventMessage<String> {

    private static final long serialVersionUID = 1717921447038023733L;

}
