package ua.MFTR.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ua.MFTR.javaclass.GetSettings;
import ua.MFTR.javaclass.domain.Result;
import ua.MFTR.javaclass.domain.Source;
import ua.MFTR.javaclass.servisClass.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ua.MFTR.javaclass.Normalised.resultBulk;
import static ua.MFTR.javaclass.servisClass.FileChooserRun.selectedOpenFile;

@Slf4j
public class Controller implements Initializable {
    AlertAndInform inform = new AlertAndInform();
    OpenStage os = new OpenStage();
    FileChooserRun fileChooserRun = new FileChooserRun();
    GetSettings getSettings = new GetSettings();
    private SaveToWord stw = new SaveToWord();
    private SaveToExcel ste = new SaveToExcel();
    public static String localZone;
    public static String openFile = " ";
    public static String openDirectory;
    public String lineCount;
    public static String headFile = "Назва файлу,Номер,Дата,Час,Початкова швидкість (м/с),Похибка (м/с),Точки,FitOrder,Примітка";
    public int colsInpDate = 0;
    public static List<Result> resultStream = new ArrayList<>();
    public static List<Source> sours = new ArrayList<>();
    public ObservableList<InputDate> inputDatesList = FXCollections.observableArrayList();

    @FXML
    public TableView outputTable;
    @FXML
    public ImageView imgView;
    @FXML
    public TextField statusBar, labelLineCount;
    @FXML
    public Label statusLabel, labelGMT;
    public Button tSaveWord, tCalc, tSaveExcel, tChart;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getSettings.getSettings();
    }

    public void openData() throws Exception {

        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            line = line.replaceAll("'", ",");

            String[] split = line.split(",");

            if (split.length <= 3 || lineNumber < 42) {
                lineNumber++;
                continue;
            }
            lineNumber++;
            Source source = new Source(
                    split[1],
                    split[3],
                    split[5],
                    split[7],
                    split[9],
                    split[11],
                    split[19],
                    split[21],
                    split[27]
            );
            sours.add(source);
        }

        resultStream = resultBulk(sours);

        fileReader.close();

        inputDates(resultStream);

        TableColumn<InputDate, String> filename = new TableColumn<>("Назва файлу");
        TableColumn<InputDate, String> measNum = new TableColumn<>("Номер");
        TableColumn<InputDate, String> measDate = new TableColumn<>("Дата");
        TableColumn<InputDate, String> measTime = new TableColumn<>("Час");
        TableColumn<InputDate, String> velocity = new TableColumn<>("Початкова швидкість");
        TableColumn<InputDate, String> accuracy = new TableColumn<>("Похибка");
        TableColumn<InputDate, String> pointsUsed = new TableColumn<>("Точки");
        TableColumn<InputDate, String> fitOrder = new TableColumn<>("FitOrder");
        TableColumn<InputDate, String> comment = new TableColumn<>("Примітка");

        for (int i = 0; i <= colsInpDate - 9; i++) {
            final int indexColumn = i;
            filename.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
            measNum.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
            measDate.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
            measTime.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
            velocity.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
            accuracy.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
            pointsUsed.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(6 + indexColumn)));
            fitOrder.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(7 + indexColumn)));
            comment.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(8 + indexColumn)));
        }
        outputTable.getColumns().addAll(filename, measNum, measDate, measTime, velocity, accuracy, pointsUsed, fitOrder, comment);
        outputTable.setItems(inputDatesList);
        //--------------------------------------------------------

        lineCount = String.valueOf(lineNumber - 44);
        labelLineCount.setText("Cтрок:  " + lineCount);

        statusLabel.setText("Результати вимірювань " + openFile);
        statusBar.setText(" Файл \n" + openFile);

        labelGMT.setText("Локальний час - " + localZone);

        tSaveExcel.setDisable(false);
        tCalc.setDisable(false);
        tSaveWord.setDisable(false);
        tChart.setDisable(false);
    }

    public void onClickOpenFile() throws Exception {
        if (statusBar.getText().equals("")) {
            fileChooserRun.openFileChooser();
            openFile = selectedOpenFile.getName().substring(0, selectedOpenFile.getName().length() - 4);
            openDirectory = selectedOpenFile.getParent();

            openData();
            return;
        } else {
            inform.hd = "Файл уже відкритий";
            inform.ct = " Повторне відкриття файлу призведе до втрати не збережених даних \n";
            inform.inform();
            return;
        }
    }

    @SneakyThrows
    public void onClickCalculate() {
        getSettings.getSettings();
        resultStream = resultBulk(sours);

        List<String> resultsStrings = resultStream.stream().map(Result::toString).collect(Collectors.toList());
        //output to Table----------------------------------------
        inputDates(resultsStrings);
        TableColumn<InputDate, String> filename = new TableColumn<>("Назва файлу");
        TableColumn<InputDate, String> measNum = new TableColumn<>("Номер");
        TableColumn<InputDate, String> measDate = new TableColumn<>("Дата");
        TableColumn<InputDate, String> measTime = new TableColumn<>("Час");
        TableColumn<InputDate, String> velocity = new TableColumn<>("Початкова швидкість");
        TableColumn<InputDate, String> accuracy = new TableColumn<>("Похибка");
        TableColumn<InputDate, String> pointsUsed = new TableColumn<>("Точки");
        TableColumn<InputDate, String> fitOrder = new TableColumn<>("FitOrder");
        TableColumn<InputDate, String> comment = new TableColumn<>("Примітка");

        for (int i = 0; i <= colsInpDate - 9; i++) {
            final int indexColumn = i;
            filename.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
            measNum.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
            measDate.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
            measTime.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
            velocity.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
            accuracy.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
            pointsUsed.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(6 + indexColumn)));
            fitOrder.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(7 + indexColumn)));
            comment.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(8 + indexColumn)));
        }
        outputTable.getColumns().addAll(filename, measNum, measDate, measTime, velocity, accuracy, pointsUsed, fitOrder, comment);
        outputTable.setItems(inputDatesList);

        statusLabel.setText("Результати вимірювань " + openFile);
        labelGMT.setText("Локальний час - " + localZone);
    }

    @SneakyThrows
    public void onClickSaveWord() {
        stw.toWord();
        inform.title = "Збереження файлу";
        inform.hd = null;
        inform.ct = "Успішно записано в файл '" + openFile + ".docx'";
        inform.inform();
    }

    @SneakyThrows
    public void onClickSaveExcel() {
        ste.toExcel();
        inform.title = "Збереження файлу";
        inform.hd = null;
        inform.ct = "Успішно записано в файл '" + openFile + ".xlsx'";
        inform.inform();
    }

    @SneakyThrows
    public void onClickChart() {
        os.viewURL = "/view/chart.fxml";
        os.title = "Графік GPS   " + openFile;
        os.openStage();
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

    @SneakyThrows
    public void onClickShot(ActionEvent actionEvent) {
        os.viewURL = "/view/shotSample.fxml";
        os.title = "Sl-520PE Reporter (Shot)";
        os.maximized = true;
        os.isResizable = true;
        os.isModality = false;
        os.openStage();
    }

    @SneakyThrows
    public void onClickManual() {
        if (Desktop.isDesktopSupported()) {
            File url = new File("userManual/UserManual.pdf");
            Desktop desktop = Desktop.getDesktop();
            //System.out.println(url.getPath());
            desktop.open(url);
        }
    }

    @SneakyThrows
    public void onClickImageSetup() {
        if (Desktop.isDesktopSupported()) {
            File url = new File("userManual/ImageSetup.pdf");
            Desktop desktop = Desktop.getDesktop();
            //System.out.println(url.getPath());
            desktop.open(url);
        }
    }

    public void onClick_menuAbout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/about.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void onClickNew(ActionEvent e) {
        outputTable.getColumns().clear();
        outputTable.getItems().clear();
        statusBar.setText("");
        statusLabel.setText("Відкрийте файл *.ses");
        labelLineCount.setText(" ");
        sours.clear();
        resultStream.clear();
        tSaveExcel.setDisable(true);
        tCalc.setDisable(true);
        tSaveWord.setDisable(true);
        tChart.setDisable(true);
    }

    @SneakyThrows
    public void onClickSettings()  {
        os.viewURL = "/view/settings.fxml";
        os.title = "Налаштування";
        os.maximized = false;
        os.isResizable = false;
        os.isModality = true;
        os.openStage();
    }

    public void onClickCancelBtn(ActionEvent event) {
        inform.title = "Вихід з програми";
        inform.hd = "Закрити програму?";
        inform.ct = "Всі незбережені дані буде втрачено";
        inform.confirmation(event);
    }


}


