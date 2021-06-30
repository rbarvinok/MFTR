package ua.SL520.javaclass.servisClass;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ua.SL520.javaclass.domain.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static ua.SL520.controller.Controller.*;

public class SaveToExcel {

    public void toExcel() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти як...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.xlsx", "*.xlsx"),
                new FileChooser.ExtensionFilter("*.*", "*.*"));
        fileChooser.setInitialFileName(openFile + "_res");
        File userDirectory = new File(openDirectory);
        fileChooser.setInitialDirectory(userDirectory);

        File file = fileChooser.showSaveDialog((new Stage()));

        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet(openFile);

    DataFormat format = book.createDataFormat();
    CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));

    int rownum = 0;
    Cell cell;
    Row row = sheet.createRow(rownum);
    cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Дані вимірювань");
    cell = row.createCell(1, CellType.STRING);
        cell.setCellValue(openFile);
    rownum++;
    row = sheet.createRow(rownum);
    cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Локальний час");
    cell = row.createCell(1, CellType.STRING);
        cell.setCellValue(localZone);
    rownum++;
    rownum++;
    row = sheet.createRow(rownum);

    int columnCount = StringUtils.countMatches(headFile, ",");
        for (int colnum = 0; colnum <= columnCount; colnum++) {
        cell = row.createCell(colnum, CellType.STRING);
        cell.setCellValue(headFile.split(",")[colnum]);
        sheet.autoSizeColumn(colnum);
    }
    rownum++;

        for (
    Result result : resultStream) {
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
