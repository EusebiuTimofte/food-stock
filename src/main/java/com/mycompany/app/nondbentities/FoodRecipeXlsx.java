package com.mycompany.app.nondbentities;

import com.mycompany.app.entities.FoodRecipe;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class FoodRecipeXlsx extends Xlsx{

    List<FoodRecipe> foodRecipeList;

    public FoodRecipeXlsx(List<FoodRecipe> foodRecipeList) {
        super();
        this.foodRecipeList = new ArrayList<>(foodRecipeList);
    }
    @Override
    public void populateExcel() {
        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Id reteta");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Nume reteta");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Id ingredient");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Nume ingredient");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Cantitate");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Unitate de masura");
        headerCell.setCellStyle(headerStyle);

        // populate excel workbook with data
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        for (int i=0;i<foodRecipeList.size(); i++){
            Row row = sheet.createRow(i+1);

            Cell cell = row.createCell(0);
            cell.setCellValue(foodRecipeList.get(i).getRecipe().getId());
            cell.setCellStyle(style);

            Cell cell2 = row.createCell(1);
            cell2.setCellValue(foodRecipeList.get(i).getRecipe().getRecipeName());
            cell2.setCellStyle(style);

            Cell cell3 = row.createCell(2);
            cell3.setCellValue(foodRecipeList.get(i).getFood().getId());
            cell3.setCellStyle(style);

            Cell cell4 = row.createCell(3);
            cell4.setCellValue(foodRecipeList.get(i).getFood().getProductName());
            cell4.setCellStyle(style);

            Cell cell5 = row.createCell(4);
            cell5.setCellValue(foodRecipeList.get(i).getQuantity());
            cell5.setCellStyle(style);

            Cell cell6 = row.createCell(5);
            cell6.setCellValue(foodRecipeList.get(i).getFood().getMeasurementUnit());
            cell6.setCellStyle(style);
        }

    }
}
