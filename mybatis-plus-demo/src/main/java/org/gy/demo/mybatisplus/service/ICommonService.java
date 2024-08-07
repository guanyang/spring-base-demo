package org.gy.demo.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gy
 */
public interface ICommonService<T> extends IService<T> {

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean insertBatchSomeColumn(List<T> entityList) {
        return insertBatchSomeColumn(entityList, DEFAULT_BATCH_SIZE);
    }

    @Transactional(rollbackFor = Exception.class)
    default boolean updateBatchByWrapper(Collection<T> entityList,
        Function<T, LambdaQueryWrapper<T>> wrapperFunction) {
        return updateBatchByWrapper(entityList, wrapperFunction, DEFAULT_BATCH_SIZE);
    }

    boolean updateBatchByWrapper(Collection<T> entityList, Function<T, LambdaQueryWrapper<T>> wrapperFunction,
        int batchSize);

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     * @param batchSize 插入批次数量
     */
    boolean insertBatchSomeColumn(List<T> entityList, int batchSize);

    default boolean batchConsumer(List<T> list, int batchSize, Consumer<List<T>> consumer) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        int size = list.size();
        for (int i = 0; i < size; i += batchSize) {
            int toIndex = Math.min(i + batchSize, size);
            consumer.accept(list.subList(i, toIndex));
        }
        return true;
    }

    default <R> List<R> batchFunction(List<T> list, int batchSize, Function<List<T>, List<R>> function) {
        List<R> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        int size = list.size();
        for (int i = 0; i < size; i += batchSize) {
            int toIndex = Math.min(i + batchSize, size);
            List<T> newList = list.subList(i, toIndex);
            result.addAll(function.apply(newList));
        }
        return result;
    }


}
