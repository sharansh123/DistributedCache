package my.cache.model;

import lombok.Data;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Data
public class ServerOpts {
    int listenAddress;
    Boolean isLeader;
    int leaderAddress;
    Map<String, Socket> followers;


    public ServerOpts(int listenAddress, Boolean isLeader, int leaderAddress) {
        this.listenAddress = listenAddress;
        this.isLeader = isLeader;
        this.leaderAddress = leaderAddress;
        this.followers = new HashMap<>();
    }
}
