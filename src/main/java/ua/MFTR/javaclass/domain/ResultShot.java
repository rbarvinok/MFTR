package ua.MFTR.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
public class ResultShot {

    private double time;
    private String timeUTC;
    private double velocity;
    private double distance;
    @AllArgsConstructor
    @Data
    public static class Tvel {
        private double time;
        private double velocity;
    }
    @AllArgsConstructor
    @Data
    public static class Tdist {
        private double time;
        private double distance;
    }

    @Override
    public String toString() {
        return time + "," + timeUTC + "," + velocity  + "," + distance;// +"\n";

    }
}
