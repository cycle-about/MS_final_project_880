package edu.jhu.jhg.finalreportreleasedatacheck.gui;

import java.util.Random;

public class GraphExample {

    public static void main(String[] args) {
        String greeting = getGreeting();                  // main_0 [1]
        System.out.println(greeting);                       // main_1 [2]
    }
    /* ---- Edges ----
        main_0 -> getGreeting_0           [start] [1 -> 3]
        main_0 -> main_1                  [end]
     */

    private static String getGreeting() {
        boolean b = new Random().nextBoolean(); // getGreeting_0 [3]
        if (b) {                          // getGreeting_1 [4]
            return "Hello";
        } else {                          // getGreeting_2 [5]
            return "Hi!";
        }
    }
    /* ---- Edges ----
        getGreeting_0 -> getGreeting_1    [3 -> 4]
        getGreeting_0 -> getGreeting_2    [3 -> 5]
        getGreeting_1 -> main_0           [4 -> 2]
        getGreeting_2 -> main_0           [5 -> 2]
     */
}


