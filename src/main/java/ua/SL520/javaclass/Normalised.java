package ua.SL520.javaclass;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ua.SL520.javaclass.domain.Result;
import ua.SL520.javaclass.domain.Source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static ua.SL520.controller.Controller.localZone;

@UtilityClass
public class Normalised {

    @SneakyThrows
    public static Result res(Source source) {
        String calcLocalZone = "GMT+0" + (Integer.parseInt(localZone.substring(5, 6)) + 2) + ":00";
        Result result = new Result();

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        Date docDate = sdfTime.parse(source.getMeasTime());
        sdfTime.setTimeZone(TimeZone.getTimeZone(calcLocalZone));
        result.setMeasTime(sdfTime.format(docDate));

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        Date data = sdf.parse(source.getMeasDate());
        sdf.applyPattern("dd.MM.yyyy");
        result.setMeasDate(sdf.format(data));

        result.setFilename(source.getFilename());
        result.setMeasNum(source.getMeasNum());
        result.setVelocity(source.getVelocity());
        result.setAccuracy(String.valueOf(Math.rint(Double.parseDouble(source.getAccuracy()) * 100) / 100));
        result.setPointsUsed(source.getPointsUsed());
        result.setFitOrder(source.getFitOrder());
        result.setComment(source.getComment());

        return result;
    }

    public static List<Result> resultBulk(List<Source> sours) {
        return sours.stream().map(Normalised::res).collect(Collectors.toList());
    }
}
