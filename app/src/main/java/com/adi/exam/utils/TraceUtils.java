package com.adi.exam.utils;

import android.util.Log;

public class TraceUtils {

    public static void logException(Exception e) {
        e.printStackTrace();
    }

    public static void logE(String key, String value) {
       Log.e(key, value);
    }

    public static void logCrashlytics(Exception e,String urlKey, long timeTaken, int statusCode) {

    }

}
