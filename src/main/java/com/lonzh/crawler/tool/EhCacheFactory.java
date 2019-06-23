package com.lonzh.crawler.tool;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * 缓存工厂类
 * 
 * @author LZ
 * 
 */
public class EhCacheFactory {
	private static CacheManager cacheManager;

	static {
		// 创建缓存管理器
		cacheManager = CacheManager.create();
	}

	/**
	 * 获取缓存实例对象
	 * 
	 * @param cacheName
	 *            缓存名称
	 * @return
	 */
	public static Cache getCacheInstance(String cacheName) {
		Cache cache = null;
		if (cacheManager == null) {
			cacheManager = CacheManager.create();
		}
		try {
			cache = cacheManager.getCache(cacheName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cache;
	}

	public static void shutdown() {
		if (cacheManager != null) {
			cacheManager.shutdown();
			cacheManager = null;
		}
	}


}
