package com.adi.exam;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.webkit.WebView;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.adi.exam.broadcasts.OnScreenOffReceiver;
import com.adi.exam.services.KioskService;
import com.adi.exam.utils.TraceUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class SriVishwaApp extends MultiDexApplication implements ActivityLifecycleCallbacks {

    private static boolean isInterestingActivityVisible;

    private static SriVishwaApp instance;

    private OnScreenOffReceiver onScreenOffReceiver;

    private PowerManager.WakeLock wakeLock;

    public SriVishwaApp() {
        super();
        instance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {

            instance = this;

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
            ImageLoader.getInstance().init(config);

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

        try {
            new WebView(this).destroy();
        } catch (Exception e) {
            TraceUtils.logException(e);
        }
      //  registerKioskModeScreenOffReceiver();
        //startKioskService();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof SriVishwa) {
            isInterestingActivityVisible = true;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity instanceof SriVishwa) {
            isInterestingActivityVisible = false;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public static boolean isInterestingActivityVisible() {
        return isInterestingActivityVisible;
    }

    public static SriVishwaApp get() {
        return instance;
    } //TODO:/// check how to avoid this call
   /* private void registerKioskModeScreenOffReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        onScreenOffReceiver = new OnScreenOffReceiver();
        registerReceiver(this.onScreenOffReceiver, intentFilter);
    }
    public PowerManager.WakeLock getWakeLock() {
        if (wakeLock == null) {

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
        }
        return wakeLock;
    }
    private void startKioskService() {
        startService(new Intent(this, KioskService.class));
    }*/
   public Bitmap icon() {
       Drawable icon = getPackageManager().getApplicationIcon(getApplicationInfo());
       return ((BitmapDrawable)icon).getBitmap();
   }
}
