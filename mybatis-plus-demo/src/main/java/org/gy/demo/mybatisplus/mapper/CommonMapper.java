package org.gy.demo.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

/**
 * @author gy
 */
public interface CommonMapper<T> extends BaseMapper<T> {

    /**
     * 真正的批量插入（注意控制插入的数量）
     */

    int insertBatchSomeColumn(List<T> entityList);
}
