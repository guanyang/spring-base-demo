package org.gy.demo.dubbo.api.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Data
public class TestRequest implements Serializable {

    private static final long serialVersionUID = -747705355147439338L;

    private String name;

    private Integer age;
}
