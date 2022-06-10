package org.gy.demo.redisdemo.model;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;
import org.gy.framework.core.util.JsonUtils;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = -8427966812117449553L;

    private Integer id;

    private String name;

    private Long time;

    private Info info;

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }

    @Data
    @Accessors(chain = true)
    public static class Info implements Serializable {

        private static final long serialVersionUID = -2026019309273247311L;

        private String address;

        private String tel;

        @Override
        public String toString() {
            return JsonUtils.toJson(this);
        }
    }
}
