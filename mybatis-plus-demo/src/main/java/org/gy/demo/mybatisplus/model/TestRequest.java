package org.gy.demo.mybatisplus.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;


/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class TestRequest {



    @NotNull(message = "name不能为空")
    @Length(min = 2, max = 32, message = "name字符数只能介于2~32之间")
    private String name;

    @NotNull(message = "age不能为空")
    @Range(min = 1, max = 100, message = "age只能介于1~100之间")
    private Integer age;
}
