package my.cache.logic;

import my.cache.interfaces.Cacher;

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
}
