package com.adi.exam.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;

import com.adi.exam.MainActivity;
import com.adi.exam.SplashActivity;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.utils.PrefUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DNotifyCloser extends Service {
    private static final long INTERVAL = TimeUnit.SECONDS.toMillis(1);
    private Context ctx = null;
    private boolean running = false;
    Thread t=null;
    @Override
    public void onDestroy() {

        startService(new Intent(this, DNotifyCloser.class));
        running = false;
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        running = true;
        ctx = this;

        // start a thread that periodically checks if your app is in the foreground
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    String exam_process= AppPreferences.getInstance(ctx).getFromStore("exam_on");
                    if (exam_process.startsWith("0")) {
                        handleKioskMode();
                       // safeMode();
                        try{
                            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                            Thread.sleep(INTERVAL);
                        }  catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                } while (running);
                stopSelf();
            }
        });

        t.start();
        return Service.START_NOT_STICKY;
    }

    private void handleKioskMode() {
        // is Kiosk Mode active?
        if(!isForeground()) {
            // is App in background?
            //  if(isInBackground()) {
            restoreApp(); // restore!
            //  }
        }
    }
    private void restoreApp() {
        // Restart activity
        Intent i = new Intent(ctx, MainActivity.class);
        //  i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                |Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                |Intent.FLAG_ACTIVITY_SINGLE_TOP
                |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ctx.startActivity(i);
    }
    private boolean isForeground() {
        boolean isInForeground = false;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses!=null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(getPackageName())) {

                                isInForeground = true;
                            }else{
                                am.killBackgroundProcesses(activeProcess);
                            }
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(getPackageName())) {
                isInForeground = true;
            }
        }

        return isInForeground;
    }

}
