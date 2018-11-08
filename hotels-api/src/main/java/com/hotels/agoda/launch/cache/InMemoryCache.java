package com.hotels.agoda.launch.cache;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCache<K,V> implements Cache<K,V>{

	
	private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
	
	@Override
	public void put(K key, V value) {
		cache.put(key, value);
		
		
	}

	@Override
	public void remove(K key) {
		cache.remove(key);
		
	}

	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public void clear() {
       cache.clear();
		
	}
	
	@Override
	public Collection<V> getAllValues() {
		return cache.values();
	}

}
