package org.gy.demo.mybatisplus.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class TestResponse {

    private String name;

    private Integer age;

    private Long time;
}
