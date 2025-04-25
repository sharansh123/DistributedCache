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
        out.println("GET abc");
        Thread.sleep(500);
        out.println("SET abc 111 245");
        Thread.sleep(500);
        out.println("GET abc 123");
        Thread.sleep(500);
        out.println("SET abc 111 245 qa");
        Thread.sleep(500);
        out.println("SET abc 111 245q");
        Thread.sleep(500);
        out.println("REMOVE abc 111 245");
        Thread.sleep(500);
        out.println("REMOVE abc");
        Thread.sleep(500);
//        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        System.out.println(in.readLine());
//        System.out.println(in.readLine());
        clientSocket.close();
    }
}