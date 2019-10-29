package com.adi.exam;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adi.exam.common.AppPreferences;
import com.adi.exam.services.DNotifyCloser;
import com.adi.exam.utils.TraceUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,CompoundButton.OnCheckedChangeListener{

    private ComponentName mAdminComponentName;

    private DevicePolicyManager mDevicePolicyManager;

    private PackageManager mPackageManager;

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_BACK,KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_HOME, KeyEvent.KEYCODE_POWER, KeyEvent.KEYCODE_APP_SWITCH));

    private View mDecorView;

    private static final String PREFIX = MainActivity.class.getSimpleName() + ": ";

    private CheckBox checkBoxAdmin;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private static final int REQUEST_ENABLE = 1;
    private static final int SET_PASSWORD = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        Settings.System.canWrite(this);
        mDecorView = getWindow().getDecorView();

       /* View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/
        View rootContent=findViewById(R.id.frame);


        findViewById(R.id.ll_appicon).setOnClickListener(this);
       /* devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, MyPolicyReceiver.class);
        checkBoxAdmin = (CheckBox) findViewById(R.id.checkBoxAdmin);
        checkBoxAdmin.setOnCheckedChangeListener(this);*/
        boolean my_safe_mode=getPackageManager().isSafeMode();
        Log.e("Main-safeMode",String.valueOf(my_safe_mode));
        if (my_safe_mode){
            Intent i = new Intent(this, SafeModeView.class);
            //  i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    |Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    |Intent.FLAG_ACTIVITY_SINGLE_TOP
                    |Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            this.finish();
        }
        setAssistBlocked(rootContent, true);
        mDevicePolicyManager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);

        mAdminComponentName = MyAdminReciver.getComponentName(this);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        // mDevicePolicyManager.lockNow();

        mPackageManager = getPackageManager();
        //mDevicePolicyManager.addUserRestriction(mAdminComponentName, UserManager.DISALLOW_SAFE_BOOT);
        // mDevicePolicyManager.clearDeviceOwnerApp(getPackageName());

        if (mDevicePolicyManager.isDeviceOwnerApp(getPackageName())) {

            setDefaultCosuPolicies(true);

        } else {

            Toast.makeText(getApplicationContext(),
                    R.string.not_device_owner, Toast.LENGTH_SHORT)
                    .show();
            //setDefaultCosuPolicies(true);
        }

        if (mDevicePolicyManager.isLockTaskPermitted(getPackageName())) {

            startLockTask();

           /* mDevicePolicyManager.setLockTaskFeatures(mAdminComponentName,
                    DevicePolicyManager.LOCK_TASK_FEATURE_HOME |
                            DevicePolicyManager.LOCK_TASK_FEATURE_OVERVIEW);*/

        } else {
            // Because the package isn't whitelisted, calling startLockTask() here
            // would put the activity into screen pinning mode.
        }

        //getEmails();

        //stopLockTask();   //Keep a button on this activity and when user clicks on it ask the user to enter the password. Take one API from Pradeep.
        //  This API should accept user id. If the user entered password is correc then call be line  ---> stopLockTask();
        registerReceiver(batteryChangeReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        AppPreferences.getInstance(this).addToStore("exam_on","0",true);
      /*  try{
            registerReceiver(apkBroadcaster, new IntentFilter(
                    "come.main.apks"));
        }catch (Exception e){
            e.printStackTrace();
        }*/
        // startService(new Intent(this, DNotifyCloser.class));
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkBoxAdmin.setVisibility(View.GONE);
        if(isChecked) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.request_explanation));
            startActivityForResult(intent, REQUEST_ENABLE);
        } else {
            devicePolicyManager.removeActiveAdmin(componentName);
        }
    }
    /*private void getEmails() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        // Getting all registered Google Accounts;
        // Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
        // Getting all registered Accounts;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                TraceUtils.logE("working fine", String.format("%s - %s", account.name, account.type));
            }
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE:
                    checkBoxAdmin.setChecked(true);
                    AdminApp.logger(PREFIX, "Enabling Policies Now", Log.DEBUG);

                    devicePolicyManager.setMaximumTimeToLock(componentName, 3000L);
                    devicePolicyManager.setMaximumFailedPasswordsForWipe(componentName, 5);
                    devicePolicyManager.setPasswordQuality(componentName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                    devicePolicyManager.setCameraDisabled(componentName, false);

                    boolean isSufficient = devicePolicyManager.isActivePasswordSufficient();

                    if (!isSufficient) {
                        Intent setPasswordIntent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                        startActivityForResult(setPasswordIntent, SET_PASSWORD);
                        devicePolicyManager.setPasswordExpirationTimeout(componentName, 10000L);
                    }
                    break;
            }
        } else {
            checkBoxAdmin.setChecked(false);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();


    }
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    BroadcastReceiver batteryChangeReceiver=new BroadcastReceiver() {

        int scale = -1;
        int level = -1;
        int voltage = -1;
        int temp = -1;

        @Override
        public void onReceive(Context context, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

            float batteryPct = level / (float) scale;
            int per=(int)(batteryPct*100);
            if (per<=20) {

                startService(new Intent(getApplicationContext(), DNotifyCloser.class));
               /* Toast.makeText(getApplicationContext(), per+" %", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));*/
