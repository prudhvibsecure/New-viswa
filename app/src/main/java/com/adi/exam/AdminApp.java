package com.adi.exam;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminApp extends Application {
    private static final String TAG = "SmartApp";
    private static final String PREFIX = AdminApp.class.getSimpleName() + ": ";
    private static Context context;

    private static File logFile;
    private static final String newline = System.getProperty("line.separator");

    // ========================================================================================

    @Override
    public void onCreate() {
        super.onCreate();
        if (AdminApp.context == null) {
            AdminApp.context = getApplicationContext();
        }
        AdminApp.logger(PREFIX, "onCreate()", Log.DEBUG);


    }

    // ========================================================================================

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    // ========================================================================================

    /**
     * @return Application Context
     */
    public static Context getAppContext() {
        return AdminApp.context;
    }

    // ========================================================================================

    /**
     * @param prefix  of the current activity or service
     * @param log     string that will be logged
     * @param logType type of the Log - Debug, Error, Info, Warn...
     */
    public static void logger(String prefix, String log, int logType) {
        switch (logType) {
            case Log.INFO:
                Log.i(TAG, prefix + log);
                break;
            case Log.WARN:
                Log.w(TAG, prefix + log);
                break;
            case Log.VERBOSE:
                Log.v(TAG, prefix + log);
                break;
            case Log.ERROR:
                Log.e(TAG, prefix + log);
                break;
            default:
                Log.d(TAG, prefix + log);
        }


    }

    // ========================================================================================

    private static String getLogTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy'T'h:mm:ss a z", Locale.getDefault());
        return dateFormat.format(new Date()) + ": ";
    }

    // ========================================================================================

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void initializeLogFile() {
        File logFolder = context.getFilesDir();
        if (logFolder != null && !logFolder.exists()) {
            logFolder.mkdir();
        }

        if (logFile == null) {
            logFile = new File(logFolder, context.getString(R.string.app_name));
        }
    }

    // ========================================================================================


}
