package ua.SL520.javaclass.servisClass;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ua.SL520.javaclass.domain.ResultShot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static ua.SL520.controller.Controller.localZone;
import static ua.SL520.controller.ShotController.*;

public class SaveToExcelShot {

    public void toExcel() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти як...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.xlsx", "*.xlsx"),
                new FileChooser.ExtensionFilter("*.*", "*.*"));
        fileChooser.setInitialFileName(openFileShot + "_res");
        File userDirectory = new File(openDirectoryShot);
        fileChooser.setInitialDirectory(userDirectory);
        File file = fileChooser.showSaveDialog((new Stage()));

        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet(openFileShot);

        int rownum = 0;
        Cell cell;
        Row row = sheet.createRow(rownum);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Дані вимірювань");
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue(openFileShot);
        rownum++;
        row = sheet.createRow(rownum);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Часовий пояс: ");
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue(localZone);
        rownum++;
        rownum++;
        row = sheet.createRow(rownum);

        int columnCount = StringUtils.countMatches(headFileShot, ",");
        for (int colnum = 0; colnum <= columnCount; colnum++) {
            cell = row.createCell(colnum, CellType.STRING);
            cell.setCellValue(headFileShot.split(",")[colnum]);
            sheet.autoSizeColumn(colnum);
        }
        rownum++;

        for (
                ResultShot result : resultStreamShot) {
            row = sheet.createRow(rownum);
            for (int colnum = 0; colnum <= columnCount; colnum++) {
                cell = row.createCell(colnum, CellType.STRING);
                cell.setCellValue(result.toString().split(",")[colnum]);
                //sheet.autoSizeColumn(colnum);
            }
            rownum++;
        }
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream outFile = new FileOutputStream(file);
        book.write(outFile);
        outFile.close();
    }
}
