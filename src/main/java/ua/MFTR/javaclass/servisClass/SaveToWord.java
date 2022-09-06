package ua.MFTR.javaclass.servisClass;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import ua.MFTR.javaclass.domain.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static ua.MFTR.controller.Controller.*;

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

//-------------------  table  -------------
        int cellCount = StringUtils.countMatches(headFile, ",");
        String splitBy = ",";

        XWPFTable table = docxModel.createTable();
        table.setTableAlignment(TableRowAlign.CENTER);
        //table.setCellMargins(5, 5, 5, 5);
        XWPFTableRow tableRow = table.getRow(0);

//        XWPFRun run = tableRow.addNewTableCell().addParagraph().createRun();
//        run.setFontSize(14);
//        run.setFontFamily("Time New Roman");
//        run.setBold(true);
        
        for (int colnum = 0; colnum <= cellCount; colnum++) {
            tableRow.addNewTableCell();
            String[] fields = headFile.split(splitBy);
            List<String> strings = Arrays.asList(fields);
            tableRow.getCell(colnum).setText(strings.get(colnum));
            tableRow.setRepeatHeader(true);
        }

        for (Result result : resultStream) {
            tableRow = table.createRow();
            for (int colnum = 0; colnum <= cellCount; colnum++) {
                String line = result.toString();
                line = line.replace("[", "").replace("]", "");
                String[] fields = line.split(splitBy);
                List<String> strings = Arrays.asList(fields);
                if (colnum==2)
                tableRow.getCell(colnum).setText(strings.get(colnum));
                else tableRow.getCell(colnum).setText(strings.get(colnum).replace(".",","));
            }
        }

        // сохраняем модель docx документа в файл
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            docxModel.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
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

