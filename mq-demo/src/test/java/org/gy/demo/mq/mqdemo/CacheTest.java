package org.gy.demo.mq.mqdemo;

import cn.hutool.core.collection.CollectionUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Ticker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/8/17 20:38
 */
@Slf4j
public class CacheTest {

    private static final LoadingCache<Integer, Optional<PropInfoRes>> propCache = buildCache();

    private static LoadingCache<Integer, Optional<PropInfoRes>> buildCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            //定义缓存对象失效的时间精度为纳秒级
            .ticker(Ticker.systemTicker())
            //设置缓存容器的初始容量为10
            .initialCapacity(10)
            .softValues()
            //设置缓存最大容量为10000，超过10000之后就会按照LRU最近虽少使用算法来移除缓存项
            .maximumSize(100)
            //设置要统计缓存的命中率
            .recordStats()
            .removalListener((k, v, cause) -> log.debug(k + " 该key已被移除,原因:" + cause))
            //LoadingCache不允许返回null值，所以用Optional包装
            .build(id -> {
                List<PropInfoRes> propertyList = queryPropInfoBatch(Lists.newArrayList(id));
                return Optional.ofNullable(CollectionUtil.isNotEmpty(propertyList) ? propertyList.get(0) : null);
            });
    }

    private static List<PropInfoRes> queryPropInfoBatch(Collection<Integer> ids) {
        List<PropInfoRes> result = Lists.newArrayList();
        if (CollectionUtil.isEmpty(ids)) {
            return result;
        }
        for (Integer id : ids) {
            log.info("[loadData]加载数据：id={}", id);
            if (id == 2) {
                //模拟数据不存在的场景
                continue;
            }
            PropInfoRes data = new PropInfoRes();
            data.setId(id);
            data.setName(UUID.randomUUID().toString());
            result.add(data);
        }
        return result;
    }

    public static List<PropInfoRes> getPropInfoBatch(Collection<Integer> collection) {
        Collection<Integer> noCachePropCollection = collection.stream()
            .filter(e -> Objects.isNull(propCache.getIfPresent(e)))
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noCachePropCollection)) {
            List<PropInfoRes> propInfoResList = queryPropInfoBatch(collection);
            propInfoResList.forEach(e -> propCache.put(e.getId(), Optional.of(e)));
        }
        return collection.stream().map(e -> getPropInfo(e)).collect(Collectors.toList());
    }

    public static Map<Integer, PropInfoRes> getPropInfoBatch2(Collection<Integer> collection) {
        Map<Integer, PropInfoRes> result = Maps.newHashMap();
        if (CollectionUtil.isEmpty(collection)) {
            return result;
        }
        Map<Integer, Optional<PropInfoRes>> cacheMap = propCache.getAllPresent(collection);
        Set<Integer> nocacheKeys = collection.stream().filter(id -> !cacheMap.containsKey(id))
            .collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(nocacheKeys)) {
            //批量分批获取，避免超过限制
            List<List<Integer>> groupKeys = Lists.partition(Lists.newArrayList(nocacheKeys), 2);
            for (List<Integer> ids : groupKeys) {
                List<PropInfoRes> propInfoResList = queryPropInfoBatch(ids);
                if (CollectionUtil.isEmpty(propInfoResList)) {
                    continue;
                }
                propInfoResList.forEach(e -> {
                    propCache.put(e.getId(), Optional.of(e));
                    result.put(e.getId(), e);
                });
            }
        }
        cacheMap.forEach((k, v) -> {
            if (v.isPresent()) {
                result.put(k, v.get());
            }
        });
        return result;
    }


    public static PropInfoRes getPropInfo(int propId) {
        return propCache.get(propId).orElse(null);
    }


    public static void main(String[] args) {
        //单个获取
        List<Integer> ids = Lists.newArrayList(1, 2, 3);
        ids.forEach(id -> {
            PropInfoRes propInfo = getPropInfo(id);
            System.out.println(propInfo);
        });

        //批量获取
        Set<Integer> idSet = Sets.newHashSet(1, 2, 3, 4, 5, 6);
        Map<Integer, PropInfoRes> infoBatch2 = getPropInfoBatch2(idSet);
        infoBatch2.forEach((k, v) -> System.out.println(v));
    }


    @lombok.Data
    public static class PropInfoRes {

        private Integer id;

        private String name;
    }

}
