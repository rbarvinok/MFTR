package ua.SL520.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

import static ua.SL520.controller.Controller.openFile;

public class chartEulerController implements Initializable {

    @FXML
    public LineChart lineChart, lineChartAlt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        LineChart<Number, Number> lcc = new LineChart<Number, Number>(x, y);
        lcc.setTitle("Кути Ейлера " + openFile);
        x.setLabel("Час,UTC");
        y.setLabel("Кут, град");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Курс");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Крен");

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Тангаж");

//  Углы Эйлера



}}