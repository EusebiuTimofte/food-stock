package com.mycompany.app.services;

import com.mycompany.app.entities.Food;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;
import java.util.Scanner;

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
                default:
                    throw new AssertionError();
            }

        }while (true);
    }

    public void showMenu(){
        System.out.println("---------MENU----------\n");
        System.out.println("-- MENIU INGREDIENTE");
        System.out.println("1. Adauga un singur ingredient");
    }

    public int readOptionChoosed() {
        System.out.println("Introduceti optiunea dorita:");
        String optionTerminal = this.sc.nextLine();
        do {
            try {
                int option= Integer.parseInt(optionTerminal);
                if (1 <= option && option <= 10 ) return option;
                else System.out.println("Introduceti un numar intreg intre 1 si 1");
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
        return food;
    }

    public void addIngredient() {
        Food food = readIngredient();
        // get ingredient from db
        // if it exists, add units to the quantity
        // if not, create ingredient
        Transaction transaction = session.beginTransaction();
        List<Food> foodList = session.createNativeQuery("select * from Food where product_name=" + food.getProductName(), Food.class).list();
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
            foodFromDb.setStockQuantity(food.getStockQuantity() + food.getStockQuantity());
            System.out.println("Cantitatea ingredientului a fost actualizata");
            System.out.println(foodFromDb);
        }
        transaction.commit();
    }
}
