package my.cache.commands;


import my.cache.exceptions.InvalidCommand;
import my.cache.interfaces.Cacher;
import my.cache.model.MessageGet;
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
    public static void handleRemove(MessageGet messageGet, Cacher cacher) {
        cacher.remove(messageGet.getKey());
        //System.out.println("messageRemove = " + messageGet);
    }
    public static String handleJoin(String follower, Map<String, Socket> followers, Socket socket) {
        if(followers.containsKey(follower)) throw new InvalidCommand();
        followers.put(follower, socket);
        return "201";
    }
}
