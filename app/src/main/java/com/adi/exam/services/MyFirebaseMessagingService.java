package com.adi.exam.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.Html;
import android.util.Log;

import com.adi.exam.common.AppPreferences;
import com.adi.exam.database.Database;
import com.adi.exam.utils.TraceUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String m_type;
    public MyFirebaseMessagingService() {

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        if (token != null) {

            AppPreferences.getInstance(this).addToStore("token", token, false);

        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        TraceUtils.logE("From: ", remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {


            try {

                Map<String, String> params = remoteMessage.getData();
                Object object = new JSONObject(params);
                sendPushNotification(object);
            } catch (Exception e) {
                TraceUtils.logException(e);
            }


        }
    }
    private void sendPushNotification(Object json) {
        try {
            //getting the json data
            JSONObject data = new JSONObject(json.toString());
            String data_silent = data.optString("silent");
            if (data_silent.equalsIgnoreCase("true")) {

                String message_data = data.getString("message");
                JSONObject object = new JSONObject(message_data);
                String title_msg = object.optString("msg_det");
                String arry_data[] = title_msg.split(",");
                m_type = arry_data[0];
               /* if (m_type.equalsIgnoreCase("QF")) {
                    String file_name = arry_data[1];
                    Intent filedata=new Intent("com.file.data");
                    filedata.putExtra("filename",file_name);
                    sendBroadcast(filedata);
                }*/

            }
        } catch (JSONException e) {
            TraceUtils.logException(e);
        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }
}
