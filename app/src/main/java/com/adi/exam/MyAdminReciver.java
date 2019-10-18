// Copyright 2016 Google Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//      http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.adi.exam;


import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adi.exam.common.Constants;
import com.adi.exam.common.Notify;
import com.adi.exam.common.Passwords;
import android.telephony.SmsManager;

/**
 * Handles events related to the managed profile.
 */
public class MyAdminReciver extends DeviceAdminReceiver {

    /**
     * @param context The context of the application.
     * @return The component name of this component in the given context.
     */
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), MyAdminReciver.class);
    }


    public void onEnabled(Context context, Intent intent) {
    }



    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {

        Log.i("pweb", getClass().getSimpleName() + ".onDisableRequested( " + intent.getAction() + " )");

        Notify.get( context ).showIsLocked();

        Passwords.get(context).setPassword( Constants.MASTER_PASSWORD ).lock();

        return "warn";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {

        Log.i("pweb", getClass().getSimpleName() + ".onDisabled( " + intent.getAction() + " )");

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage( Constants.MY_PHONE, null, "Disable", null, null);
    }
}
