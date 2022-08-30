package ua.SL520;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ua.SL520.javaclass.servisClass.AlertAndInform;

public class Main extends Application {
    AlertAndInform inform = new AlertAndInform();
    public static String icoImage = "/images/img.png";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/sample.fxml"));
        primaryStage.getIcons().add(new Image(getClass().getResource(icoImage).toExternalForm()));
        primaryStage.setTitle("SL-520PE Reporter");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event ->
        {
            inform.title = "Вихід з програми";
            inform.hd = "Закрити програму?";
            inform.ct = "Всі незбережені дані буде втрачено";
            inform.confirmation(event);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
