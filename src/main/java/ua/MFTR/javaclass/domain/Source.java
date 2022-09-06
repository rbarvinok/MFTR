package ua.MFTR.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Source {

    private String filename;
    private String measNum;
    private String measDate;
    private String measTime;
    private String velocity;
    private String accuracy;
    private String pointsUsed;
    private String fitOrder;
    private String comment;



    @AllArgsConstructor
    @Data
    public static class Tsource {
        private int measNum;
        private double velocity;
    }



    @Override
    public String toString() {
        return filename + ",    " + measNum + ",    " + measDate + ",   " + measTime + ",    " + velocity  + ",    " + accuracy + ",    " +  pointsUsed + ",    " + fitOrder + ",    " + comment +"\n";

    }
}
