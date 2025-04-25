package my.cache.logic;

import my.cache.interfaces.Cacher;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacherImpl implements Cacher {
    Map<String,byte[]> cache;
    Lock writeLock;
    Lock readLock;

    public CacherImpl() {
        this.cache  = new HashMap<>();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();
    }

    public static CacherImpl newCache(){
        return new CacherImpl();
    }

    @Override
    public void set(byte[] key, byte[] value, int ttl) {
        this.writeLock.lock();
        String k = new String(key, StandardCharsets.UTF_8);
        this.cache.putIfAbsent(k, value);
        this.writeLock.unlock();
    }

    @Override
    public byte[] get(byte[] key) {
        this.readLock.lock();
        String k = new String(key, StandardCharsets.UTF_8);
        try{
            if(this.cache.containsKey(k)) return this.cache.get(k);
            return null;
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void remove(byte[] key) {
        this.writeLock.lock();
        try{
            this.cache.remove(new String(key, StandardCharsets.UTF_8));
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean has(byte[] key) {
        this.readLock.lock();
        String k = new String(key, StandardCharsets.UTF_8);
        try{
            return this.cache.containsKey(k);
        } finally {
            this.readLock.unlock();
        }
    }
}
