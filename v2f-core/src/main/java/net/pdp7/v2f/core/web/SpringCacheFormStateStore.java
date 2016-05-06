package net.pdp7.v2f.core.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class SpringCacheFormStateStore implements FormStateStore {


	protected final Cache cache;

	public SpringCacheFormStateStore(CacheManager cacheManager, String cacheName) {
		cache = cacheManager.getCache(cacheName);
	}

	@Override
	public UUID store(Map<String, String[]> parameterMap) {
		UUID uuid = UUID.randomUUID();
		cache.put(uuid, parameterMap);
		return uuid;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String[]> retrieve(UUID fromString) {
		return (Map<String, String[]>) cache.get(fromString).get();
	}

}
