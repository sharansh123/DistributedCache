package my.cache.logic;

import my.cache.commands.Command;
import my.cache.exceptions.ForbiddenRequest;
import my.cache.exceptions.InvalidCommand;
import my.cache.interfaces.Cacher;
import my.cache.model.MessageGet;
import my.cache.model.MessageSet;
import my.cache.model.ServerOpts;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


public class Server {
    ServerOpts serverOpts;
    Cacher cacher;
    public Logger logger = Logger.getLogger(Server.class.getName());

    public Server(ServerOpts serverOpts, CacherImpl cacher) {
        this.serverOpts = serverOpts;
        this.cacher = cacher;
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
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.ISO_8859_1));
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.ISO_8859_1);
        while(true){
            //logger.info("Waiting for message...");
            String line = bufferedReader.readLine();
            if(line == null || line.isEmpty()) continue;
            logger.info(line);
            Thread.ofVirtual().start(() -> {
                try {
                    handleCommand(line,printWriter, clientSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            //printWriter.println("Sending a message back: "+ line);
        }
    }

    public void handleCommand(String message, PrintWriter printWriter, Socket clientSocket) throws IOException {
        try {
            String[] splitMessage = message.split(" ");
            String command = splitMessage[0];
            switch (command) {
                case "GET": {
                    if(splitMessage.length > 2) throw new InvalidCommand();
                    printWriter.println(Command.handleGet(new MessageGet(splitMessage[1].getBytes(StandardCharsets.UTF_8)), this.cacher));
                    break;
                }
                case "SET": {
                    if(splitMessage.length > 4) throw new InvalidCommand();
                    Command.handleSet(new MessageSet(splitMessage[1].getBytes(StandardCharsets.UTF_8), splitMessage[2].getBytes(StandardCharsets.UTF_8), Integer.parseInt(splitMessage[3])), this.cacher);
                    printWriter.println("Set Status: 201");
                    if(serverOpts.getIsLeader()) sendToFollowers(message);
                    break;
                }
                case "REMOVE": {
                    if(splitMessage.length > 2) throw new InvalidCommand();
                    if(!serverOpts.getIsLeader()) throw new ForbiddenRequest();
                    Command.handleRemove(new MessageGet(splitMessage[1].getBytes(StandardCharsets.UTF_8)), this.cacher);
                    printWriter.println("Remove Status: 200");
                    if(serverOpts.getIsLeader()) sendToFollowers(message);
                    break;
                }
                case "JOIN": {
                    //logger.info("Entering join");
                    if(splitMessage.length > 2 && !serverOpts.getIsLeader()) throw new InvalidCommand();
                    String status = Command.handleJoin(splitMessage[1], serverOpts.getFollowers(), clientSocket);
                    logger.info(serverOpts.getFollowers().values().toString());
                    //.println("Status: " + status);
                    break;
                }
                /*case "STATUS": {
                    if(splitMessage.length > 2) throw new InvalidCommand();
                    logger.info(command);
                    break;
                }*/
                default:
                    logger.info(command);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | InvalidCommand e) {
            logger.severe("Could not parse message: " + message);
            printWriter.write("Status: 400");
        } catch (IOException e) {
            logger.severe("Couldn't Connect: " + e.getMessage());
            //printWriter.println("Status: 500");
        }
    }

    private void sendToFollowers(String command) throws IOException {
        for(String key: serverOpts.getFollowers().keySet()){
            Socket socket = serverOpts.getFollowers().get(key);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.ISO_8859_1);
            Thread.ofVirtual().start(() -> {
                logger.info(command + ": Sending it to " + key);
                printWriter.println(command);
            });
        }
    }
}

