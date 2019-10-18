package com.adi.exam.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

public class AppFonts {

    private static AppFonts appFonts = null;

    private Typeface bahnschrift = null;

    private Typeface swissbold = null;

    public static AppFonts getInstance(Context context) {

        if (appFonts == null)
            appFonts = new AppFonts(context);

        return appFonts;

    }

    private AppFonts(Context context) {

        AssetManager assets = context.getAssets();

        bahnschrift = Typeface.createFromAsset(assets, "fonts/bahnschrift.ttf");

        swissbold = Typeface.createFromAsset(assets, "fonts/swissbold.ttf");

    }

    public Typeface getBahnschrift() {
        return bahnschrift;
    }

    public Typeface getSwissbold() {
        return swissbold;
    }

    public void clear() {
        appFonts = null;
    }

}
