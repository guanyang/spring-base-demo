package org.gy.demo.mybatisplus.config;

import com.baomidou.dynamic.datasource.plugin.MasterSlaveAutoRoutingPlugin;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/11/26 16:54
 */
@Configuration
@MapperScan("org.gy.demo.mybatisplus.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //分页插件，参考文档：https://mp.baomidou.com/guide/page.html
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        //乐观锁插件，参考文档：https://mp.baomidou.com/guide/interceptor-optimistic-locker.html#optimisticlockerinnerinterceptor
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        //防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        //数据变动记录插件
//        interceptor.addInnerInterceptor(new DataChangeRecorderInnerInterceptor());
        return interceptor;
    }

    /**
     * 读写分离配置
     */
//    @Bean
//    public MasterSlaveAutoRoutingPlugin masterSlaveAutoRoutingPlugin() {
//        return new MasterSlaveAutoRoutingPlugin();
//    }

    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }

}
