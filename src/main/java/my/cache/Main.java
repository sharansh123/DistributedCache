package my.cache;

import my.cache.interfaces.Cacher;
import my.cache.logic.CacherImpl;
import my.cache.logic.Server;
import my.cache.model.ServerOpts;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server(new ServerOpts(3000, true), CacherImpl.newCache());
        System.out.println("Hello, World!");
        Thread.ofVirtual().start(server::Start);
        Thread.sleep(1000);
        Socket clientSocket = new Socket("127.0.0.1", 3000);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.ISO_8859_1));
        out.println("SET abc 111 2000");
        Thread.sleep(500);
        out.println("GET abc");
        System.out.println(in.readLine());
        Thread.sleep(500);
        out.println("SET abcd 1112 3000");
        Thread.sleep(500);
        out.println("GET abcd");
        System.out.println(in.readLine());
        Thread.sleep(500);
        out.println("GET abc");
        System.out.println(in.readLine());
        Thread.sleep(2000);
        out.println("GET abcd");
        System.out.println(in.readLine());
        Thread.sleep(500);
//        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        System.out.println(in.readLine());
//        System.out.println(in.readLine());
        clientSocket.close();
    }
}