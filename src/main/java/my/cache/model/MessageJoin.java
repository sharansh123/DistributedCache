package my.cache.model;

public class MessageJoin {
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
        return "MessagerJoin [" + new String(client) + "]";
    }
}
