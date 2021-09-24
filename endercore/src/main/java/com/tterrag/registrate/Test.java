package com.tterrag.registrate;


public class Test {
    
    public static void main(String[] args) {
        for (double d = -0.1; d <= 0.1; d += 0.001) {
            System.out.println(test(d));
        }
    }
    
    private static int test(double val) {
        return (int)(val * 10430.378F) & '\uffff';
    }

}
