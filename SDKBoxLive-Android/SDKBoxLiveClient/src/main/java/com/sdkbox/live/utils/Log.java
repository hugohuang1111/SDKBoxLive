package com.sdkbox.live.utils;

public class Log {

    private enum LEVEL {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    private static LEVEL level = LEVEL.VERBOSE;

    public static void v(String tag, String s) {
        if (level.ordinal() > LEVEL.VERBOSE.ordinal()) {
            return;
        }
        android.util.Log.d(tag, s);
    }

    public static void d(String tag, String s) {
        if (level.ordinal() > LEVEL.DEBUG.ordinal()) {
            return;
        }
        android.util.Log.d(tag, s);
    }

    public static void i(String tag, String s) {
        if (level.ordinal() > LEVEL.INFO.ordinal()) {
            return;
        }
        android.util.Log.d(tag, s);
    }

    public static void w(String tag, String s) {
        if (level.ordinal() > LEVEL.WARN.ordinal()) {
            return;
        }
        android.util.Log.d(tag, s);
    }

    public static void e(String tag, String s) {
        if (level.ordinal() > LEVEL.ERROR.ordinal()) {
            return;
        }
        android.util.Log.d(tag, s);
    }
}
