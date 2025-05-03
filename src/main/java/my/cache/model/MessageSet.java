package my.cache.model;

import my.cache.interfaces.Cacher;
import my.cache.interfaces.MessageHandler;

public class MessageSet implements MessageHandler {
    byte[] key;
    byte[] value;
    int ttl;

    public MessageSet(byte[] key, byte[] value, int ttl) {
        this.key = key;
        this.value = value;
        this.ttl = ttl;
    }

    public byte[] getKey() {
        return key;
    }
    public void setKey(byte[] key) {
        this.key = key;
    }
    public byte[] getValue() {
        return value;
    }
    public void setValue(byte[] value) {
        this.value = value;
    }
    public int getTtl() {
        return ttl;
    }
    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String toString() {
        return "MessageSet [" + new String(key) + "," + new String(value) + "," + ttl + "]";
    }

    @Override
    public String getCommand() {
        return "SET";
    }

}
