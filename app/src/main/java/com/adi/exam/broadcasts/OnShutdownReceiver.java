package com.adi.exam.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adi.exam.common.Constants;
import com.adi.exam.common.Passwords;

public class OnShutdownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("pweb", getClass().getSimpleName() + ".onReceive( action: " + intent.getAction() + " )");

        Passwords.get(context).setPassword(Constants.MASTER_PASSWORD);

    }


}