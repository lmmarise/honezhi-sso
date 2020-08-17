package com.honezhi.sso.server.controller.strategy_pattern;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author： tsb
 * @date： 2020/8/17
 * @description：
 * @modifiedBy：
 * @version: 1.0
 */
@Component
@PropertySource("classpath:cache.yml")
@ConfigurationProperties(prefix = "sso.server.cache")
@Data
public class CacheConfig {
    @Value("${dialect-class-name}")
    private String className;
}
