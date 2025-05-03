package my.cache.model;

public class MessageRemove {
    byte[] key;

    public MessageRemove(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public String toString() {
        return "MessagerRemove [" + new String(key) + "]";
    }
}
