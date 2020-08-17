package com.honezhi.sso.server.cache;

import com.honezhi.sso.server.cache.annotaion.EhcacheCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 限制 key 只能为 String
 *
 * @author： tsb
 * @date： 2020/8/17
 * @description：以 Ehcache 作为缓存
 * @modifiedBy：tsb
 * @version: 1.0
 */
@Service
@EhcacheCondition   // 为true就初始化本bean
public class StringEhcacheCacheService<String, V> implements CacheService<String, V> {

    @Resource
    private CacheManager cacheManager;

    private Cache cache = null;

    @PostConstruct
    public void cacheTest() {
        // 显示所有的Cache空间
        System.out.println(StringUtils.join(cacheManager.getCacheNames(), ","));
        cache = cacheManager.getCache("SsoCache");
        // cache.put("key", "123");
        // java.lang.String res = cache.get("key", java.lang.String.class);
    }

    //=====================================common start=====================================


    @Override
    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        // 设置过期时间
        return redisTemplate.expire(key, timeout, timeUnit);
    }


    @Override
    public Boolean delete(String key) {
        cache.evict(key);
        return true;
    }

    //=====================================common end=====================================

    //=====================================key value start=====================================

    @Override
    public void set(String key, V value) {
        // redisTemplate.opsForValue().set(key, value);
        cache.put(key, value);
    }


    @Override
    public void set(String key, V value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }


    @Override
    public void set(String key, V value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }


    @Cacheable(value = {"Product#5#2"},key ="#id")
    @Override
    public V get(String key) {
        Cache.ValueWrapper valueWrapper = cache.get(key);
        return (V) valueWrapper.get();
    }


    //=====================================key value  end=====================================


}
