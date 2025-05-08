package my.cache.logic;

import my.cache.interfaces.Cacher;
import my.cache.interfaces.Connection;
import my.cache.model.MessageHeartBeat;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Logger;

public class TimeTicker {

    public static final Logger logger = Logger.getLogger(TimeTicker.class.getName());

    //Possible Improvement With Blocking Queue to deal with backpressure?
    public static void tickAndRemove(int ttl, byte[] key, Cacher cacher) {
        Thread.ofVirtual().start(
                () -> {
                    try {
                        Thread.sleep(ttl);
                        cacher.remove(key);
                        logger.info("Removed " + new String(key) + " from cache");
                    } catch (InterruptedException e) {
                        logger.severe("Couldn't remove key: " + new String(key));
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static void tickHeartBeat(int heartBeatInMillis, Map<String, Socket> followers, String from, Connection connection, HeartBeatTracker heartBeatTracker) {
        try{
            while(true) {
                Thread.sleep(heartBeatInMillis);
                long time = System.currentTimeMillis();
                heartBeatTracker.cleanAndAddHeartBeat(time);
                MessageHeartBeat messageHeartBeat = new MessageHeartBeat(WALStorage.getOffset(), from, time);
                byte[] heartBeatInBytes = connection.marshall(messageHeartBeat, "HEARTBEAT");
                logger.info("Sending heartbeat: " + time + " " + heartBeatTracker.getHeartBeatCount(time));
                for (String follower : followers.keySet()) {
                    Thread.ofVirtual().start(() -> {
                        try {
                            followers.get(follower).getOutputStream().write(heartBeatInBytes);
                        } catch (IOException e) {
                            logger.severe("Couldn't send heartbeat: " + followers.get(follower).getLocalSocketAddress());
                        }
                    });
                }
            }
        } catch (InterruptedException | IOException e) {
            logger.info("Couldn't send heartbeat: " + e.getMessage());
        }
    }
}
