package com.td.dcts.eso.experience.util;

import java.util.HashMap;
import java.util.Map;

public class InMemoryCache {
	
	public static final String KEY_CLIENT_CREDENTIALS_TOKEN = "KEY_CLIENT_CREDENTIALS_TOKEN";
	
	private static Map<String, Object> cache;
	
	static {
		//sessionContext = new HashMap<String, Map<String, Object>>();
		cache = new HashMap<String, Object>();
	}

	public static void addToCache(String key, Object value) {
		cache.put(key, value);
	}
	
	public static Object getObjectFromCache(String key) {
		return cache.get(key);
	}
	
	public static boolean isKeyExistInCache(String key) {
		return cache.containsKey(key);
	}
	
	public String toString() {
		return cache.toString();
	}

}
