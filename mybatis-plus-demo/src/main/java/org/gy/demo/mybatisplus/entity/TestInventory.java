package org.gy.demo.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author gy
 * @since 2025-08-10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("test_inventory")
public class TestInventory extends Model<TestInventory> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer qty;

    @Version
    private Integer version;

    private Integer productId;

    public static final String ID = "id";

    public static final String QTY = "qty";

    public static final String VERSION = "version";

    public static final String PRODUCT_ID = "product_id";

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
