package my.cache.model;

import lombok.Data;

import java.util.Comparator;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
public class RemoveByte implements Delayed {
    byte[] keyToRemove;
    long endTime;

    public RemoveByte(byte[] keyToRemove, int delay) {
        this.keyToRemove = keyToRemove;
        this.endTime = System.currentTimeMillis() + delay;
    }
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if(this.endTime - ((RemoveByte) o).endTime < 0) return -1;
        if(this.endTime - ((RemoveByte) o).endTime > 0) return 1;
        return 0;
    }
}
