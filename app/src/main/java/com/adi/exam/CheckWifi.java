package com.adi.exam;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adi.exam.fragments.WifiFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckWifi extends AppCompatActivity {
    private FragmentManager fm;
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_HOME, KeyEvent.KEYCODE_POWER));
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_fagment);

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        WifiFragment wifiFragment= WifiFragment.newInstance("Login");
        ft.replace(R.id.main_container,wifiFragment);
        ft.commit();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            //Toast.makeText(this, "This action is not required..", Toast.LENGTH_SHORT).show();
        }else{
            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);

    }
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {

        if (keyEvent.getKeyCode()==KeyEvent.KEYCODE_POWER){
            keyEvent.startTracking();
            return true;
        }
        if (this.blockedKeys.contains(Integer.valueOf(keyEvent.getKeyCode()))) {
            keyEvent.startTracking();

            return true;

        }
        return super.dispatchKeyEvent(keyEvent);
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        if (event.getKeyCode()==KeyEvent.KEYCODE_POWER) {
            Toast.makeText(this, "Power Clicked...", Toast.LENGTH_SHORT).show();
            return true;

        }
        return super.onKeyLongPress(keyCode, event);
    }
}
