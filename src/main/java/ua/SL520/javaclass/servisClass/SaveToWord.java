package ua.SL520.javaclass.servisClass;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import ua.SL520.javaclass.domain.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static ua.SL520.controller.Controller.*;

public class SaveToWord {
    public String headerContent, footerContent, fileContent;


    public void toWord() throws IOException {


        headerContent = "ДЕРЖАВНИЙ НАУКОВО-ДОСЛІДНИЙ ІНСТИТУТ ВИПРОБУВАНЬ І СЕРТИФІКАЦІЇ ОЗБРОЄННЯ ТА ВІЙСЬКОВОЇ ТЕХНІКИ ";
        footerContent = " 14033 м. Чернігів ";

        fileContent = "Дані вимірювань";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти як...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.docx", "*.docx"),
                new FileChooser.ExtensionFilter("*.*", "*.*"));
        fileChooser.setInitialFileName(openFile + "_res");
        File userDirectory = new File(openDirectory);
        fileChooser.setInitialDirectory(userDirectory);

        File file = fileChooser.showSaveDialog((new Stage()));

        // создаем модель docx документа, к которой будем прикручивать наполнение (колонтитулы, текст)
        XWPFDocument docxModel = new XWPFDocument();
        CTSectPr ctSectPr = docxModel.getDocument().getBody().addNewSectPr();

        // получаем экземпляр XWPFHeaderFooterPolicy для работы с колонтитулами
        XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(docxModel, ctSectPr);

        // создаем верхний колонтитул Word файла
        CTP ctpHeaderModel = createHeaderModel("");

        // устанавливаем сформированный верхний колонтитул в модель документа Word
        XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeaderModel, docxModel);
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);
        headerParagraph.setBorderBottom(Borders.BASIC_BLACK_SQUARES);

        XWPFRun hederparagraphConfig = headerParagraph.createRun();
        hederparagraphConfig.setText(headerContent);
        hederparagraphConfig.setFontSize(12);
        hederparagraphConfig.setBold(false);
        hederparagraphConfig.setFontFamily("Time New Roman");
        hederparagraphConfig.setColor("0000ff");

        headerFooterPolicy.createHeader(
                XWPFHeaderFooterPolicy.DEFAULT,
                new XWPFParagraph[]{headerParagraph}
        );

        // создаем нижний колонтитул docx файла
        CTP ctpFooterModel = createFooterModel("");

        // устанавливаем сформированый нижний колонтитул в модель документа Word
        XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooterModel, docxModel);
        footerParagraph.setAlignment(ParagraphAlignment.CENTER);
        footerParagraph.setBorderTop(Borders.BASIC_BLACK_SQUARES);

        XWPFRun fotterparagraphConfig = footerParagraph.createRun();
        fotterparagraphConfig.addBreak();
        fotterparagraphConfig.setText(footerContent);
        fotterparagraphConfig.setFontFamily("Time New Roman");
        fotterparagraphConfig.setColor("0000ff");

        headerFooterPolicy.createFooter(
                XWPFHeaderFooterPolicy.DEFAULT,
                new XWPFParagraph[]{footerParagraph}
        );

        // создаем обычный параграф
        XWPFParagraph bodyParagraph = docxModel.createParagraph();
        bodyParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun document = bodyParagraph.createRun();
        document.setFontSize(14);
        document.setFontFamily("Time New Roman");
        document.setBold(true);
        document.setTextPosition(25);
        document.setText(fileContent);
        document.addBreak();
        document.setText(openFile);
        //document.addBreak();

        XWPFParagraph bodyParagraph1 = docxModel.createParagraph();
        XWPFRun document1 = bodyParagraph1.createRun();
        bodyParagraph1.setAlignment(ParagraphAlignment.LEFT);
        document1.setFontSize(12);
        document1.setFontFamily("Time New Roman");
        document1.addBreak();

//-------------------  table  -------------
        XWPFTable table = docxModel.createTable();
        int cellCount = StringUtils.countMatches(headFile, ",");
        int rownum = 0;
        String splitBy = ",";
        String line;
//------------------   1  ----------------------------
        XWPFTableRow tableRow = table.getRow(0);

        line = Arrays.toString(headFile.split(splitBy));
        line = line.replace("[", "").replace("]", "");


        //---
        for (int colnum = 0; colnum <= cellCount; colnum++) {
            tableRow.addNewTableCell();
            tableRow.getCell(colnum).setText(line);
        }
        //---
        for (Result result : resultStream) {
            tableRow = table.createRow();

            for (int colnum = 0; colnum <= cellCount; colnum++) {
                line = resultStream.get(colnum).toString();
                line = line.replace("[", "").replace("]", "");
                String[] fields = line.split(splitBy, -1);
                //tableRow.getCell(colnum).setText(Arrays.toString(result.toString().split(splitBy)));
                tableRow.getCell(colnum).setText(fields.toString());
            }
        }

        //-------------------------------    2    -------------------------------------------------
//        List<String[]> csvData = new ArrayList<String[]>();
//        csvData.addAll(Collections.singleton(resultStream.toString().split(",")));
//        System.out.println(csvData);
//        int cols = cellCount;
//        int rows = (int)Math.ceil(resultStream.size());
//        table = docxModel.createTable(rows, cols);
//        XWPFTableRow row;
//        XWPFTableCell cell;
//        int i = 0;
//
//        for (int r = 0; r < rows; r++) {
//
//            row = table.getRow(r);
//            for (int c = 0; c <= cols; c++) {
//                cell = row.getCell(c);
//                if (i < resultStream.size()) cell.setText(csvData.get(i)[1]);
//                i++;
//            }
//        }

//---------------------------------     0    ----------------------------------------------------
//        XWPFTable table = docxModel.createTable();
//        int cellCount = StringUtils.countMatches(headFile, ",");
//        int rownum = 0;
//
//        XWPFTableRow tableRow = table.getRow(0);
//        String line = Arrays.toString(headFile.split(","));
//        System.out.println(line);
//
//        for (int colnum = 0; colnum <= cellCount; colnum++) {
//            tableRow.getCell(0);
//            tableRow.addNewTableCell().setText(line);
//        }
////        headFile = "Назва файлу, Номер, Дата, Час, Швидкість, Похибка, Точки, FitOrder, Примітка";
//
//        for (Result result : resultStream) {
//            tableRow = table.createRow();
//
//            for (int colnum = 0; colnum <= cellCount; colnum++) {
//                tableRow.addNewTableCell().setText(Arrays.toString(result.toString().split(",")));
//            }
//        }
//-------------------------------------------------------------------------------------
        // сохраняем модель docx документа в файл
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file.getAbsolutePath());

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            docxModel.write(outputStream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private static CTP createHeaderModel(String headerContent) {
        // создаем хедер или верхний колонтитул
        CTP ctpHeaderModel = CTP.Factory.newInstance();
        CTR ctrHeaderModel = ctpHeaderModel.addNewR();
        CTText cttHeader = ctrHeaderModel.addNewT();

        cttHeader.setStringValue(headerContent);
        return ctpHeaderModel;
    }

    private static CTP createFooterModel(String footerContent) {
        // создаем футер или нижний колонтитул
        CTP ctpFooterModel = CTP.Factory.newInstance();
        CTR ctrFooterModel = ctpFooterModel.addNewR();
        CTText cttFooter = ctrFooterModel.addNewT();

        cttFooter.setStringValue(footerContent);
        return ctpFooterModel;
    }


}

