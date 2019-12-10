package com.mxgraph.examples.swing.util;

import java.time.LocalDateTime;

/**
 * Created by QiboLee on 2017/10/20 0020.
 */
public class MLog {

    public static String getTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return String.format("[%d-%02d-%02d %02d:%02d:%02d] ", localDateTime.getYear(), localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(),
                localDateTime.getSecond());
    }

    public static void print(String s) {
        System.out.print(s);
    }

    public static void println(String s) {
        System.out.println(getTime() + s);
    }

    public static void println() {
        System.out.println();
    }
}
