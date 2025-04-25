package my.cache.model;

public class ServerOpts {
    int listenAddress;
    Boolean isLeader;

    public ServerOpts(int listenAddress, Boolean isLeader) {
        this.listenAddress = listenAddress;
        this.isLeader = isLeader;
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
}
