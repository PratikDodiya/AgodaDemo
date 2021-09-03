package com.agodademo.utils;

import android.util.Log;

/**
 * Created by Pratik on 6/7/18.
 */
public class LogUtil {
    
    private final static String MATCH = "%s->%s->%d";
    private final static String CONNECTOR = ":<--->:";

    private static boolean SWITCH = true;

    private static String buildHeader() {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
        return stack == null ? "UNKNOWN" : stack.getLineNumber() + "=>";
//        StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
//        return stack == null ? "UNKNOWN" : String.format(Locale.getDefault(),
//                MATCH, stack.getClassName(), stack.getMethodName(), stack.getLineNumber()) + CONNECTOR;
    }

    public static void v(String TAG, Object msg) {
        if (SWITCH) Log.v(TAG, buildHeader() + msg.toString());
    }

    public static void d(String TAG, Object msg) {
        if (SWITCH) Log.d(TAG, buildHeader() + msg.toString());
    }

    public static void i(String TAG, Object msg) {
        if (SWITCH) Log.i(TAG, buildHeader() + msg.toString());
    }

    public static void w(String TAG, Object msg) {
        if (SWITCH) Log.w(TAG, buildHeader() + msg.toString());
    }

    public static void e(String TAG, Object msg) {
        if (SWITCH) Log.e(TAG, buildHeader() + msg.toString());
    }
}