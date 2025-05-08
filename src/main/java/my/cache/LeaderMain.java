package my.cache;

import my.cache.interfaces.Connection;
import my.cache.logic.*;
import my.cache.model.RaftOpts;
import my.cache.model.ServerOpts;

import java.io.*;

public class LeaderMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerOpts serverOpts = fetchServerOpts(args);
        Connection connection =  new ByteConnection();
        Server server = new Server(serverOpts, CacherImpl.newCache(), connection);
        RaftServer raftServer = new RaftServer(new RaftOpts(serverOpts.getListenAddress()+1, serverOpts.getLeaderAddress()+1, serverOpts.getIsLeader()), new ByteConnection());
        System.out.println("Running server...");
        Thread.ofVirtual().start(() -> {
            try {
                raftServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread.ofVirtual().start(() -> TimeTicker.tickHeartBeat(2000, raftServer.raftOpts.getFollowers(), "leader", raftServer.connection, raftServer.raftOpts.getHeartBeatTracker()));
        server.Start();
    }

    private static ServerOpts fetchServerOpts(String[] args) {
        if(args.length != 2) throw new RuntimeException();
        return new ServerOpts(Integer.parseInt(args[0]), Integer.parseInt(args[0]) == Integer.parseInt(args[1]) , Integer.parseInt(args[1]));
    }
}