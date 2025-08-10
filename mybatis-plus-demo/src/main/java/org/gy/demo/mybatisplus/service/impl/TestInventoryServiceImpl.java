package org.gy.demo.mybatisplus.service.impl;

import org.gy.demo.mybatisplus.entity.TestInventory;
import org.gy.demo.mybatisplus.mapper.TestInventoryMapper;
import org.gy.demo.mybatisplus.service.ITestInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gy
 * @since 2025-08-10
 */
@Service
public class TestInventoryServiceImpl extends ServiceImpl<TestInventoryMapper, TestInventory> implements ITestInventoryService {

    @Override
    public int batchUpsertSelective(List<TestInventory> list) {
        return baseMapper.batchUpsertSelective(list);
    }

    @Override
    public int batchUpdateQty(List<TestInventory> list) {
        return baseMapper.batchUpdateQty(list);
    }
}
