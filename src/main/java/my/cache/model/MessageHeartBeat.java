package my.cache.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import my.cache.interfaces.MessageHandler;

@Data
@AllArgsConstructor
public class MessageHeartBeat implements MessageHandler {
    String offset;
    String from;
    Long timestamp;

    @Override
    public String getCommand() {
        return "HEARTBEAT";
    }



}
