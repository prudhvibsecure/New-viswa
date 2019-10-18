package com.adi.exam.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adi.exam.MainActivity;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean my_safe_mode=context.getPackageManager().isSafeMode();

        Log.e("safeMode",String.valueOf(my_safe_mode));
        //Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
        if (my_safe_mode) {

            Intent myIntent = new Intent(context, MainActivity.class);

            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(myIntent);

        }

    }

}