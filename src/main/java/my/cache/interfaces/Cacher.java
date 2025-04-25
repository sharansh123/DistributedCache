package my.cache.interfaces;

public interface Cacher {
    public void set(byte[] key, byte[] value, int ttl);
    public byte[] get(byte[] key);
    public void remove(byte[] key);
    public boolean has(byte[] key);
}
