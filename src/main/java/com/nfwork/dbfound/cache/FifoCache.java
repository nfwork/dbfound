package com.nfwork.dbfound.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class FifoCache<K, V> {

    private final int sizeLimit;

    private final Function<K, V> generator;

    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

    private final ConcurrentLinkedQueue<K> queue = new ConcurrentLinkedQueue<>();

    private final ReentrantLock lock = new ReentrantLock();

    private volatile int size;

    /**
     * Create a new cache instance with the given limit and generator function.
     * @param sizeLimit the maximum number of entries in the cache
     * (0 indicates no caching, always generating a new value)
     * @param generator a function to generate a new value for a given key
     */
    public FifoCache(int sizeLimit, Function<K, V> generator) {
        this.sizeLimit = sizeLimit;
        this.generator = generator;
    }

    /**
     * Retrieve an entry from the cache, potentially triggering generation
     * of the value.
     * <p>
     * Cache hits do not update eviction order, keeping the read path lock-free.
     *
     * @param key the key to retrieve the entry for
     * @return the cached or newly generated value
     */
    public V get(K key) {
        if (this.sizeLimit == 0) {
            return this.generator.apply(key);
        }

        V cached = this.cache.get(key);
        if (cached != null) {
            return cached;
        }

        V value = this.generator.apply(key);
        this.lock.lock();
        try {
            // Retrying in case another thread cached the same key while we generated the value.
            cached = this.cache.get(key);
            if (cached != null) {
                return cached;
            }
            if (this.size == this.sizeLimit) {
                K oldest = this.queue.poll();
                if (oldest != null) {
                    this.cache.remove(oldest);
                }
            }
            this.queue.offer(key);
            this.cache.put(key, value);
            this.size = this.cache.size();
            return value;
        }
        finally {
            this.lock.unlock();
        }
    }

    /**
     * Determine whether the given key is present in this cache.
     * @param key the key to check for
     * @return {@code true} if the key is present,
     * {@code false} if there was no matching key
     */
    public boolean contains(K key) {
        return this.cache.containsKey(key);
    }

    /**
     * Immediately remove the given key and any associated value.
     * @param key the key to evict the entry for
     * @return {@code true} if the key was present before,
     * {@code false} if there was no matching key
     */
    public boolean remove(K key) {
        this.lock.lock();
        try {
            boolean wasPresent = (this.cache.remove(key) != null);
            this.queue.remove(key);
            this.size = this.cache.size();
            return wasPresent;
        }
        finally {
            this.lock.unlock();
        }
    }

    /**
     * Immediately remove all entries from this cache.
     */
    public void clear() {
        this.lock.lock();
        try {
            this.cache.clear();
            this.queue.clear();
            this.size = 0;
        }
        finally {
            this.lock.unlock();
        }
    }

    /**
     * Return the current size of the cache.
     * @see #sizeLimit()
     */
    public int size() {
        return this.size;
    }

    /**
     * Return the maximum number of entries in the cache
     * (0 indicates no caching, always generating a new value).
     * @see #size()
     */
    public int sizeLimit() {
        return this.sizeLimit;
    }
}
