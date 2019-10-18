package com.adi.exam.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class PowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.ACTION_POWER_DISCONNECTED".equals(intent.getAction())) {


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            return;

                        }
                    },500);
                }
    }
}
