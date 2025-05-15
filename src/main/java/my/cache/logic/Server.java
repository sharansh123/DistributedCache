package my.cache.logic;

import my.cache.commands.Command;
import my.cache.exceptions.ForbiddenRequest;
import my.cache.exceptions.InvalidCommand;
import my.cache.interfaces.Cacher;
import my.cache.interfaces.Connection;
import my.cache.interfaces.MessageHandler;
import my.cache.model.*;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


//to-do: raft consensus
//to-do: multiple leaders
public class Server {

    public ServerOpts serverOpts;
    Cacher cacher;
    public Logger logger = Logger.getLogger(Server.class.getName());
    public Connection connection;


    public Server(ServerOpts serverOpts, Cacher cacher, Connection connection) {
        this.serverOpts = serverOpts;
        this.cacher = cacher;
        this.connection = connection;
    }

    public void Start() {
        try(ServerSocket serverSocket = new ServerSocket(serverOpts.getListenAddress())){
            logger.info("Listening on port " + serverOpts.getListenAddress());
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("Accepted connection from " + socket.getInetAddress().getHostAddress());
                Thread.ofVirtual().start(
                        () -> {
                            try {
                                handleConnection(socket);
                            } catch (IOException e) {
                                logger.info("Could not handle connection from " + socket.getInetAddress().getHostAddress());
                                throw new RuntimeException(e);
                            }
                        }
                );
            }
        } catch (IOException e){
            logger.severe("Could not start server");
        }
    }

    public void handleConnection(Socket clientSocket) throws IOException {

        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        AtomicInteger counter = new AtomicInteger(0);
        while(true){
            if(counter.get() >= 3) {
                logger.severe("Closing connection!");
                clientSocket.close();
                break;
            }
            Object data = connection.read(inputStream);
            if(data == null) continue;
            Thread.ofVirtual().start(() -> {
                try {
                    //logger.info("Received data from " + clientSocket.getLocalSocketAddress());
                    handleCommand(data ,outputStream, clientSocket);
                } catch (IOException | InvalidCommand e) {
                    logger.severe("Could not handle command from " + clientSocket.getInetAddress().getHostAddress());
                    counter.incrementAndGet();
                }
            });
            //printWriter.println("Sending a message back: "+ line);
        }
    }

    public void handleCommand(Object data, OutputStream outputStream, Socket clientSocket) throws IOException {
        MessageHandler message = (MessageHandler) data;
        switch (message.getCommand()) {
            case "GET": {
                String val = Command.handleGet((MessageGet) data, cacher);
                MessageStatus messageStatus = new MessageStatus("200", val);
                if(val.isEmpty()) messageStatus = new MessageStatus("404", "Not Found");
                connection.write(messageStatus,outputStream, "STATUS");
                break;
            }

            case "SET": {
                if(serverOpts.getIsLeader()){
                    if(serverOpts.getHeartBeatTracker().getHeartBeatCount(System.currentTimeMillis()) <= serverOpts.getFollowers().size()/2) {
                        logger.info("Not able to set key: " + serverOpts.getHeartBeatTracker().getHeartBeatCount(System.currentTimeMillis()));
                        connection.write(new MessageStatus("500", " NOT OK"), outputStream, "STATUS");
                        break;
                    }
                    connection.write(new MessageStatus("200", "SET OK"), outputStream, "STATUS");
                    sendToFollowers(data, message.getCommand());
                }
                logger.info("Setting the key!");
                Command.handleSet((MessageSet) data, cacher);
                break;
            }
            case "REMOVE": {
                Command.handleRemove((MessageRemove) data, cacher);
                if(serverOpts.getIsLeader()) sendToFollowers(data, message.getCommand());
                connection.write(new MessageStatus("200", "REMOVE OK"), outputStream, "STATUS");
                break;
            }
            case "JOIN": {
                Command.handleJoin((MessageJoin) data, serverOpts.getFollowers(), clientSocket);
                logger.info(serverOpts.getFollowers().keySet().toString());
                connection.write(new MessageStatus("200", "JOIN OK"), outputStream, "STATUS");
                break;
            }
            case "STATUS": {
                logger.info(data.toString());
                break;
            }
            default: {
                connection.write(new MessageStatus("400", "Invalid command"), outputStream, "STATUS");
                throw new InvalidCommand();
            }
        }
    }

    private void sendToFollowers(Object data, String command) throws IOException {
        for(String key: serverOpts.getFollowers().keySet()){
            Socket socket = serverOpts.getFollowers().get(key);
            Thread.ofVirtual().start(() -> {
                logger.info(command + ": Sending it to " + key);
                try {
                    connection.write(data,socket.getOutputStream(), command);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}

