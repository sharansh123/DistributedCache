package my.cache.logic;

import lombok.Data;
import my.cache.interfaces.Cacher;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ConcurrentCache implements Cacher {

    Map<String,byte[]> cache;
    ArrayBlockingQueue<Long> setTimes;
    ArrayBlockingQueue<Long> getTimes;
    ArrayBlockingQueue<Long> removeTimes;

    public ConcurrentCache() {
        cache = new ConcurrentHashMap<>();
        this.setTimes = new ArrayBlockingQueue<>(10000000);
        this.getTimes = new ArrayBlockingQueue<>(10000000);
        this.removeTimes = new ArrayBlockingQueue<>(10000000);
    }


    @Override
    public void set(byte[] key, byte[] value, int ttl) {
        long st = System.nanoTime();
        String k = new String(key, StandardCharsets.ISO_8859_1);
        this.cache.putIfAbsent(k, value);
        TimeTicker.tickAndRemove(ttl, key, this);
        setTimes.add(System.nanoTime()-st);
    }

    @Override
    public byte[] get(byte[] key) {
        long st = System.nanoTime();
        String k = new String(key, StandardCharsets.ISO_8859_1);
        try{
            if(this.cache.containsKey(k)) return this.cache.get(k);
            return "".getBytes(StandardCharsets.UTF_8);
        } finally {
            getTimes.add(System.nanoTime()-st);
        }
    }

    @Override
    public void remove(byte[] key) {
        long st = System.nanoTime();
        try{
            this.cache.remove(new String(key, StandardCharsets.ISO_8859_1));
        } finally {
            removeTimes.add(System.nanoTime()-st);
        }
    }

    @Override
    public boolean has(byte[] key) {
        return false;
    }
}
