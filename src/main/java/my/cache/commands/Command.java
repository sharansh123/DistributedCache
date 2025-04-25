package my.cache.commands;


import my.cache.model.MessageGet;
import my.cache.model.MessageSet;

public class Command {

    public static void handleSet(MessageSet messageSet) {
        System.out.println("messageSet = " + messageSet);
    }
    public static void handleGet(MessageGet messageGet) {
        System.out.println("messageGet = " + messageGet);
    }
    public static void handleRemove(MessageGet messageGet) {
        System.out.println("messageRemove = " + messageGet);
    }
}
