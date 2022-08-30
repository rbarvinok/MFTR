package ua.SL520.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SourceShot {

    private double time;
    private double timeUTC;
    private double velocity;
    private double distance;

    @AllArgsConstructor
    @Data
    public static class Tsource {
        private double time;
        private double velocity;
    }

    @Override
    public String toString() {
        return time + "," + timeUTC + "," + velocity  + "," + distance +"\n";

    }
}
