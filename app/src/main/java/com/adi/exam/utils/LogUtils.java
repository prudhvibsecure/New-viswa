package com.adi.exam.utils;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LogUtils {

    public static void logRequestLocally(final Context context, final String reqAPI, final String reqBody, final String reqHeaders, final Object response, final String excepTitle, final String excepMsg, final long resTime, String mode) {

        //mode = s server, mode = d device, mode  = b both
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(context.getExternalFilesDir(null).getAbsolutePath() + "/exam.log", true)));
                    pw.append(getCustomSystemTime());

                    pw.append("Req URL:");
                    pw.append(reqAPI);

                    pw.append("\n");

                    pw.append("Req Body:");
                    pw.append(reqBody);

                    pw.append("\n");

                    pw.append("Req Headers:");
                    pw.append(reqHeaders);

                    pw.append("\n");

                    pw.append("Response:");
                    pw.append(response.toString());

                    pw.append("\n");

                    pw.append("Excep Title:");
                    pw.append(excepTitle);

                    pw.append("\n");

                    pw.append("Excep Message:");
                    pw.append(excepMsg);

                    pw.append("\n");

                    pw.append("Res Time:");
                    pw.append(resTime+"");

                    pw.append("\n");

                    pw.append("------------------------------\n");

                    pw.flush();

                    pw.close();

                } catch (Exception ex) {
                    TraceUtils.logException(ex);
                }

            }
        }).start();

    }

    private static String getCustomSystemTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd:MMM:yyy hh:mm:ss", Locale.ENGLISH);
        java.util.Date date = new java.util.Date();
        return "-----"+dateFormat.format(date)+"-----\n";
    }

}
