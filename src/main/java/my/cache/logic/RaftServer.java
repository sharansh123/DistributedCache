package my.cache.logic;

import my.cache.commands.Command;
import my.cache.exceptions.InvalidCommand;
import my.cache.interfaces.Connection;
import my.cache.interfaces.MessageHandler;
import my.cache.model.MessageHeartBeat;
import my.cache.model.MessageJoin;
import my.cache.model.MessageStatus;
import my.cache.model.RaftOpts;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class RaftServer {
    public RaftOpts raftOpts;
    public static Logger logger = Logger.getLogger(RaftServer.class.getName());
    public Connection connection;

    public RaftServer(RaftOpts raftOpts, Connection connection) {
        this.raftOpts = raftOpts;
        this.connection = connection;
    }

    public void start() throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(raftOpts.getListenAddress())){
            while(true){
                Socket socket = serverSocket.accept();
                Thread.ofVirtual().start( () ->
                {
                    try {
                        handleConnection(socket);
                    } catch (IOException e) {
                        logger.severe("Couldn't handle connection");

                    }
                }
                );
            }
        }
    }

    public void handleConnection(Socket socket) throws IOException {
        while(true){
            Object data  = this.connection.read(socket.getInputStream());
            if(data == null) continue;
            Thread.ofVirtual().start(()->{
                try {
                    handleCommand(data, socket.getOutputStream(), socket);
                } catch (IOException| InvalidCommand e) {
                    logger.severe("Couldn't handle command");
                }
            });

        }
    }

    public void handleCommand(Object data, OutputStream outputStream, Socket clientSocket) throws IOException {
        MessageHandler messageHandler = (MessageHandler) data;
        switch(messageHandler.getCommand()){
            case "HEARTBEAT": {
                if(!raftOpts.getIsLeader()) {
                    if(raftOpts.getDisappear()) break;
                    long timeStamp = ((MessageHeartBeat) data).getTimestamp();
                    String offset = ((MessageHeartBeat) data).getOffset();
                    logger.info("Heartbeat received: " + timeStamp + ", " + offset);
                    if(!offset.equals(WALStorage.getOffset())){
                        raftOpts.setDisappear(true);
                        logger.info("Offset does not match");
                        break;
                    }
                    MessageHeartBeat response = new MessageHeartBeat(WALStorage.getOffset(), "follower-" + raftOpts.getListenAddress(), timeStamp);
                    connection.write(response, outputStream, messageHandler.getCommand());
                } else {
                    long timeStamp = ((MessageHeartBeat) data).getTimestamp();
                    this.raftOpts.getHeartBeatTracker().addHeartBeat(timeStamp);
                }
                break;
            }
            case "JOIN": {
                Command.handleJoin((MessageJoin)data, raftOpts.getFollowers(), clientSocket);
                logger.info(raftOpts.getFollowers().keySet().toString());
                connection.write(new MessageStatus("200", "JOIN OK RAFT"), outputStream, "STATUS");
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
}
