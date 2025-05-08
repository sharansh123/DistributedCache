package my.cache;

import my.cache.interfaces.Connection;
import my.cache.logic.ByteConnection;
import my.cache.model.MessageGet;
import my.cache.model.MessageSet;
import my.cache.model.MessageStatus;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientMain {
    public static void main(String[] args) throws IOException, InterruptedException, RocksDBException {
        Socket clientSocket = new Socket("127.0.0.1", 4000);
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        Connection connection = new ByteConnection();
        connection.write(new MessageSet("abc".getBytes(StandardCharsets.UTF_8), "111".getBytes(StandardCharsets.UTF_8),2000), outputStream, "SET");
        MessageStatus messageStatus = (MessageStatus) connection.read(inputStream);
        System.out.println(messageStatus);
        Thread.sleep(500);
        connection.write(new MessageGet("abc".getBytes(StandardCharsets.UTF_8)), outputStream, "GET");
        messageStatus = (MessageStatus) connection.read(inputStream);
        System.out.println(messageStatus);
        Thread.sleep(500);
        connection.write(new MessageSet("abcd".getBytes(StandardCharsets.UTF_8), "1112".getBytes(StandardCharsets.UTF_8),3000), outputStream, "SET");
        messageStatus = (MessageStatus) connection.read(inputStream);
        System.out.println(messageStatus);
        Thread.sleep(500);
        connection.write(new MessageGet("abcd".getBytes(StandardCharsets.UTF_8)), outputStream, "GET");
        messageStatus = (MessageStatus) connection.read(inputStream);
        System.out.println(messageStatus);
        Thread.sleep(500);
        connection.write(new MessageGet("abc".getBytes(StandardCharsets.UTF_8)), outputStream, "GET");
        messageStatus = (MessageStatus) connection.read(inputStream);
        System.out.println(messageStatus);
        Thread.sleep(2100);
        connection.write(new MessageGet("abcd".getBytes(StandardCharsets.UTF_8)), outputStream, "GET");
        messageStatus = (MessageStatus) connection.read(inputStream);
        System.out.println(messageStatus);
        Thread.sleep(500);
        clientSocket.close();
        


    }
}
