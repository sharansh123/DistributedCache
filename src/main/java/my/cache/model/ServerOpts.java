package my.cache.model;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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

    public int getListenAddress() {
        return listenAddress;
    }
    public void setListenAddress(int listenAddress) {
        this.listenAddress = listenAddress;
    }
    public Boolean getIsLeader() {
        return isLeader;
    }
    public void setIsLeader(Boolean isLeader) {
        this.isLeader = isLeader;
    }
    public int getLeaderAddress() {
        return leaderAddress;
    }
    public void setLeaderAddress(int leaderAddress) {
        this.leaderAddress = leaderAddress;
    }
    public Map<String, Socket> getFollowers() {
        return followers;
    }
}
