package my.cache;

import my.cache.interfaces.Connection;
import my.cache.logic.*;
import my.cache.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FollowerMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        Thread.sleep(1000);
        HeartBeatTracker heartBeatTracker = new HeartBeatTracker(5);
        ServerOpts serverOpts = fetchServerOpts(args);
        Connection connection = new ByteConnection();
        serverOpts.setHeartBeatTracker(heartBeatTracker);
        Server server = new Server(serverOpts, new ConcurrentCache(),connection);
        RaftServer raftServer = new RaftServer(new RaftOpts(serverOpts.getListenAddress()+1, serverOpts.getLeaderAddress()+1, serverOpts.getIsLeader(), heartBeatTracker), new ByteConnection());
        System.out.println("Running server...");
        try(Socket leaderSocket = new Socket("127.0.0.1", serverOpts.getLeaderAddress());Socket leaderRaftSocket = new Socket("127.0.0.1", raftServer.raftOpts.getLeaderAddress()) ) {
            connectToServerAndJoin(server, leaderSocket);
            connectToRaftAndJoin(raftServer, leaderRaftSocket);
            server.Start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void connectToServerAndJoin(Server server, Socket leaderSocket) throws IOException {
        Thread.ofVirtual().start(() -> {
            try {
                server.handleConnection(leaderSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        server.connection.write(new MessageJoin("follower-" + server.serverOpts.getListenAddress()), leaderSocket.getOutputStream(), "JOIN");
    }

    private static void connectToRaftAndJoin(RaftServer server, Socket leaderSocket) throws IOException {
        Thread.ofVirtual().start(() -> {
            try {
                server.handleConnection(leaderSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        server.connection.write(new MessageJoin("follower-raft-" + server.raftOpts.getListenAddress()), leaderSocket.getOutputStream(), "JOIN");
    }

    private static ServerOpts fetchServerOpts(String[] args) {
        if(args.length != 2) throw new RuntimeException();
        return new ServerOpts(Integer.parseInt(args[0]), Integer.parseInt(args[0]) == Integer.parseInt(args[1]) , Integer.parseInt(args[1]));
    }
}