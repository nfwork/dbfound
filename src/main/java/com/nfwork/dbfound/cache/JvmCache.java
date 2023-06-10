package com.nfwork.dbfound.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class JvmCache<K, V> {

    private final Function<K, V> generator;

    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

    public JvmCache(Function<K, V> generator) {
        this.generator = generator;
    }

    public V get(K key) {
        V cached = this.cache.get(key);
        if (cached != null) {
            return cached;
        }
        V value = this.generator.apply(key);
        this.cache.putIfAbsent(key, value);
        return value;
    }

    public boolean contains(K key) {
        return this.cache.containsKey(key);
    }

    public boolean remove(K key) {
        return (this.cache.remove(key) != null);
    }

    public void clear() {
        this.cache.clear();
    }
}
