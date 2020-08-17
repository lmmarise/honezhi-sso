package com.honezhi.sso.server.cache.annotaion;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @author： tsb
 * @date： 2020/8/17
 * @description：自定义注解,用于判定是否启用ehcache缓存
 * @modifiedBy：
 * @version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
@Conditional(EhcacheCacheConditionImpl.class)
public @interface EhcacheCondition {
}
