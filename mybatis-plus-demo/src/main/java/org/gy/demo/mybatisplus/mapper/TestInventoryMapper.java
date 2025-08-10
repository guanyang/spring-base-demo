package org.gy.demo.mybatisplus.mapper;

import org.gy.demo.mybatisplus.entity.TestInventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
*  Mapper 接口
* </p>
*
* @author gy
* @since 2025-08-10
*/
public interface TestInventoryMapper extends BaseMapper<TestInventory> {

    /**
    * 单个插入或更新（插入和更新都只处理非空字段）
    * @param entity 要插入或更新的实体
    * @return 影响的行数
    */
    int upsertSelective(TestInventory entity);

    /**
    * 批量插入或更新（插入和更新都只处理非空字段）
    * @param list 要插入或更新的实体列表
    * @return 影响的行数
    */
    int batchUpsertSelective(@Param("list") List<TestInventory> list);

    /**
     * 批量更新库存（插入和更新都只处理非空字段）
     * @param list 要插入或更新的实体列表
     * @return 影响的行数
     */
    int batchUpdateQty(@Param("list") List<TestInventory> list);

}
