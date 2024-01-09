package com.adira.contact.service;

public class Example {
    public static void main(String[] args) {
        try {
            System.out.println("Entering main");
            func1();
            System.out.println("Exiting main");
        } catch (Exception e) {
            System.out.println("Error in main: " + e.getMessage());
        }
    }

    static void func1() {
        try {
            System.out.println("Entering func1");
            func2();
            System.out.println("Exiting func1");
        } catch (Exception e) {
            System.out.println("Error in func1: " + e.getMessage());
        }
    }

    static void func2() {
        System.out.println("Entering func2");
        func3();
        System.out.println("Exiting func2");
    }

    static void func3() {
        System.out.println("Entering func3");
        func4();
        System.out.println("Exiting func3");
    }

    static void func4() {
        // Logic di func4 yang menyebabkan exception
        throw new RuntimeException("This is an error in func4");
    }
}