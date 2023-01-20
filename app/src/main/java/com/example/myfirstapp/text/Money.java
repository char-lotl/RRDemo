package com.example.myfirstapp.text;

public class Money {
    public static String decimalize(String s) {
        return s.replaceFirst("(\\d{2})$", ".$1");
    }
    public static String decimalize(Long l) {
        return decimalize(l.toString());
    }
}