//                pm.isPowerSaveMode();
//                Intent i = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
//                i.putExtra("android.intent.extra.KEY_CONFIRM", true);
//                startActivity(i);

               /* Window w = getWindow();
                WindowManager.LayoutParams lp = w.getAttributes();
                lp.screenBrightness =0;
                w.setAttributes (lp);*/
//                PowerManager pm = (PowerManager)getSystemService(Service.POWER_SERVICE);
//                pm.isPowerSaveMode();
//                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                //  manager.cancelAll();
            /*  Intent statusIntent=new Intent(Intent.ACTION_BATTERY_LOW);
                sendBroadcast(statusIntent);*/
            }else{
                if(startService(new Intent(getApplicationContext(), DNotifyCloser.class)) != null) {
                    stopService(new Intent(getApplicationContext(), DNotifyCloser.class));
                }
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryChangeReceiver);
        // unregisterReceiver(apkBroadcaster);
    }

    @Override
    public void onClick(View view) {

        try {

            switch (view.getId()) {

                case R.id.ll_appicon:
                    String stud_data=AppPreferences.getInstance(this).getFromStore("studentDetails");
                    if (stud_data==null|| TextUtils.isEmpty(stud_data)) {
                        Intent intent = new Intent(this, SplashActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(this, SriVishwa.class);
                        startActivity(intent);
                    }

                    break;

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void setDefaultCosuPolicies(boolean active) {
        // set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);
        setUserRestriction(UserManager.DISALLOW_APPS_CONTROL, active);
        setUserRestriction(UserManager.DISALLOW_USB_FILE_TRANSFER, active);
        setUserRestriction(UserManager.DISALLOW_APPS_CONTROL, active);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_INSTALL_APPS, active);
        setUserRestriction(UserManager.DISALLOW_UNINSTALL_APPS, active);
        setUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, active);
        setUserRestriction(UserManager.DISALLOW_OUTGOING_CALLS, active);

        // disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);

        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        // set system update policy
        if (active) {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    null);
        }

        // set this Activity as a lock task package

        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,
                active ? new String[]{getPackageName()} : new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(
                    mAdminComponentName, intentFilter, new ComponentName(
                            getPackageName(), MainActivity.class.getName()));
        } else {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName, getPackageName());
        }
    }

    private void setUserRestriction(String restriction, boolean disallow) {
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName,
                    restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName,
                    restriction);
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled) {
        if (enabled) {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
                            | BatteryManager.BATTERY_PLUGGED_USB
                            | BatteryManager.BATTERY_PLUGGED_WIRELESS));
        } else {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    "0"
            );
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //return super.onKeyDown(keyCode, event);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            //  sendBroadcast(new Intent("android.intent.action.ACTION_POWER_DISCONNECTED"));
        }else{
            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            // sendBroadcast(new Intent("android.intent.action.ACTION_POWER_DISCONNECTED"));
        }
    }
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {

        if (keyEvent.getKeyCode()== KeyEvent.KEYCODE_POWER){
            keyEvent.startTracking();
            return true;
        }
        if (this.blockedKeys.contains(Integer.valueOf(keyEvent.getKeyCode()))) {
            keyEvent.startTracking();

            return true;

        }
        return super.dispatchKeyEvent(keyEvent);
    }
    private void setAssistBlocked(View view, boolean blocked) {
        try {
            Method setAssistBlockedMethod = View.class.getMethod("setAssistBlocked", boolean.class);
            setAssistBlockedMethod.invoke(view, blocked);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_POWER) {
            // Here we can detect long press of power button
            Toast.makeText(this, "Power long pressed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
  /*  private BroadcastReceiver apkBroadcaster=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().startsWith("come.main.apks")){
                String a_name=intent.getStringExtra("apkname");
                stopLockTask();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                MainActivity.this.finish();
                String type = Utils.getMimeType(a_name);
                String path = Environment.getExternalStorageDirectory()
                        .toString() + "/" + a_name.trim();
                Uri paths = Uri.fromFile(new File(path));
                Intent intent_n = new Intent(Intent.ACTION_VIEW);
                intent_n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_n.setDataAndType(paths, type);
                startActivity(intent_n);
            }
        }
    };*/

}