package com.spark.bitrade;

/**
 * @author shenzucai
 * @time 2019.10.24 19:07
 */
public class Stri {

    public static void main(String[] args) {
        Object o = "1";
        System.out.println(Integer.valueOf(String.valueOf(o)));
        System.out.println((Integer)o);
    }
}
