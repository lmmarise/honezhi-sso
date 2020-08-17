package com.honezhi.sso.server.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author： tsb
 * @date： 2020/8/17
 * @description：缓存接口
 * @modifiedBy：tsb
 * @version: 1.0
 */
public interface CacheService<K, V> {
    Boolean expire(K key, long timeout, TimeUnit timeUnit);

    Boolean delete(K key);

    void set(K key, V value);

    void set(K key, V value, long timeout);

    void set(K key, V value, long timeout, TimeUnit timeUnit);

    V get(K key);
}
