package my.cache;

import my.cache.logic.CacherImpl;
import my.cache.logic.Server;
import my.cache.model.ServerOpts;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FollowerMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        Thread.sleep(1000);
        ServerOpts serverOpts = fetchServerOpts(args);
        Server server = new Server(serverOpts, CacherImpl.newCache());
        System.out.println("Running server...");
        try(Socket leaderSocket = new Socket("127.0.0.1", serverOpts.getLeaderAddress())) {
            Thread.ofVirtual().start(() -> {
                try {
                    server.handleConnection(leaderSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            PrintWriter printWriter = new PrintWriter(leaderSocket.getOutputStream(), true, StandardCharsets.ISO_8859_1);
            printWriter.println("JOIN " + "follower-" + serverOpts.getListenAddress());
            server.Start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ServerOpts fetchServerOpts(String[] args) {
        if(args.length != 2) throw new RuntimeException();
        return new ServerOpts(Integer.parseInt(args[0]), Integer.parseInt(args[0]) == Integer.parseInt(args[1]) , Integer.parseInt(args[1]));
    }
}