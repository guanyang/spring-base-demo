package org.gy.demo.mybatisplus.service;

import org.apache.ibatis.annotations.Param;
import org.gy.demo.mybatisplus.entity.TestInventory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gy
 * @since 2025-08-10
 */
public interface ITestInventoryService extends IService<TestInventory> {

    /**
     * 批量插入或更新（插入和更新都只处理非空字段）
     * @param list 要插入或更新的实体列表
     * @return 影响的行数
     */
    int batchUpsertSelective(List<TestInventory> list);

    /**
     * 批量更新库存（插入和更新都只处理非空字段）
     * @param list 要插入或更新的实体列表
     * @return 影响的行数
     */
    int batchUpdateQty(List<TestInventory> list);

}
