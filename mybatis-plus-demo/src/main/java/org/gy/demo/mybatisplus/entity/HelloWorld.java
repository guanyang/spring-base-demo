package org.gy.demo.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 简单演示表
 * </p>
 *
 * @author gy
 * @since 2021-11-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("hello_world")
public class HelloWorld extends Model<HelloWorld> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String sayHello;

    private String yourName;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreated;

    /**
     * 更新时间
     */
    private LocalDateTime gmtModified;

    /**
     * 是否逻辑删除
     */
    private Integer isDeleted;


    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
