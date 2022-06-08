package org.gy.demo.redisdemo.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.gy.framework.limit.core.ILimitCheckService;
import org.gy.framework.limit.core.support.LimitCheckContext;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/8 17:59
 */
public class CustomLimitCheckService implements ILimitCheckService {

    private static final Map<String, LimitItem> LIMIT_MAP = new ConcurrentHashMap<>();

    @Override
    public String type() {
        return "custom";
    }

    @Override
    public boolean check(LimitCheckContext context) {
        //简单实现，后续考虑key的过期删除
        LimitItem limitItem = LIMIT_MAP.computeIfAbsent(context.getKey(), k -> new LimitItem(context.getTime()));
        return limitItem.incr() > context.getLimit();
    }

    @Data
    public static class LimitItem {

        private long startMillis;

        private long count;

        private long timeMillis;

        public LimitItem(int timeSeconds) {
            this.startMillis = System.currentTimeMillis();
            this.count = 0;
            this.timeMillis = timeSeconds * 1000;
        }

        public synchronized long incr() {
            boolean valid = valid();
            if (!valid) {
                reset();
            }
            count++;
            return count;
        }

        public boolean valid() {
            long cost = System.currentTimeMillis() - startMillis;
            return cost <= timeMillis;
        }

        public void reset() {
            this.startMillis = System.currentTimeMillis();
            this.count = 0;
        }
    }
}
