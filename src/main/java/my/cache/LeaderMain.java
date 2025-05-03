package my.cache;

import my.cache.logic.ByteConnection;
import my.cache.logic.CacherImpl;
import my.cache.logic.Server;
import my.cache.model.ServerOpts;

import java.io.*;

public class LeaderMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerOpts serverOpts = fetchServerOpts(args);
        Server server = new Server(serverOpts, CacherImpl.newCache(), new ByteConnection());
        System.out.println("Running server...");
        server.Start();
    }

    private static ServerOpts fetchServerOpts(String[] args) {
        if(args.length != 2) throw new RuntimeException();
        return new ServerOpts(Integer.parseInt(args[0]), Integer.parseInt(args[0]) == Integer.parseInt(args[1]) , Integer.parseInt(args[1]));
    }
}