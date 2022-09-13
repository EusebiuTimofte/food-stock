package com.mycompany.app.nondbentities;

import com.mycompany.app.entities.Food;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class FoodXlsx extends Xlsx{

    List<Food> foodList;

    public FoodXlsx(List<Food> foodList) {
        super();
        this.foodList = new ArrayList<>(foodList);
    }

    @Override
    public void populateExcel() {
        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Nume");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Unitate de masura");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Cantitate");
        headerCell.setCellStyle(headerStyle);

        // populate excel workbook with data
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        for (int i=0;i<foodList.size(); i++){
            Row row = sheet.createRow(i+1);

            Cell cell = row.createCell(0);
            cell.setCellValue(foodList.get(i).getProductName());
            cell.setCellStyle(style);

            Cell cell2 = row.createCell(1);
            cell2.setCellValue(foodList.get(i).getMeasurementUnit());
            cell2.setCellStyle(style);

            Cell cell3 = row.createCell(2);
            cell3.setCellValue(foodList.get(i).getStockQuantity());
            cell3.setCellStyle(style);
        }

    }
}
