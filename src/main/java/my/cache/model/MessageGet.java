package my.cache.model;

public class MessageGet {
    byte[] key;

    public MessageGet(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }
    public void setKey(byte[] key) {
        this.key = key;
    }

    public String toString() {
        return "MessageGet [" + new String(key) + "]";
    }
}
