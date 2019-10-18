package com.adi.exam.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;

import androidx.core.app.NotificationCompat;

import com.adi.exam.R;


public class Notify {

    public static Notify get( Context context ) {
        return new Notify( context );
    }

    private final Context context;

    private Notify( Context context ) {
        this.context = context;
    }

    public void showIsLocked() {

        Resources r = context.getResources();
        show( r.getString( R.string.is_locked ) );
    }

    public void showIsReady() {

        Resources r = context.getResources();
        show(r.getString(R.string.is_ready));
    }

    private void show( String msg ) {

    }

}