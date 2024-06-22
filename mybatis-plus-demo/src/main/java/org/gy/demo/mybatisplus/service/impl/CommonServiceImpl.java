package org.gy.demo.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.gy.demo.mybatisplus.mapper.CommonMapper;
import org.gy.demo.mybatisplus.service.ICommonService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gy
 */
public class CommonServiceImpl<M extends CommonMapper<T>, T> extends ServiceImpl<M, T> implements ICommonService<T> {


    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     * @param batchSize 插入批次数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatchSomeColumn(List<T> entityList, int batchSize) {
        Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
        return batchConsumer(entityList, batchSize, baseMapper::insertBatchSomeColumn);
    }
}
