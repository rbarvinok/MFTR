package ua.SL520.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import ua.SL520.javaclass.domain.Source;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ua.SL520.controller.Controller.openFile;
import static ua.SL520.controller.Controller.sours;

public class chartController implements Initializable {
    public static ObservableList<XYChart.Data> vel;

    @FXML
    public ScatterChart sChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        ScatterChart<Number, Number> scc = new ScatterChart<>(x, y);
        scc.setTitle("Графік початкової швидкості " + openFile);
        x.setLabel("Постріл");
        y.setLabel("Швидкість, м/с");

        XYChart.Series series = new XYChart.Series();
        series.setName("Початкова швидкість");
        getData();

        series.setData(vel);
        sChart.getData().addAll(series);
        getCurrentData();
    }

    public static void getData() {
        List<Source.Tsource> tsources = sours.stream().map(velocityes -> {
            return new Source.Tsource(Integer.parseInt(velocityes.getMeasNum()), Double.parseDouble(velocityes.getVelocity()));
        }).collect(Collectors.toList());

        vel = FXCollections.observableArrayList();
        for (Source.Tsource tvelocity : tsources) {
            vel.add(new XYChart.Data(tvelocity.getMeasNum(), tvelocity.getVelocity()));
        }
    }

    public void getCurrentData() {
        ObservableList<XYChart.Data> dataList = ((XYChart.Series) sChart.getData().get(0)).getData();
        for (XYChart.Data data : dataList) {
            Node node = data.getNode();
            Tooltip tooltip = new Tooltip("Номер пострілу: " + data.getXValue().toString() + '\n' + "Початкова швидкість: " + data.getYValue().toString());
            Tooltip.install(node, tooltip);

            node.setOnMouseEntered(event -> node.getStyleClass().add("onHover"));
            node.setOnMouseExited(event -> node.getStyleClass().remove("onHover"));
        }
    }


}