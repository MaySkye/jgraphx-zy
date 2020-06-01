package com.mxgraph.examples.swing.util.ww;

import java.text.MessageFormat;

/**
 * 日志工具
 */
public class WWLogger {
    enum LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        NONE
    }

    public static final LogLevel logLevel = LogLevel.ERROR;

    private static void printCurrentMethod() {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        StackTraceElement element = (StackTraceElement) stacks[4];
        String out = MessageFormat.format("      \033[47;35m{0} --- {1}()  Line: {2}\033[0m", element.getClassName(), element.getMethodName(), element.getLineNumber());
        System.out.println(out);
    }


    public static void debugF(String pattern, Object... args) {
        doLog(LogLevel.DEBUG, MessageFormat.format(pattern, args));
    }

    public static void debug(Object... args) {
        String content = "";
        for (Object arg : args) {
            content += arg + " ";
        }
        doLog(LogLevel.DEBUG, content);
    }


    public static void infoF(String pattern, Object... args) {
        doLog(LogLevel.INFO, MessageFormat.format(pattern, args));
    }

    public static void info(Object... args) {
        String content = "";
        for (Object arg : args) {
            content += arg + " ";
        }
        doLog(LogLevel.INFO, content);
    }

    public static void warnF(String pattern, Object args) {
        doLog(LogLevel.WARN, MessageFormat.format(pattern, args));
    }

    public static void warn(Object... args) {
        String content = "";
        for (Object arg : args) {
            content += arg + " ";
        }
        doLog(LogLevel.WARN, content);
    }

    public static void error(Object content) {
        doLog(LogLevel.ERROR, content);
    }

    public static void errorF(String pattern, Object... args) {
        doLog(LogLevel.ERROR, MessageFormat.format(pattern, args));
    }

    public static void error(Object... args) {
        String content = "";
        for (Object arg : args) {
            content += arg + " ";
        }
        doLog(LogLevel.ERROR, content);
    }

    private static boolean isNeedPrint(LogLevel printLevel) {
        return printLevel.ordinal() >= logLevel.ordinal();
    }

    private static void doLog(LogLevel level, Object content) {
        if (!isNeedPrint(level)) return;
        switch (level) {
            case DEBUG:
                System.out.print("\033[44;37m" + "【WW】debug:" + "\033[0m ");
                System.out.print("\033[30;34m" + content + "\033[0m");
                printCurrentMethod();
                break;
            case INFO:
                System.out.print("\033[42m" + "【WW】Log:" + "\033[0m ");
                System.out.print(content);
                printCurrentMethod();
                break;
            case WARN:
                System.out.print("\033[43m" + "【WW】warn:" + "\033[0m ");
                System.out.print("\033[37m" + content + "\033[0m");
                printCurrentMethod();
                break;
            case ERROR:
                System.out.print("\033[41m" + "【WW】error:" + "\033[0m ");
                System.out.print("\033[31m" + content + "\033[0m");
                printCurrentMethod();
        }
    }
}

