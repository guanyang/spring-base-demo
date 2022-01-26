package org.gy.demo.mybatis.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import cn.org.atool.fluent.mybatis.functions.TableSupplier;
import java.io.Serializable;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * HelloWorldEntity: 数据映射实体定义
 *
 * @author Powered By Fluent Mybatis
 */
@SuppressWarnings({"unchecked"})
@Data
@Accessors(
    chain = true
)
@EqualsAndHashCode(
    callSuper = false
)
@FluentMybatis(
    table = "hello_world",
    schema = "demo"
)
public class HelloWorldEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   */
  @TableId("id")
  private Long id;

  /**
   * 创建时间
   */
  @TableField("gmt_created")
  private Date gmtCreated;

  /**
   * 更新时间
   */
  @TableField("gmt_modified")
  private Date gmtModified;

  /**
   * 是否逻辑删除
   */
  @TableField("is_deleted")
  private Integer isDeleted;

  /**
   */
  @TableField("say_hello")
  private String sayHello;

  /**
   */
  @TableField("your_name")
  private String yourName;

  @Override
  public Serializable findPk() {
    return this.id;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return HelloWorldEntity.class;
  }

  @Override
  public final HelloWorldEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final HelloWorldEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
