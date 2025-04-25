package my.cache.logic;

import my.cache.model.ServerOpts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


public class Server {
    ServerOpts serverOpts;
    CacherImpl cacher;
    public Logger logger = Logger.getLogger(Server.class.getName());

    public Server(ServerOpts serverOpts, CacherImpl cacher) {
        this.serverOpts = serverOpts;
        this.cacher = cacher;
    }

    public void Start() {
        try(ServerSocket serverSocket = new ServerSocket(serverOpts.getListenAddress())){
            logger.info("Listening on port " + serverOpts.getListenAddress());
            while(true){
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
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        while(true){
            String line = bufferedReader.readLine();
            if(line == null) continue;
            logger.info("Received Message "  + line);
            //printWriter.println("Sending a message back: "+ line);
        }
    }
}

