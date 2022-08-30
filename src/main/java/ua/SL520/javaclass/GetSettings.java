package ua.SL520.javaclass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static ua.SL520.controller.Controller.localZone;

public class GetSettings {
    public void getSettings() throws IOException {

        FileReader fileReader1 = new FileReader("settings.txt");
        BufferedReader bufferedReader1 = new BufferedReader(fileReader1);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader1.readLine()) != null) {
            if (lineNumber == 0) {
                localZone = (line.split("=")[1]);
            }

            lineNumber++;
        }
        fileReader1.close();
    }

}
