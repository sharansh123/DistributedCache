package my.cache;

import my.cache.interfaces.Cacher;
import my.cache.logic.CacherImpl;
import my.cache.logic.Server;
import my.cache.model.ServerOpts;

import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server(new ServerOpts(3000, true), CacherImpl.newCache());
        System.out.println("Hello, World!");
        Thread.ofVirtual().start(server::Start);
        Thread.sleep(1000);
        Socket clientSocket = new Socket("127.0.0.1", 3000);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("Hello, Server!");
        Thread.sleep(1000);
        out.println("Second Message");
        Thread.sleep(1000);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println(in.readLine());
        System.out.println(in.readLine());
        clientSocket.close();
    }
}