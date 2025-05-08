package my.cache.logic;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HeartBeatTracker {

    TreeMap<Long, AtomicInteger> heartBeatMap;
    int capacity;

    public HeartBeatTracker(int capacity) {
        this.capacity = capacity;
        this.heartBeatMap = new TreeMap<>();
    }

    //If heartbeat is not present it means it has been deleted, else increment
    public void addHeartBeat(long time) {
        if(heartBeatMap.containsKey(time)) {
            heartBeatMap.get(time).incrementAndGet();
        }
    }

    public void cleanAndAddHeartBeat(long time) {
        if(!heartBeatMap.containsKey(time)){
            if(heartBeatMap.size() == capacity) heartBeatMap.pollFirstEntry();
            heartBeatMap.put(time, new AtomicInteger(0));
        }
    }

    public int getHeartBeatCount(long timeStamp) {
        int sum = 0;
        NavigableMap<Long, AtomicInteger> navMap = heartBeatMap.headMap(timeStamp - 1000, true);
        for(long time : navMap.keySet()) {
            sum += navMap.get(time).get();
            //System.out.println(sum);
        }
        if(navMap.isEmpty()) return 0;
        return sum/navMap.size();
    }


}
