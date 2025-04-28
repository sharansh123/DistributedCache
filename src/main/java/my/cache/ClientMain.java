package my.cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket clientSocket = new Socket("127.0.0.1", 4000);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.ISO_8859_1);
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
        Thread.sleep(2100);
        out.println("GET abcd");
        System.out.println(in.readLine());
        Thread.sleep(500);
        clientSocket.close();
    }
}
