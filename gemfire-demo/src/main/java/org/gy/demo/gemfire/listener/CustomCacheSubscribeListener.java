package org.gy.demo.gemfire.listener;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.RegionEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/1/19 22:07
 */
public class CustomCacheSubscribeListener extends CacheListenerAdapter<String, Object> {

    @Override
    public void afterCreate(EntryEvent<String, Object> event) {
        System.out.printf("afterCreate key = %s value = %s", event.getKey(), event.getNewValue());
    }

    @Override
    public void afterDestroy(EntryEvent<String, Object> event) {
        super.afterDestroy(event);
    }

    @Override
    public void afterInvalidate(EntryEvent<String, Object> event) {
        super.afterInvalidate(event);
    }

    @Override
    public void afterRegionDestroy(RegionEvent<String, Object> event) {
        super.afterRegionDestroy(event);
    }

    @Override
    public void afterRegionCreate(RegionEvent<String, Object> event) {
        super.afterRegionCreate(event);
    }

    @Override
    public void afterRegionInvalidate(RegionEvent<String, Object> event) {
        super.afterRegionInvalidate(event);
    }

    @Override
    public void afterUpdate(EntryEvent<String, Object> event) {
        System.out.printf("afterUpdate key = %s value = %s", event.getKey(), event.getNewValue());
    }

    @Override
    public void afterRegionClear(RegionEvent<String, Object> event) {
        super.afterRegionClear(event);
    }

    @Override
    public void afterRegionLive(RegionEvent<String, Object> event) {
        super.afterRegionLive(event);
    }
}
