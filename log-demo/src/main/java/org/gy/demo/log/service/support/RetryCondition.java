package org.gy.demo.log.service.support;

import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import java.net.ConnectException;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/4/25 16:48
 */
public class RetryCondition implements RetryWhen {

    @Override
    public boolean retryWhen(ForestRequest req, ForestResponse res) {
        //仅模拟重试条件，正常场景需要根据业务定制
        Throwable exception = res.getException();
        return exception instanceof ConnectException;
    }
}
