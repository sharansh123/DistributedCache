package my.cache.model;

import my.cache.interfaces.Cacher;
import my.cache.interfaces.MessageHandler;

public class MessageGet implements MessageHandler {
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

    @Override
    public String getCommand() {
        return "GET";
    }


}
