package my.cache.logic;

import lombok.Data;
import my.cache.interfaces.Cacher;
import my.cache.model.RemoveByte;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

@Data
public class ConcurrentCache implements Cacher {

    Map<String,byte[]> cache;
    ArrayBlockingQueue<Long> setTimes;
    ArrayBlockingQueue<Long> getTimes;
    ArrayBlockingQueue<Long> removeTimes;
    DelayQueue<RemoveByte> removeQueue;

    public ConcurrentCache() {
        cache = new ConcurrentHashMap<>();
        this.setTimes = new ArrayBlockingQueue<>(10000000);
        this.getTimes = new ArrayBlockingQueue<>(10000000);
        this.removeTimes = new ArrayBlockingQueue<>(10000000);
        this.removeQueue = new DelayQueue<>();
        spinUpDelayQueue();
    }

    public void spinUpDelayQueue() {
        Thread.ofVirtual().start(() -> {
           while(true) {
               try {
                   RemoveByte removeByte = this.removeQueue.take();
                   remove(removeByte.getKeyToRemove());
                   System.out.println("Removed: " + Arrays.toString(removeByte.getKeyToRemove()));
               } catch (InterruptedException e) {
                   System.out.println("Unable to remove key");
               }
           }
        });
    }


    @Override
    public void set(byte[] key, byte[] value, int ttl) {
        long st = System.nanoTime();
        String k = new String(key, StandardCharsets.ISO_8859_1);
        this.cache.putIfAbsent(k, value);
        this.removeQueue.add(new RemoveByte(key, ttl));
        //TimeTicker.tickAndRemove(ttl, key, this);
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
