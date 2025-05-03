package my.cache.interfaces;

public interface Cacher {
    void set(byte[] key, byte[] value, int ttl);
    byte[] get(byte[] key);
    void remove(byte[] key);
    boolean has(byte[] key);
}
