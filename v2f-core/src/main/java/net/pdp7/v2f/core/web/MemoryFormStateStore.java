package net.pdp7.v2f.core.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryFormStateStore implements FormStateStore {
	/** In-memory, ever-growing cache. Do not use in production */

	protected Map<UUID, Map<String, String[]>> store = new ConcurrentHashMap<>();

	@Override
	public UUID store(Map<String, String[]> formState) {
		UUID key = UUID.randomUUID();
		store.put(key, new HashMap<>(formState));
		return key;
	}

	@Override
	public Map<String, String[]> retrieve(UUID uuid) {
		return store.get(uuid);
	}
}
