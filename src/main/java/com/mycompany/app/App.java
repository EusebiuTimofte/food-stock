package com.mycompany.app;

import com.mycompany.app.services.MainMenuService;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        /*
        1.2. permite adaugarea de alimente

                              1.2.1. printr-un excel

                              1.2.2. manual cate unul
         */
        new MainMenuService().start();
    }
}
