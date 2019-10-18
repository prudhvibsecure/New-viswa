
package com.adi.exam;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.adi.exam.callbacks.IDialogCallbacks;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.common.NetworkInfoAPI;
import com.adi.exam.database.App_Table;
import com.adi.exam.dialogfragments.MessageDialog;

import com.adi.exam.utils.PrefUtils;
import com.adi.exam.utils.TraceUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/*import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;*/

public class SplashActivity extends AppCompatActivity implements IDialogCallbacks {
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_BACK,KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_HOME, KeyEvent.KEYCODE_POWER, KeyEvent.KEYCODE_APP_SWITCH));
    private NetworkInfoAPI networkInfoAPI = null;
    public static final int OVERLAY_PERMISSION_REQ_CODE = 4545;
    AlertDialog.Builder builder;

    private ComponentName mAdminComponentName;

    private DevicePolicyManager mDevicePolicyManager;

    private PackageManager mPackageManager;
    //protected CustomViewGroup blockingView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);


        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        mDevicePolicyManager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);

        mAdminComponentName = MyAdminReciver.getComponentName(this);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        mPackageManager = getPackageManager();

        //mDevicePolicyManager.clearDeviceOwnerApp(getPackageName());

        if (mDevicePolicyManager.isDeviceOwnerApp(getPackageName())) {

            //setDefaultCosuPolicies(true);

        } else {

//            Toast.makeText(getApplicationContext(),
//                    R.string.not_device_owner, Toast.LENGTH_SHORT)
//                    .show();

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

        // Settings.Secure.putInt(getContentResolver(),Settings.Secure.USB_MASS_STORAGE_ENABLED,0);
        PrefUtils.setKioskModeActive(false, getApplicationContext());
        String networkType = getNetWorkObject().execute("getConnectionInfo");

//        String[] command = { "/system/bin/sh", "-c", "echo xxx > /sys/bus/usb/devices/usb3/power/level", };
//        try {
//            Runtime.getRuntime().exec(command);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //  Settings.Secure.putInt(getContentResolver(),Settings.Secure.USB_MASS_STORAGE_ENABLED,0);
        /*if (networkType.equalsIgnoreCase("none")) {

            showokPopUp(R.drawable.pop_ic_info, getString(R.string.alert), getString(R.string.nipcyns), 1, this, false);

            return;

        }
*/
        if (AppPreferences.getInstance(this).getFromStore("User-Agent").length() == 0) {

            WebView webview = new WebView(this);

            AppPreferences.getInstance(this).addToStore("User-Agent", webview.getSettings().getUserAgentString() + "", false);

            webview = null;

        }
        boolean flag = AppPreferences.getInstance(this).getBooleanFromStore("exceldone");
        if (!flag) {
            File folder = new File(Environment.getExternalStorageDirectory(), ".allimages");
            folder.mkdir();
            loadData();

        } else {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (!AppPreferences.getInstance(SplashActivity.this).getFromStore("studentDetails").equalsIgnoreCase("")) {
                        lauchLoginActivity(SriVishwa.class);
                    } else {

                        lauchLoginActivity(LoginActivity.class);
                    }

                }

            }, 5000);

        }
    }

    public NetworkInfoAPI getNetWorkObject() {
        if (networkInfoAPI == null) {
            networkInfoAPI = new NetworkInfoAPI();
            networkInfoAPI.initialize(this);
        }
        return networkInfoAPI;
    }

    @Override
    public void onOK(int requestId) {

        switch (requestId) {

            case 1:

                SplashActivity.this.finish();

                break;

            case 2:

                break;

        }

    }

    @Override
    public void onCancel(int requestId) {
        switch (requestId) {

            case 1:

                break;

            case 2:

                break;
        }
    }

    public void showokPopUp(int drawableId, String title, String message, int requestId, IDialogCallbacks callbacks, boolean showCancel) {

        try {

            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("message", message);
            bundle.putInt("requestId", requestId);
            bundle.putInt("drawableId", drawableId);
            bundle.putBoolean("showcancel", showCancel);
            MessageDialog messages = MessageDialog.newInstance(bundle);
            messages.setIDialogListener(callbacks);
            messages.show(this.getSupportFragmentManager(), "dialog");

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }

    public void lauchLoginActivity(Class<?> cls) {

        Intent intent = new Intent(this, cls);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

        SplashActivity.this.finish();

    }

    public void readExcelFileFromAssets(String[] columnNames, int sheetNameIndex, String tableName, App_Table table) {

        try {

            InputStream myInput;

            // initialize asset manager
            AssetManager assetManager = getAssets();

            //  open excel sheet
            myInput = assetManager.open("data.xls");

            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

           /* // Finds the workbook instance for XLSX file
            XSSFWorkbook myWorkBook = new XSSFWorkbook(myInput);

            // Return first sheet from the XLSX workbook
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);*/

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(sheetNameIndex);

            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();

            int rowno = 0;

            //textView.append("\n");

            JSONArray jsonArray = new JSONArray();

            while (rowIter.hasNext()) {

                TraceUtils.logE("row no", rowno + "");

                HSSFRow myRow = (HSSFRow) rowIter.next();

                Iterator<Cell> cellIter = myRow.cellIterator();

                JSONObject jsonObject = new JSONObject();

                int cellIndex = 0;

                while (cellIter.hasNext()) {

                    HSSFCell myCell = (HSSFCell) cellIter.next();

                    String data = myCell.toString();

                    TraceUtils.logE("Cell value", data);

                    jsonObject.put(columnNames[cellIndex], data);

                    ++cellIndex;

                }

                rowno++;

                jsonArray.put(jsonObject);

            }

            if (jsonArray.length() > 0) {

                table.insertMultipleRecords(jsonArray, tableName);

                AppPreferences.getInstance(this).addBooleanToStore("exceldone", true, false);

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void loadData() {

        try {

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {

                        App_Table table = new App_Table(SplashActivity.this);


                        String[] cloumnsNames = table.getColumnNames("STUDENTS");
//                        readExcelFileFromAssets(cloumnsNames, 0/*"student"*/, "STUDENTS", table);


                        cloumnsNames = table.getColumnNames("COURSES");
                        readExcelFileFromAssets(cloumnsNames, 4/*"student"*/, "COURSES", table);


                        cloumnsNames = table.getColumnNames("TOPICS");
                        readExcelFileFromAssets(cloumnsNames, 1/*"TOPICS"*/, "TOPICS", table);


                        cloumnsNames = table.getColumnNames("LESSONS");
                        readExcelFileFromAssets(cloumnsNames, 2/*"student"*/, "LESSONS", table);


                        cloumnsNames = table.getColumnNames("QUESTIONS");
                        readExcelFileFromAssets(cloumnsNames, 3/*"student"*/, "QUESTIONS", table);


                    } catch (Exception ex) {

                        TraceUtils.logException(ex);

                    } finally {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (!AppPreferences.getInstance(SplashActivity.this).getFromStore("studentDetails").equalsIgnoreCase("")) {
                                    lauchLoginActivity(SriVishwa.class);
                                } else {

                                    lauchLoginActivity(LoginActivity.class);
                                }

                                // lauchLoginActivity(LoginActivity.class);
                            }
                        });

                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                lauchLoginActivity(LoginActivity.class);
                            }
                        }, 100);*/

                    }
                }
            }).start();

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

   /* @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.blockedKeys.contains(Integer.valueOf(event.getKeyCode()))) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Html.fromHtml("<b>WARNING:<b>") + "Unusual activity detected");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
        return super.dispatchKeyEvent(event);
    }
*/
    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

   /* public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

   /* public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!z) {
            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
           // Toast.makeText(this, "This action is not required..", Toast.LENGTH_SHORT).show();
           *//* if (builder==null) {
                sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                builder = new AlertDialog.Builder(this);
                builder.setMessage(Html.fromHtml("<b>WARNING:<b>") + "This action is not required..");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }*//*

        }

    }*/
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //return super.onKeyDown(keyCode, event);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        }else{

        }
    }
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {

        if (keyEvent.getKeyCode()==3&&keyEvent.getKeyCode()==187){

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

        if (this.blockedKeys.contains(Integer.valueOf(event.getKeyCode()))) {

            return true;

        }
        return super.onKeyLongPress(keyCode, event);
    }

}

