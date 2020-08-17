package com.honezhi.sso.server.cache.annotaion;

import com.google.common.base.Strings;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * @author： tsb
 * @date： 2020/8/17
 * @description： 根据配置文件中的cache类型来自动切换 redis 或 ehcache 缓存
 * @modifiedBy：
 * @version: 1.0
 */
public class EhcacheCacheConditionImpl implements Condition {

    /** 启动的配置 */
    // 缓存类型
    private static String CONFIG_PROPERTY_NAME = "spring.cache.type";
    // 默认使用redis缓存
    private static String cacheType = "ehcache";

    /**
     * @return true 为 ehcache 缓存
     */
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String propertyValue = conditionContext.getEnvironment().getProperty(CONFIG_PROPERTY_NAME);
        return !Strings.isNullOrEmpty(propertyValue) && propertyValue.equalsIgnoreCase(cacheType);
    }
}
