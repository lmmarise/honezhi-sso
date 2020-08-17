package com.honezhi.sso.server.cache.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.util.ClassUtils;

/**
 * 1、存数据
 *      EhCacheUtil ehCacheUtil = EhCacheUtil.getInstance();
 *      ehCacheUtil.put(ehCacheUtil.TAXI_CACHE,ehCacheUtil.GROUP_VEHICLE_TREE_KEY,ztrees);
 * 2、取数据
 *      ztrees = (List<Ztree>) ehCacheUtil.get(ehCacheUtil.TAXI_CACHE,ehCacheUtil.GROUP_VEHICLE_TREE_KEY);
 * 3、删除缓存
 *      ehCacheUtil.delete(ehCacheUtil.TAXI_CACHE,ehCacheUtil.GROUP_TREE_KEY);
 *
 * @author： tsb
 * @date： 2020/8/17
 * @description：如果spring缓存注解失效就使用本工具类
 * @modifiedBy：
 * @version: 1.0
 */
public class EhCacheUtil {

    private static final Logger log = LoggerFactory.getLogger(EhCacheUtil.class);

    private static final String PATH = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "config/ehcache.xml";
    private CacheManager manager;
    private static EhCacheUtil ehCache;

    public static final String TAXI_CACHE = "taxiCache";
    public static final String GROUP_TREE_KEY = "groupTreeList";
    public static final String GROUP_VEHICLE_TREE_KEY = "groupVehicleTreeList";

    /**
     * org.springframework.cache.CacheManager	转 		net.sf.ehcache.Cache
     */
    public static Cache ehcache(org.springframework.cache.CacheManager cacheManager, String cacheName) {
        // 获取EhCache的管理器
        EhCacheCacheManager cacheCacheManager = (EhCacheCacheManager) cacheManager;
        net.sf.ehcache.CacheManager ehCacheManager = cacheCacheManager.getCacheManager();
        return ehCacheManager.getCache(cacheName);
    }

    /**
     * 获得缓存配置管理
     *
     * @param path
     */
    private EhCacheUtil(String path) {
        try {
            manager = CacheManager.create(path);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取配置文件错误：{}", e.getMessage());
        }
    }

    /**
     * 初始化缓存管理类
     *
     * @return
     */
    public static EhCacheUtil getInstance() {
        try {
            if (ehCache == null) {
                ehCache = new EhCacheUtil(PATH);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化错误：{}", e.getMessage());
        }
        return ehCache;
    }

    /**
     * 获取Cache类
     *
     * @param cacheName
     * @return
     */
    public Cache getCache(String cacheName) {
        return manager.getCache(cacheName);
    }

    /**
     * 添加缓存数据
     *
     * @param cacheName
     * @param key
     * @param value
     */
    public void put(String cacheName, String key, Object value) {
        try {
            Cache cache = manager.getCache(cacheName);
            Element element = new Element(key, value);
            cache.put(element);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("添加缓存失败：{}", e.getMessage());
        }
    }

    /**
     * 获取缓存数据
     *
     * @param cacheName
     * @param key
     * @return
     */
    public Object get(String cacheName, String key) {
        try {
            Cache cache = manager.getCache(cacheName);
            Element element = cache.get(key);
            return element == null ? null : element.getObjectValue();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取缓存数据失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除缓存数据
     *
     * @param cacheName
     * @param key
     */
    public void delete(String cacheName, String key) {
        try {
            Cache cache = manager.getCache(cacheName);
            cache.remove(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除缓存数据失败：{}", e.getMessage());
        }
    }
}