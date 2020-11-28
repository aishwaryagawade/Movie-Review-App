package com.example.trainingproject.utils;

import java.util.Scanner;

public class MyClass {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String string = sc.nextLine();
        int charCount = 0;
        int wordCount = 1;

        for(int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != ' '){

                charCount++;
         }else {
            wordCount ++;
        }
        }

        System.out.println(charCount);
        System.out.println(wordCount);
    }
}
