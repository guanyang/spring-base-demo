package org.gy.demo.redisdemo.config;

import java.text.MessageFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 功能描述：定义缓存key
 *
 * @author gy
 * @version 1.0.0
 */
public class RedisCacheKey {

    /**
     * 防止缓存穿透，默认缓存15s
     */
    public static final int DEFAULT_NULL_EXPIRE = 15;
    /**
     * 防止缓存穿透，默认缓存空值NULL
     */
    public static final String DEFAULT_NULL_VALUE = "_DEFAULT_NULL";


    /**
     * 功能描述: 获取Redis中的key
     *
     * @param cacheKeyEnum 枚举数据
     * @param arrays 需要替换的数据，数组格式（统一采用字符串格式）
     * @return String Redis中的真实key
     */
    public static String getKey(CacheKeyEnum cacheKeyEnum, String... arrays) {
        return MessageFormat.format(cacheKeyEnum.getPattern(), arrays);
    }

    /**
     * 功能描述: 定义缓存key<br>
     * <ul>
     * <strong>请严格遵守以下规则，否则后果自负!</strong>
     *
     * <li>key的用途：cache缓存，lock锁，persist持久化</li>
     *
     * <li>枚举格式要求：{key的用途}_{功能}_KEY</li>
     *
     * <li>占位符要求：占位符可以定义多个或者不定义，示例：{0}:{1}</li>
     *
     * <li>pattern格式要求：{APPCODE}:{key的用途}:{功能}:{占位符}</li>
     *
     * </ul>
     */
    @AllArgsConstructor
    @Getter
    public enum CacheKeyEnum {

        //缓存key示例
        CACHE_DEMO_KEY("wms:cache:demo:{0}", 1800, "示例");
        /**
         * key值
         */
        private final String pattern;
        /**
         * 过期时间，单位：秒
         */
        private final int expireTime;
        /**
         * 描述
         */
        private final String description;
    }

}
