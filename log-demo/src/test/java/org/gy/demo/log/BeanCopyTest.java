package org.gy.demo.log;

import com.alibaba.fastjson.JSON;
import com.github.houbb.bean.mapping.asm.util.AsmBeanUtil;
import com.github.houbb.bean.mapping.core.util.BeanUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.assertj.core.util.Lists;
import org.dozer.DozerBeanMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/5/12 10:53
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Threads(2)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class BeanCopyTest {

    @Param({"1000"})
    private int loop;

    private List<SourceBean> sources;

    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    private static final ModelMapper modelMapper = new ModelMapper();

    private static final DozerBeanMapper dozerMapper = new DozerBeanMapper();

    private static final BeanCopier beanCopier = BeanCopier.create(SourceBean.class, TargetBean.class, false);

    static {
        mapperFactory.classMap(SourceBean.class, TargetBean.class)
//            .field("source", "target")  // 字段不一致时可以指定
            .byDefault().register();
        modelMapper.addMappings(new PropertyMap<SourceBean, TargetBean>() {
            @Override
            protected void configure() {
                // 属性值不一样可以自己操作
//                map().setTarget(source.getSource());
            }
        });
    }

    @Setup
    public void setup() {
        sources = buildSources(loop);
    }

    @TearDown
    public void tearDown() {
        sources = null;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(BeanCopyTest.class.getSimpleName()).build();
        new Runner(opt).run();
//        List<SourceBean> sources = buildSources(5);
//        List<TargetBean> targetBeans = doCopy(sources, (s, t) -> beanCopier.copy(s, t, null));
//        targetBeans.forEach(System.out::println);

    }

    @Benchmark
    public void apacheBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, s -> {
            TargetBean targetBean = new TargetBean();
            try {
                org.apache.commons.beanutils.BeanUtils.copyProperties(targetBean, s);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return targetBean;
        });
        bh.consume(targetBeans);
    }

    @Benchmark
    public void beanCopierBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, (s, t) -> beanCopier.copy(s, t, null));
        bh.consume(targetBeans);
    }

    @Benchmark
    public void beanMappingBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, BeanUtil::copyProperties);
        bh.consume(targetBeans);
    }

    @Benchmark
    public void beanAsmBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, AsmBeanUtil::copyProperties);
        bh.consume(targetBeans);
    }

    @Benchmark
    public void dozerBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, s -> dozerMapper.map(s, TargetBean.class));
        bh.consume(targetBeans);
    }

    @Benchmark
    public void fastJsonBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, s -> JSON.parseObject(JSON.toJSONString(s), TargetBean.class));
        bh.consume(targetBeans);
    }

    @Benchmark
    public void mapStructBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, BeanMapping.INSTANCE::map);
        bh.consume(targetBeans);
    }

    @Benchmark
    public void modelMapperBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, modelMapper::map);
        bh.consume(targetBeans);
    }

    @Benchmark
    public void orikaBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, mapperFactory.getMapperFacade()::map);
        bh.consume(targetBeans);
    }

    @Benchmark
    public void springBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, BeanUtils::copyProperties);
        bh.consume(targetBeans);
    }

    @Benchmark
    public void getSetBeanCopy(Blackhole bh) {
        List<TargetBean> targetBeans = doCopy(sources, BeanCopyTest::getSetMapping);
        bh.consume(targetBeans);
    }


    private static List<TargetBean> doCopy(List<SourceBean> sources, Function<SourceBean, TargetBean> beanCopy) {
        return sources.stream().map(beanCopy).collect(Collectors.toList());
    }

    private static List<TargetBean> doCopy(List<SourceBean> sources, BiConsumer<SourceBean, TargetBean> beanCopy) {
        return sources.stream().map(s -> {
            TargetBean targetBean = new TargetBean();
            beanCopy.accept(s, targetBean);
            return targetBean;
        }).collect(Collectors.toList());
    }

    private static List<SourceBean> buildSources(Integer num) {
        List<SourceBean> sources = Lists.newArrayList();
        for (int j = 0; j < num; j++) {
            int index = ThreadLocalRandom.current().nextInt();
            boolean flag = ThreadLocalRandom.current().nextBoolean();
            SourceBean sourceBean = new SourceBean();
            sourceBean.setAge(index);
            sourceBean.setTitle("title" + index);
            sourceBean.setSource("source" + index);
            sourceBean.setEat(flag);
            sourceBean.setStart(new Date());
            sourceBean.setItem(new Item(System.currentTimeMillis(), System.currentTimeMillis()));
            sources.add(sourceBean);
        }
        return sources;
    }

    public static TargetBean getSetMapping(SourceBean source) {
        TargetBean targetBean = new TargetBean();

        targetBean.setAge(source.getAge());
        targetBean.setTitle(source.getTitle());
        targetBean.setEat(source.getEat());
        targetBean.setStart(source.getStart());
        targetBean.setItem(source.getItem());

        return targetBean;
    }

    @Mapper
    interface BeanMapping {

        BeanMapping INSTANCE = Mappers.getMapper(BeanMapping.class);

        TargetBean map(SourceBean source);

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SourceBean {

        private Integer age;

        private String title;

        private String source;

        private Boolean eat;

        private Date start;

        private Item item;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TargetBean {

        private Integer age;

        private String title;

        private String target;

        private Boolean eat;

        private Date start;

        private Item item;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {

        private Long startTime;

        private Long endTime;
    }

}
