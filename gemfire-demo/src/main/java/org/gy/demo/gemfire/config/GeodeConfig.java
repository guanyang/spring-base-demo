package org.gy.demo.gemfire.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.geode.config.annotation.EnableClusterAware;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Configuration
@EnableEntityDefinedRegions(basePackages = "org.gy.demo.gemfire.entity")
@EnableGemfireRepositories(basePackages = "org.gy.demo.gemfire.repository")
@EnableClusterAware
public class GeodeConfig {

}
