package my.cache.model;

import lombok.Data;
import my.cache.logic.HeartBeatTracker;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class RaftOpts {
    int listenAddress;
    int leaderAddress;
    Boolean isLeader;
    Map<String, Socket> followers;
    HeartBeatTracker heartBeatTracker;
    Boolean disappear;

    public RaftOpts(int listenAddress, int leaderAddress, Boolean isLeader, HeartBeatTracker heartBeatTracker) {
        this.listenAddress = listenAddress;
        this.leaderAddress = leaderAddress;
        this.isLeader = isLeader;
        this.followers = new HashMap<>();
        this.heartBeatTracker = heartBeatTracker;
        this.disappear = false;
    }



}
