package my.cache.logic;

import my.cache.commands.Command;
import my.cache.exceptions.ForbiddenRequest;
import my.cache.exceptions.InvalidCommand;
import my.cache.interfaces.Cacher;
import my.cache.interfaces.Connection;
import my.cache.interfaces.MessageHandler;
import my.cache.model.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

//To-Do: Create Proto Buffers
//to-do: create basic load balancer
//to-do: create basic sentinel for monitoring and raft consensus
//to-do: replication of data
//to-do: server sync implementation
public class Server {
    ServerOpts serverOpts;
    Cacher cacher;
    public Logger logger = Logger.getLogger(Server.class.getName());
    public Connection connection;

    public Server(ServerOpts serverOpts, CacherImpl cacher, Connection connection) {
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
        while(true){
            Object data = connection.read(inputStream);
            MessageHandler message = (MessageHandler) data;
            if(message == null) continue;
            logger.info(data.toString());
            Thread.ofVirtual().start(() -> {
                try {
                    handleCommand(data ,outputStream, clientSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            //printWriter.println("Sending a message back: "+ line);
        }
    }

    public void handleCommand(Object data, OutputStream outputStream, Socket clientSocket) throws IOException {
        try {
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
                    Command.handleSet((MessageSet) data, cacher);
                    if(serverOpts.getIsLeader()) sendToFollowers(data, message.getCommand());
                    connection.write(new MessageStatus("200", " SET OK"), outputStream, "STATUS");
                    break;
                }

                case "REMOVE": {
                    Command.handleRemove((MessageRemove) data, cacher);
                    if(serverOpts.getIsLeader()) sendToFollowers(data, message.getCommand());
                    connection.write(new MessageStatus("200", "REMOVE OK"), outputStream, "STATUS");
                    break;
                }

                case "JOIN": {
                    Command.handleJoin((MessageJoin)data, serverOpts.getFollowers(), clientSocket);
                    logger.info(serverOpts.getFollowers().values().toString());
                    connection.write(new MessageStatus("200", "JOIN OK"), outputStream, "STATUS");
                    break;
                }
                case "STATUS": {
                    logger.info(data.toString());
                    break;
                }
                default:
                    throw new InvalidCommand();
            }
        } catch (InvalidCommand e) {
            logger.severe("Could not parse message: " + data);
            connection.write(new MessageStatus("400", "Invalid command"), outputStream, e.getMessage());
        } catch (IOException e) {
            logger.severe("Couldn't Connect: " + e.getMessage());
            //printWriter.println("Status: 500");
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

