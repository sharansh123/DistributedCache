package my.cache.commands;


import my.cache.exceptions.InvalidCommand;
import my.cache.interfaces.Cacher;
import my.cache.model.MessageGet;
import my.cache.model.MessageJoin;
import my.cache.model.MessageRemove;
import my.cache.model.MessageSet;

import java.net.Socket;
import java.util.Map;

public class Command {

    public static void handleSet(MessageSet messageSet, Cacher cacher) {
        cacher.set(messageSet.getKey(), messageSet.getValue(), messageSet.getTtl());
        //System.out.println("messageSet = " + messageSet);
    }
    public static String handleGet(MessageGet messageGet, Cacher cacher) {
        return new String(cacher.get(messageGet.getKey()));
        //System.out.println("messageGet = " + messageGet);
    }
    public static void handleRemove(MessageRemove messageRemove, Cacher cacher) {
        cacher.remove(messageRemove.getKey());
        //System.out.println("messageRemove = " + messageGet);
    }
    public static void handleJoin(MessageJoin messageJoin, Map<String, Socket> followers, Socket socket) {
        if(followers.containsKey(messageJoin.getClient())) throw new InvalidCommand();
        followers.put(messageJoin.getClient(), socket);
    }
}
