package my.cache.model;

import my.cache.interfaces.Cacher;
import my.cache.interfaces.MessageHandler;

public class MessageJoin implements MessageHandler {
   String client;

    public MessageJoin(String client) {
        this.client = client;
    }

    public String getClient() {
        return client;
    }

    public void setKey(String client) {
        this.client = client;
    }

    public String toString() {
        return "MessageJoin [" + new String(client) + "]";
    }


    @Override
    public String getCommand() {
        return "JOIN";
    }

}
