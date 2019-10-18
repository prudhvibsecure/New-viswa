package com.adi.exam.broadcasts;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adi.exam.MyAdminReciver;
import com.adi.exam.common.Constants;
import com.adi.exam.common.Notify;
import com.adi.exam.common.Passwords;

public class OnBootReceiver extends BroadcastReceiver {

    /**
     * The password will be set to the childs password. So the device can be unlock by the child.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("pweb", getClass().getSimpleName() + ".onReceive( " + intent.getAction() + " )");

        ComponentName admin = new ComponentName( context, MyAdminReciver.class );
        Passwords.get(context)
                .setPassword(Constants.CHILD_PASSWORD)
                .setQuality(admin, DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);

        Notify.get( context ).showIsReady();
    }
}
