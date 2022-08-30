package ua.SL520.javaclass.domain;

import lombok.Data;

@Data
public class Result {

    private String filename;
    private String measNum;
    private String measDate;
    private String measTime;
    private String velocity;
    private String accuracy;
    private String pointsUsed;
    private String fitOrder;
    private String comment;

    @Override
    public String toString() {
        return filename + "," + measNum + "," + measDate + "," + measTime + "," + velocity  + "," + accuracy + "," +  pointsUsed + "," + fitOrder + "," + comment +"\n";

    }
}
