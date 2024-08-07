package org.gy.demo.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.gy.demo.mybatisplus.mapper.CommonMapper;
import org.gy.demo.mybatisplus.service.ICommonService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gy
 */
public class CommonServiceImpl<M extends CommonMapper<T>, T> extends ServiceImpl<M, T> implements ICommonService<T> {


    @Override
    public boolean updateBatchByWrapper(Collection<T> entityList, Function<T, LambdaQueryWrapper<T>> wrapperFunction,
        int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE);
        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            ParamMap<Object> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            param.put(Constants.WRAPPER, wrapperFunction.apply(entity));
            sqlSession.update(sqlStatement, param);
        });
    }

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
