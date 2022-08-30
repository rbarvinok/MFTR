package ua.SL520.javaclass.servisClass;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ua.SL520.javaclass.domain.ResultShot;
import ua.SL520.javaclass.domain.SourceShot;

import java.util.List;
import java.util.stream.Collectors;

import static ua.SL520.controller.Controller.localZone;

@UtilityClass
public class NormalisedShot {
    String localTime = "";

    @SneakyThrows
    public static ResultShot res(SourceShot sourceShot) {
        SecToHMS(Double.valueOf(sourceShot.getTimeUTC()));
        ResultShot resultShot = new ResultShot();
        resultShot.setTime(sourceShot.getTime());
        resultShot.setTimeUTC(localTime);
        resultShot.setVelocity(sourceShot.getVelocity());
        resultShot.setDistance(sourceShot.getDistance());
        return resultShot;
    }

    public void SecToHMS(Double timeSeconds) {
        int h = (int) (timeSeconds / 3600);
        int m=(int) ((timeSeconds-h*3600)/60);
        double ss=Math.rint((timeSeconds - h*3600 - m*60)*1000000)/1000000;
        localTime = h + Integer.parseInt(localZone.substring(5, 6)) + ":" + m + ":" + ss;
    }

    public static List<ResultShot> resultBulkShot(List<SourceShot> soursShot) {
        return soursShot.stream().map(NormalisedShot::res).collect(Collectors.toList());
    }
}
