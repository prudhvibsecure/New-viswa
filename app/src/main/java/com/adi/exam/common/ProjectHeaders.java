package com.adi.exam.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.view.WindowManager;

import com.adi.exam.utils.TraceUtils;

public class ProjectHeaders {

    private static ProjectHeaders headers = null;

    private Item item = null;

    public static ProjectHeaders getInstance() {
        if (headers == null)
            headers = new ProjectHeaders();
        return headers;
    }

    private ProjectHeaders() {

    }

    public Item getHeaders(Context context) {

        if (item != null && item.size() > 0) {

            if (item.getAttribute("User-Agent").length() == 0)
                item.setAttribute("User-Agent", AppPreferences.getInstance(context).getFromStore("User-Agent") + "");

            /*String token = AppPreferences
                    .getInstance(context).getFromStore("oauth");

            if (token.length() == 0)
                token = System.currentTimeMillis() + "";

            item.setAttribute("X-TOKENID", token);
            */
            return item;
        }

        item = new Item();

        try {

            if (item.getAttribute("User-Agent").length() == 0)
                item.setAttribute("User-Agent", AppPreferences.getInstance(context).getFromStore("User-Agent") + "");
            
            item.setAttribute("X-App-OEM",
                    Build.MANUFACTURER + "");

            item.setAttribute("X-App-Model",
                    Build.MODEL + "");

            item.setAttribute("X-App-OS", "Android");

            item.setAttribute("X-App-OSVersion",
                    Build.VERSION.RELEASE + "");

            item.setAttribute("X-App-Res", getResolution(context));

            item.setAttribute("X-VERSION", applicationVName(context) + "");

            item.setAttribute("Content-Type", "application/json");

            /*String token = AppPreferences
                    .getInstance(context).getFromStore("oauth");

            if (token.length() == 0)
                token = System.currentTimeMillis() + "";

            item.setAttribute("X-TOKENID", token);*/

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

        return item;
    }

    private String getResolution(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = windowManager.getDefaultDisplay().getHeight();
        int width = windowManager.getDefaultDisplay().getWidth();
        return width + "x" + height;
    }

    public String applicationVName(Context context) {
        String versionName = "";
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
            TraceUtils.logException(e);
        }
        return versionName;
    }

    private int applicationVCode(Context context) {
        int versionCode = 0;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            versionCode = info.versionCode;
        } catch (NameNotFoundException e) {
            TraceUtils.logException(e);
        }
        return versionCode;
    }

    public void onDestroy() {

    }

}
