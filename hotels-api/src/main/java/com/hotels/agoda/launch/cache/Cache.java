package com.hotels.agoda.launch.cache;

import java.util.Collection;
import java.util.List;

public interface Cache<K, V> {

	void put(K key, V value);
	 
    void remove(K key);
 
    V get(K key);
    
    void clear();
    
    Collection<V> getAllValues();

}
