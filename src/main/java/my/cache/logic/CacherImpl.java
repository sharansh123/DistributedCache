package my.cache.logic;

import lombok.Data;
import my.cache.interfaces.Cacher;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
public class CacherImpl implements Cacher {
    Map<String,byte[]> cache;
    Lock writeLock;
    Lock readLock;
    ArrayBlockingQueue<Long> setTimes;
    ArrayBlockingQueue<Long> getTimes;
    ArrayBlockingQueue<Long> removeTimes;

    public CacherImpl() {
        this.cache  = new HashMap<>();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();
        this.setTimes = new ArrayBlockingQueue<>(10000000);
        this.getTimes = new ArrayBlockingQueue<>(10000000);
        this.removeTimes = new ArrayBlockingQueue<>(10000000);
    }

    public static CacherImpl newCache(){
        return new CacherImpl();
    }

    @Override
    public void set(byte[] key, byte[] value, int ttl) {
        long st = System.nanoTime();
        this.writeLock.lock();
        String k = new String(key, StandardCharsets.ISO_8859_1);
        this.cache.putIfAbsent(k, value);
        this.writeLock.unlock();
        TimeTicker.tickAndRemove(ttl, key, this);
        setTimes.add(System.nanoTime()-st);
    }

    @Override
    public byte[] get(byte[] key) {
        long st = System.nanoTime();
        this.readLock.lock();
        String k = new String(key, StandardCharsets.ISO_8859_1);
        try{
            if(this.cache.containsKey(k)) return this.cache.get(k);
            return "".getBytes(StandardCharsets.UTF_8);
        } finally {
            this.readLock.unlock();
            getTimes.add(System.nanoTime()-st);
        }
    }

    @Override
    public void remove(byte[] key) {
        long st = System.nanoTime();
        this.writeLock.lock();
        try{
            this.cache.remove(new String(key, StandardCharsets.ISO_8859_1));
        } finally {
            this.writeLock.unlock();
            removeTimes.add(System.nanoTime()-st);
        }
    }

    @Override
    public boolean has(byte[] key) {
        this.readLock.lock();
        String k = new String(key,StandardCharsets.ISO_8859_1);
        try{
            return this.cache.containsKey(k);
        } finally {
            this.readLock.unlock();
        }
    }
}
