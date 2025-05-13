package my.cache.model;

import lombok.Data;

@Data
public class StatData {
    int setVal;
    int getVal;
    int removeVal;

    public StatData(int setVal, int getVal, int removeVal) {
        this.setVal = setVal;
        this.getVal = getVal;
        this.removeVal = removeVal;
    }
    public String toString() {
        return "setAvg: " + setVal + ", getAvg: " + getVal + ", removeAvg: " + removeVal;
    }
}
