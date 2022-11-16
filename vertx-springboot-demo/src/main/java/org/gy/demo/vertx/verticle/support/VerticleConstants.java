package org.gy.demo.vertx.verticle.support;

import io.vertx.core.DeploymentOptions;
import org.gy.demo.vertx.DemoApplication;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/4 10:46
 */
public class VerticleConstants {

    public static final String SPLIT = ":";

    public static final int DEFAULT_INSTANCES = 1;

    public static final int CORE_NUM = Runtime.getRuntime().availableProcessors();

    public static final DeploymentOptions DEFAULT_OPTIONS = new DeploymentOptions().setInstances(CORE_NUM);

    public static final String DEFAULT_PACKAGE = DemoApplication.class.getPackage().getName();

    private VerticleConstants() {

    }

}
