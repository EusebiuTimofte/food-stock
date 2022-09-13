package com.mycompany.app.nondbentities;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class Xlsx {

    XSSFWorkbook workbook;
    Sheet sheet;

    Xlsx() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Sheet1");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
    }

    public void writeExcel(String directoryPath, String xlsxFilename){
        String fileLocation = directoryPath + "\\" + xlsxFilename;
        try {
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            System.out.println("Fisier creat cu succes la locatia " + fileLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void close(){
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    abstract public void populateExcel();

}
