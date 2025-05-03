package my.cache.model;

import my.cache.interfaces.Cacher;
import my.cache.interfaces.MessageHandler;

public class MessageRemove implements MessageHandler {
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

    @Override
    public String getCommand() {
        return "REMOVE";
    }
}
