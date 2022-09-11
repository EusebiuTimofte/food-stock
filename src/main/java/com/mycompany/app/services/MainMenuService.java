package com.mycompany.app.services;

import com.mycompany.app.entities.Food;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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
                case 1:
                    addIngredient();
                    break;
                case 2:
                    addIngredientsFromExcel();
                    break;
                default:
                    throw new AssertionError();
            }

        }while (true);
    }

    public void showMenu(){
        System.out.println("---------MENU----------\n");
        System.out.println("-- MENIU INGREDIENTE");
        System.out.println("1. Adauga un singur ingredient");
        System.out.println("2. Adauga ingrediente dintr-un excel");
    }

    public int readOptionChoosed() {
        System.out.println("Introduceti optiunea dorita:");

        do {
            try {
                String optionTerminal = sc.nextLine();
                if (optionTerminal.equals("")) continue;
//                System.out.println("optionTerminal read: " + optionTerminal);
                int option= Integer.parseInt(optionTerminal);
                if (1 <= option && option <= 2 ) return option;
                else System.out.println("Introduceti un numar intreg intre 1 si 2");
            }catch (NumberFormatException e){
                System.out.println("Introduceti un numar intreg intre 1 si 2");
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
        Scanner sc = new Scanner(System.in);
        String excelPath = sc.nextLine();
        //read excel
        // add ingredient

        try(FileInputStream file = new FileInputStream(excelPath)) {
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
                }else {
                    food.setStockQuantity(food.getStockQuantity() + quantity);
                }
                transaction.commit();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
