package ua.SL520.javaclass.servisClass;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static ua.SL520.controller.Controller.openDirectory;
import static ua.SL520.controller.Controller.openFile;

public class SaveToWord {
    public String headerContent, footerContent, fileContent;


    public void toWord() throws IOException {


        headerContent = "ДЕРЖАВНИЙ НАУКОВО-ВИПРОБУВАЛЬНИЙ ІНСТИТУТ ВИПРОБУВАНЬ І СЕРТИФІКАЦІЇ ОЗБРОЄННЯ ТА ВІЙСЬКОВОЇ ТЕХНІКИ ";
        footerContent = " 14033 м. Чернігів ";

        fileContent = "Дані вимірювань " + openFile;

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

        XWPFParagraph bodyParagraph1 = docxModel.createParagraph();
        XWPFRun document1 = bodyParagraph1.createRun();
        bodyParagraph1.setAlignment(ParagraphAlignment.LEFT);
        document1.setFontSize(12);
        document1.setFontFamily("Time New Roman");
        document1.setText("Координати вогневої позиції:  ");
        document1.addBreak();
//        document1.setText("X = " + calc.xVp + ",  Y = " + calc.yVp);
//        document1.addBreak();


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

