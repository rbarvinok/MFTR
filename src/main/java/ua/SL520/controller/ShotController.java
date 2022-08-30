package ua.SL520.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ua.SL520.javaclass.GetSettings;
import ua.SL520.javaclass.domain.ResultShot;
import ua.SL520.javaclass.domain.SourceShot;
import ua.SL520.javaclass.servisClass.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ua.SL520.controller.Controller.localZone;
import static ua.SL520.javaclass.servisClass.FileChooserRun.selectedOpenFile;
import static ua.SL520.javaclass.servisClass.NormalisedShot.resultBulkShot;

public class ShotController implements Initializable {
    AlertAndInform inform = new AlertAndInform();
    OpenStage os = new OpenStage();
    FileChooserRun fileChooserRun = new FileChooserRun();
    GetSettings getSettings = new GetSettings();
    private SaveToExcelShot ste = new SaveToExcelShot();
    public static String openFileShot = " ";
    public static String openDirectoryShot;
    public String lineCount;
    public static String headFileShot = "Час,Локальний час,Швидкість (м/с),Похила дальність (м)";
    public int colsInpDate = 0;
    public static List<ResultShot> resultStreamShot = new ArrayList<>();
    public static List<SourceShot> soursShot = new ArrayList<>();
    public ObservableList<InputDate> inputDatesList = FXCollections.observableArrayList();
    public ObservableList<XYChart.Data> vel;
    public ObservableList<XYChart.Data> dist;

    @FXML
    public TableView outputTable;
    @FXML
    public ImageView imgView;
    @FXML
    public TextField statusBar, labelLineCount;
    @FXML
    public Label statusLabel, labelGMT;
    public Button tSaveExcel, tChart;
    @FXML
    public LineChart chartVelocity,chartDistance;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getSettings.getSettings();
    }

    @SneakyThrows
    public void onClickOpenFile(ActionEvent actionEvent) {
        if (statusBar.getText().equals("")) {
            //fileChooserRun.openFileChooser();
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Відкриття файлу");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter(".txt", "*.txt"),
                    new javafx.stage.FileChooser.ExtensionFilter("*.*", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            try {
                if (selectedFile != null) {
                    selectedOpenFile = selectedFile;
                } else {
                    inform.hd = "Помилка! ";
                    inform.ct = "Невдалось відкрити файл\n";
                    inform.alert();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            openFileShot = selectedOpenFile.getName().substring(0, selectedOpenFile.getName().length() - 4);
            openDirectoryShot = selectedOpenFile.getParent();
            openData();
        } else {
            inform.hd = "Файл уже відкритий";
            inform.ct = " Повторне відкриття файлу призведе до втрати не збережених даних \n";
            inform.inform();
        }
    }

    public void openData() throws Exception {
        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            line = line.replaceAll("'", ",");

            String[] split = line.split(",");

            if (split.length <= 1 || lineNumber < 68) {
                lineNumber++;
                continue;
            }
            lineNumber++;
            SourceShot sourceShot = new SourceShot(
                    Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]),
                    Double.parseDouble(split[3])
            );
            soursShot.add(sourceShot);
        }

        resultStreamShot = resultBulkShot(soursShot);

        fileReader.close();

        inputDates(resultStreamShot);

        TableColumn<InputDate, String> time = new TableColumn<>("Час");
        TableColumn<InputDate, String> timeUTC = new TableColumn<>("Локальний час");
        TableColumn<InputDate, String> velocity = new TableColumn<>("Швидкість");
        TableColumn<InputDate, String> distance = new TableColumn<>("Похила дальність");


        for (int i = 0; i <= colsInpDate - 4; i++) {
            final int indexColumn = i;
            time.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
            timeUTC.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
            velocity.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
            distance.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
        }
        outputTable.getColumns().addAll(time, timeUTC, velocity, distance);
        outputTable.setItems(inputDatesList);
        //--------------------------------------------------------

        lineCount = String.valueOf(lineNumber - 68);
        labelLineCount.setText("Cтрок:  " + lineCount);

        statusLabel.setText("Результати вимірювань " + openFileShot);
        statusBar.setText(" Файл \n" + openFileShot);
        labelGMT.setText("Локальний час - " + localZone);

        tSaveExcel.setDisable(false);
        charts();
    }

    @SneakyThrows
    public void onClickSaveExcel() {
        ste.toExcel();
        inform.title = "Збереження файлу";
        inform.hd = null;
        inform.ct = "Успішно записано в файл '" + openFileShot + ".xlsx'";
        inform.inform();
    }

    public void charts() {
        XYChart.Series seriesVel = new XYChart.Series();
        getDataVel();
        seriesVel.setData(vel);
        chartVelocity.getData().addAll(seriesVel);


        XYChart.Series seriesDist = new XYChart.Series();
        getDataDist();
        seriesDist.setData(dist);
        chartDistance.getData().addAll(seriesDist);
    }

    public void getDataVel() {
        List<ResultShot.Tvel> velList = resultStreamShot.stream().map(listVel -> {
            return new ResultShot.Tvel(listVel.getTime(), listVel.getVelocity());
        }).collect(Collectors.toList());

        vel = FXCollections.observableArrayList();
        for (ResultShot.Tvel vels : velList) {
            vel.add(new XYChart.Data(vels.getTime(), vels.getVelocity()));
        }
    }

    public void getDataDist() {
        List<ResultShot.Tdist> distList = resultStreamShot.stream().map(listDist -> {
            return new ResultShot.Tdist(listDist.getTime(), listDist.getDistance());
        }).collect(Collectors.toList());

        dist = FXCollections.observableArrayList();
        for (ResultShot.Tdist dists : distList) {
            dist.add(new XYChart.Data(dists.getTime(), dists.getDistance()));
        }
    }

    public void onClickSettings() throws IOException {
        os.viewURL = "/view/settings.fxml";
        os.title = "Налаштування";
        os.maximized = false;
        os.isResizable = false;
        os.isModality = true;
        os.openStage();
    }

    public void onClickNew() {
        outputTable.getColumns().clear();
        outputTable.getItems().clear();
        chartVelocity.getData().clear();
        chartDistance.getData().clear();
        statusBar.setText("");
        statusLabel.setText("Відкрийте файл *.txt");
        labelLineCount.setText(" ");
        soursShot.clear();
        resultStreamShot.clear();
        tSaveExcel.setDisable(true);
    }

    public void onClickSettings(ActionEvent actionEvent) {
    }

    public void inputDates(List source) {
        outputTable.getColumns().clear();
        outputTable.getItems().clear();
        outputTable.setEditable(false);
        int rowsInpDate = 0;

        String line;
        String csvSplitBy = ",";
        for (int j = 0; j < source.size(); j++) {
            line = source.get(j).toString();
            line = line.replace("[", "").replace("]", "");
            rowsInpDate += 1;
            String[] fields = line.split(csvSplitBy, -1);
            colsInpDate = fields.length;
            InputDate inpd = new InputDate(fields);
            inputDatesList.add(inpd);
        }
    }
}
