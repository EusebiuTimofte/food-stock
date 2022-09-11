package com.mycompany.app.services;

import com.mycompany.app.entities.Food;
import com.mycompany.app.entities.FoodRecipe;
import com.mycompany.app.entities.Recipe;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class MainMenuService {

    Scanner sc;
    final StandardServiceRegistry registry;
    SessionFactory sessionFactory;
    Session session;

    public MainMenuService() {
        sc = new Scanner(System.in);
        registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();

            session = sessionFactory.openSession();
        }catch (Exception e) {
            e.printStackTrace();
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            System.exit(1);
        }
    }

    public void start() {
        do {
            showMenu();
            int option = readOptionChoosed();
            switch (option) {
                case 1 -> addIngredient();
                case 2 -> addIngredientsFromExcel();
                case 3 -> exportAllFoodIntoXlsx();
                case 4 -> addRecipe();
                case 10 -> {
                    System.out.println("Au revoir!");
                    System.exit(0);
                }
                default -> throw new AssertionError();
            }

        }while (true);
    }

    public void showMenu(){
        System.out.println("---------MENU----------\n");
        System.out.println("-- MENIU INGREDIENTE");
        System.out.println("1. Adauga un singur ingredient");
        System.out.println("2. Adauga ingrediente dintr-un excel");
        System.out.println("3. Exporta ingredientele existente intr-un excel");
        System.out.println("4. Adauga o reteta");
        System.out.println("10. Exit");
    }

    public int readOptionChoosed() {
        System.out.println("Introduceti optiunea dorita:");

        do {
            try {
                String optionTerminal = sc.nextLine();
                if (optionTerminal.equals("")) continue;
//                System.out.println("optionTerminal read: " + optionTerminal);
                int option= Integer.parseInt(optionTerminal);
                if (1 <= option && option <= 10 ) return option;
                else System.out.println("Introduceti un numar intreg intre 1 si 10");
            }catch (NumberFormatException e){
                System.out.println("Introduceti un numar intreg intre 1 si 10");
            }

        }while (true);

    }

    public int readYesNoOption(){
        int option = sc.nextInt();
        while (option != 0 && option != 1) {
            System.out.println("Introduceti 0 sau 1");
            option = sc.nextInt();
        }
        return option;
    }

    public Food readIngredient(){
        Food food = new Food();
        System.out.println("Introdu numele ingredientului:");
        food.setProductName(sc.nextLine().toLowerCase().trim());
        System.out.println("Introdu unitatea de masura:");
        food.setMeasurementUnit(sc.nextLine());
        System.out.println("Introdu cantitatea adaugata");
        food.setStockQuantity(sc.nextDouble());
        return food;
    }

    public void addIngredient() {
        Food food = readIngredient();
        // get ingredient from db
        // if it exists, add units to the quantity
        // if not, create ingredient
        Transaction transaction = session.beginTransaction();
        List<Food> foodList = session.createNativeQuery("select * from Food where product_name='" + food.getProductName()+ "'", Food.class).list();
        if (foodList.size() == 0){
            System.out.println("Nu a fost gasit niciun ingredient cu numele " + food.getProductName());
            System.out.println("Doriti adaugarea ingredientului sau renuntati la operatiune? (0/1)");
            int option = readYesNoOption();
            if (option == 0){
                //add ingredient

                session.persist(food);

                System.out.println("Ingredientul a fost adaugat");
                System.out.println(food);
            }else{
                System.out.println("Operatiunea a fost abandonata");
            }
        }else {
            Food foodFromDb = foodList.get(0);
            foodFromDb.setStockQuantity(foodFromDb.getStockQuantity() + food.getStockQuantity());
            System.out.println("Cantitatea ingredientului a fost actualizata");
            System.out.println(foodFromDb);
        }
        transaction.commit();
    }

    public void addIngredientsFromExcel() {

        System.out.println("Introduceti calea catre fisierul xslx");
        String excelPath = sc.nextLine();
        //read excel
        // add ingredient

        try (FileInputStream file = new FileInputStream(excelPath)) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String name = row.getCell(0).getStringCellValue().trim().toLowerCase();
                String measurementUnit = row.getCell(1).getStringCellValue();
                double quantity = row.getCell(2).getNumericCellValue();

                // see if ingredient name can be found in database
                Transaction transaction = session.beginTransaction();
                Food food = session.createNativeQuery("select * from Food where product_name='" + name + "'", Food.class).uniqueResult();
                if (food == null) {
                    System.out.println("Ingredientul " + name + " nu se afla in baza de date, va fi adaugat");
                    food = new Food();
                    food.setProductName(name);
                    food.setMeasurementUnit(measurementUnit);
                    food.setStockQuantity(quantity);
                    session.persist(food);
                } else {
                    food.setStockQuantity(food.getStockQuantity() + quantity);
                }
                transaction.commit();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportAllFoodIntoXlsx() {

        System.out.println("Introduceti numele fisierului");
        String xlsxFilename = sc.nextLine();
        if (!Pattern.matches(".*\\.xlsx", xlsxFilename)) xlsxFilename = xlsxFilename + ".xlsx";

        System.out.println("Introduceti calea catre directorul unde sa fie creat fisierul");
        String directoryPath = sc.nextLine();

        //get from db data
        Transaction transaction = session.beginTransaction();
        List<Food> foodList = session.createNativeQuery("select * from Food", Food.class).list();
        transaction.commit();

        // create excel workbook
        try (XSSFWorkbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Food");
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);

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


            String fileLocation = directoryPath + "\\" + xlsxFilename;

            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRecipe() {
        //citim numele
        //citim ingredientul si cantitatea sa
        System.out.println("Introduceti numele retetei:");
        String recipeName = sc.nextLine();
        Transaction transaction = session.beginTransaction();
        Recipe recipe = new Recipe();
        recipe.setProductName(recipeName);
        session.persist(recipe);
        System.out.println("Urmeaza introducerea ingredientelor.");
        do {
            System.out.println("Introduceti numele ingredientului. 0 daca nu mai sunt ingrediente de introdus");
            String ingredientName = sc.nextLine().trim().toLowerCase();
            if (ingredientName.equals("0")) break;
            System.out.println("Introduceti cantitatea necesara");
            double quantity = sc.nextDouble();

            //check ingredient already exists, if not add it
            Food food = session.createNativeQuery("select * from Food where product_name='" + ingredientName + "'", Food.class).uniqueResult();
            if (food == null){
                System.out.println("Ingredientul nu se afla in baza de date si trebuie sa fie introdus. Precizati unitatea de masura a acestuia:");
                String measurementUnit = sc.nextLine();
                if (measurementUnit.equals("")) measurementUnit = sc.nextLine();
                food = new Food();
                food.setProductName(ingredientName);
                food.setMeasurementUnit(measurementUnit);
                food.setStockQuantity(0.0);
                session.persist(food);
                System.out.println("Ingredientul a fost adaugat cu succes!");

            }

            FoodRecipe foodRecipe = new FoodRecipe();
            foodRecipe.setRecipe(recipe);
            foodRecipe.setFood(food);
            foodRecipe.setQuantity(quantity);
            session.persist(foodRecipe);

        }while (true);
        transaction.commit();
        System.out.println("Reteta a fost adaugata cu succes");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {}

    }
}
