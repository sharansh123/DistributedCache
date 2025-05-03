package my.cache.model;

import my.cache.interfaces.Cacher;
import my.cache.interfaces.MessageHandler;

public class MessageStatus implements MessageHandler {
    String status;
    String val;

    public MessageStatus(String status, String val) {
        this.status = status;
        this.val = val;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String toString() {
        return "MessageStatus [" + new String(status) + "," + new String(val) + "]";
    }

    @Override
    public String getCommand() {
        return "STATUS";
    }
}
