package com.adi.exam;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.adi.exam.callbacks.IDialogCallbacks;
import com.adi.exam.callbacks.IFileUploadCallback;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.common.AppSettings;
import com.adi.exam.database.App_Table;
import com.adi.exam.database.Database;
import com.adi.exam.dialogfragments.AppUpdateDialog;
import com.adi.exam.dialogfragments.ExitDialog;
import com.adi.exam.dialogfragments.MessageDialog;
import com.adi.exam.fragments.AIIMSemplates;
import com.adi.exam.fragments.AllQuestions;
import com.adi.exam.fragments.AllQuestions_New;
import com.adi.exam.fragments.AllQuestions_view;
import com.adi.exam.fragments.AssignResultsPage;
import com.adi.exam.fragments.Assignment;
import com.adi.exam.fragments.AssignmentHistory;
import com.adi.exam.fragments.AssignmentList;
import com.adi.exam.fragments.BITSATTemplates;
import com.adi.exam.fragments.ChangePassword;
import com.adi.exam.fragments.Dashboard;
import com.adi.exam.fragments.ExamList;
import com.adi.exam.fragments.ExamSubmitConfirmationPage;
import com.adi.exam.fragments.ExamTemplates;
import com.adi.exam.fragments.Instructions;
import com.adi.exam.fragments.JEEAdvanceTemplates;
import com.adi.exam.fragments.JEEemplates;
import com.adi.exam.fragments.JIPMERSemplates;
import com.adi.exam.fragments.KVPYTemplates;
import com.adi.exam.fragments.Lessons;
import com.adi.exam.fragments.Materials;
import com.adi.exam.fragments.NEETemplates;
import com.adi.exam.fragments.ParentFragment;
import com.adi.exam.fragments.ResultsPage;
import com.adi.exam.fragments.Subjects;
import com.adi.exam.fragments.Topics;
import com.adi.exam.fragments.WiFiSettingsInApp;
import com.adi.exam.fragments.WifiFragment;
import com.adi.exam.services.ApkFileDownloader;
import com.adi.exam.services.DNotifyCloser;
import com.adi.exam.services.DownloadFileAsync;
import com.adi.exam.tasks.FileUploader;
import com.adi.exam.tasks.HTTPPostTask;
import com.adi.exam.tasks.ImageProcesser;
import com.adi.exam.utils.TraceUtils;
import com.adi.exam.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import ir.mahdi.mzip.zip.ZipArchive;

public class SriVishwa extends AppCompatActivity
        implements IFileUploadCallback, NavigationView.OnNavigationItemSelectedListener, ParentFragment.OnFragmentInteractionListener, Dashboard.OnListFragmentInteractionListener, View.OnClickListener, IItemHandler, AppUpdateDialog.IUpdateCallback {

    //   private final List blockedKeys = new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(25), Integer.valueOf(24)}));
    int count = 5;
    int count_page;
    private int downloadID;

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_BACK,KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_HOME, KeyEvent.KEYCODE_POWER, KeyEvent.KEYCODE_APP_SWITCH));

    private Toolbar toolbar;

    private ActionBarDrawerToggle toggle;

    private DrawerLayout drawer;

    private FragmentManager manager = null;

    private Stack<ParentFragment> fragStack = null;

    private JSONObject studentDetails;

    private JSONArray allQuestions;

    ParentFragment tempFrag = null;

    SharedPreferences sp;

    public int check = 0;

    public String myData = "";

    App_Table app_table;

    private String filename_zip=null;
    private boolean installFlag=false;

    private int pageCount = 0;
    private ComponentName mAdminComponentName;

    private DevicePolicyManager mDevicePolicyManager;

    private PackageManager mPackageManager;

    TextView note, downloadnote, test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_srivishwa);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);



        note = findViewById(R.id.note);

        downloadnote = findViewById(R.id.download_note);

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

        app_table = new App_Table(this);
        toolbar = findViewById(R.id.toolbar);
        allQuestions = new JSONArray();
        toolbar.setTitle(R.string.dashboard);

        setSupportActionBar(toolbar);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        Date date = new Date();
        String sdate = String.valueOf(formatter.format(date));

        if (AppPreferences.getInstance(this).getFromStore("td").equals("")) {
            AppPreferences.getInstance(this).addToStore("td", sdate, false);
            count = 5;
        } else {
            if (AppPreferences.getInstance(this).getFromStore("td").equals(sdate)) {

            } else {
                AppPreferences.getInstance(this).addToStore("td", sdate, false);
                count = 5;
            }
        }

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        sp = getSharedPreferences("time", MODE_PRIVATE);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        fragStack = new Stack<>();

        manager = getSupportFragmentManager();

        if (savedInstanceState != null) {

            removeAllFragments();

        }

        swiftFragments(Dashboard.newInstance(), "home");

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onKeyDown(4, null);

            }

        });

        try {

            studentDetails = new JSONObject(AppPreferences.getInstance(this).getFromStore("studentDetails"));

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        updateToken();
      //  getParagraph();
        //checkAppUpdate();



        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ((TextView) navigationView.findViewById(R.id.tv_appversion)).setText(getString(R.string.app_version, applicationVName()));

        View header = navigationView.getHeaderView(0);

        ((TextView) header.findViewById(R.id.tv_name)).setText(studentDetails.optString("student_name"));

        ((TextView) header.findViewById(R.id.tv_classbatch)).setText(studentDetails.optString("roll_no"));

        IntentFilter apk = new IntentFilter("com.attach.apk");
        apk.setPriority(1);
        registerReceiver(mBroadcastReceiverAttach, apk);
    }
    public JSONObject getStudentDetails() {

        return studentDetails;

    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sri_vishwa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_materials) {

            showSubjects();

        } else if (id == R.id.nav_logout) {

            AppPreferences.getInstance(this).clearSharedPreferences(true);
            //SriVishwa.this.finish();
            startActivity(new Intent(SriVishwa.this, LoginActivity.class));
            SriVishwa.this.finish();

        } else if (id == R.id.nav_changepwd) {

            changePassword();

        } else if (id == R.id.nav_wifisettings) {

            swiftFragments(WifiFragment.newInstance("Settings"), "wifisettings");
//            if (checkPermission("android.permission.CHANGE_WIFI_STATE", 300) == 1) {
//
//                showWiFiSettings();
//
//            }

            //showWiFiSettings();

        }else if (id==R.id.nav_unlock){
            stopLockTask();
        }
        else if(id == R.id.nav_download)
        {
            getNewQuestions();
        }
        else if(id == R.id.nav_update)
        {
            checkAppUpdate();
            /*if (checkPermission("android.permission.READ_EXTERNAL_STORAGE", 400) == 1) {
                File mediaStorage = new File(Environment.getExternalStorageDirectory()
                        .toString());
                if (!mediaStorage.exists()) {
                    mediaStorage.mkdirs();
                }
                Utils.downloadDilog(this);
                startService(ApkFileDownloader.getDownloadService(this, "https://bsecuresoftechsolutions.com/viswa/assets/upload/version/", String.valueOf(mediaStorage), "viswa.apk"));
            }*/
        }
        else
        {
            clearData();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        return true;

    }

    private void clearData() {
        app_table.clearData();
    }

    private void changePassword() {

        swiftFragments(ChangePassword.newInstance(), "ChangePassword");

    }

    public void showWiFiSettings() {

        swiftFragments(WiFiSettingsInApp.newInstance(), "wifisettings");

    }

    @Override
    public void onFragmentInteraction(int stringid, boolean blockMenu) {

        toolbar.setTitle(stringid);

        lockUnLockMenu(blockMenu);

    }

    @Override
    public void onFragmentInteraction(String title, boolean blockMenu) {

        toolbar.setTitle(title);

        lockUnLockMenu(blockMenu);

    }

    public void lockUnLockMenu(boolean mode) {

        toggle.setDrawerIndicatorEnabled(mode);

        drawer.setDrawerLockMode(mode ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    public void onListFragmentInteraction(JSONObject jsonObject) {

        try {

            int position = jsonObject.getInt("position");

            switch (position) {

                case 1:
                    if (isNetworkAvailable()) {
                         getCheckTime(1);
                    } else {
                        Toast.makeText(this, "Please connect your network", Toast.LENGTH_SHORT).show();
                        if (checkPermission("android.permission.READ_EXTERNAL_STORAGE", 100) == 1) {
                            swiftFragments(ExamList.newInstance(), "examlist");

                        }

                    }
                    break;

                case 2:
                    if (isNetworkAvailable()) {
                        Intent in = new Intent(this, ExamHistory.class);
                        startActivity(in);
                    } else {
                        Toast.makeText(this, "Please connect your network", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case 3:
                    if (checkPermission("android.permission.READ_EXTERNAL_STORAGE", 100) == 1) {

                        showAssignmentList();
                    }

                    break;

                case 4:
                    if (isNetworkAvailable()) {
                        showHistoryList();
                    } else {
                        Toast.makeText(this, "Please connect your network", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void getCheckTime(int type) {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("system_time", System.currentTimeMillis());

            jsonObject.put("student_id", studentDetails.getString("student_id"));

            HTTPPostTask post = new HTTPPostTask(this, this);
            post.disableProgress();
            post.userRequest(getString(R.string.plwait), 191, "check_exam_time", jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        try {

            switch (v.getId()) {

                case R.id.tv_ok:

                    SriVishwa.this.finish();

                    break;

                case R.id.tv_cancel:
                    //PrefUtils.setKioskModeActive(false, getApplicationContext());
                    break;

                case R.id.tv_updatecancel:


                    break;

                case R.id.tv_updateok:
                    if (checkPermission("android.permission.READ_EXTERNAL_STORAGE", 400) == 1) {
                        File mediaStorage = new File(Environment.getExternalStorageDirectory()
                                .toString());
                        if (!mediaStorage.exists()) {
                            mediaStorage.mkdirs();
                        }
                        Utils.downloadDilog(this);
                        startService(ApkFileDownloader.getDownloadService(this, "https://bsecuresoftechsolutions.com/viswa/assets/upload/version/", String.valueOf(mediaStorage), "viswa.apk"));
                    }

                    break;

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public void swiftFragments(ParentFragment frag, String tag) {
        try {
        FragmentTransaction trans = manager.beginTransaction();

        tempFrag = (ParentFragment) manager.findFragmentByTag(tag);
        if (tempFrag != null) {

            if (tempFrag.isAdded() && tempFrag.isVisible())
                return;
            else if (tempFrag.isAdded() && tempFrag.isHidden()) {
               // if (fragStack.size() > 0) {
                    trans.hide(fragStack.get(fragStack.size() - 1));
                    trans.show(tempFrag);

                    fragStack.remove(tempFrag);
                    fragStack.add(tempFrag);
               // }

            }

        } else if (!frag.isAdded()) {

            if (fragStack.size() > 0) {
                ParentFragment pf = fragStack.get(fragStack.size() - 1);
                trans.hide(pf);
            }

            trans.add(R.id.container, frag, tag);
            trans.show(frag);

            fragStack.push(frag);
        }



            if (SriVishwaApp.isInterestingActivityVisible()) {
                trans.commit();
            } else {
                trans.commitAllowingStateLoss();
            }

        } catch (IllegalStateException e) {
            TraceUtils.logException(e);
        }

    }

    public void removeAllFragments() {

        FragmentTransaction trans = manager.beginTransaction();

        for (int i = 0; i < manager.getFragments().size(); i++) {

            trans.remove(manager.getFragments().get(i));

        }

        trans.commitAllowingStateLoss();

        trans = null;

    }

    public void showInstructionsScreen(JSONObject jsonObject, boolean showProceed) {

        swiftFragments(Instructions.newInstance(jsonObject.toString(), showProceed), "instructions");

    }

    public void showAllQuestions() {

        swiftFragments(AllQuestions.newInstance(), "allquestions");

    }

    public void showExamSubmitConfirmationPage(JSONObject jsonObject, long student_exam_result_id, int type) {

        swiftFragments(ExamSubmitConfirmationPage.newInstance(jsonObject.toString(), student_exam_result_id, type), "examsubmitconfirmationpage");

    }

    public void showResults(String examDetails, long student_exam_result_id, int type) {

        swiftFragments(ResultsPage.newInstance(examDetails, student_exam_result_id, type), "results");

    }

    public void showSubjects() {

        swiftFragments(Subjects.newInstance(), "subjects");

    }

    public void showLessons(String data) {

        swiftFragments(Lessons.newInstance(data), "lessons");

    }

    public void showTopics(String data) {

        swiftFragments(Topics.newInstance(data), "topics");

    }

    public void showMaterials(String data) {

        swiftFragments(Materials.newInstance(data), "materials");

    }

    public void showExamTemplate(String data) {

        try {

            JSONObject jsonObject = new JSONObject(data);

            if (jsonObject.optString("course").equalsIgnoreCase("1")) { //JEE
              //  swiftFragments(ExamTemplates.newInstance(data), "examtemplate");
                swiftFragments(JEEemplates.newInstance(data), "examtemplate");

            } else if (jsonObject.optString("course").equalsIgnoreCase("3")) { //BITSAT
                // swiftFragments(ExamTemplates.newInstance(data), "examtemplate");
                //localPath = "file:///android_asset/BITSAT/bitsat0.html";
                swiftFragments(BITSATTemplates.newInstance(data), "examtemplate");

            } else if (jsonObject.optString("course").equalsIgnoreCase("4") || jsonObject.optString("course").equalsIgnoreCase("8")) { //EAMCET
                swiftFragments(ExamTemplates.newInstance(data), "examtemplate");
                //localPath = "file:///android_asset/EAMCET/eamcet.html";

            } else if (jsonObject.optString("course").equalsIgnoreCase("5")) { //NEET

                swiftFragments(NEETemplates.newInstance(data), "examtemplate");

            } else if (jsonObject.optString("course").equalsIgnoreCase("6")) { //AIIMS
                swiftFragments(AIIMSemplates.newInstance(data), "examtemplate");
                //localPath = "file:///android_asset/AIIMS/aiims.html";

            } else if (jsonObject.optString("course").equalsIgnoreCase("7")) { //JIPMER
                swiftFragments(JIPMERSemplates.newInstance(data), "examtemplate");
                //localPath = "file:///android_asset/JIPMER/jipmer.html";

            } else if (jsonObject.optString("course").equalsIgnoreCase("9")) { //KVPY
                swiftFragments(KVPYTemplates.newInstance(data), "examtemplate");
                //localPath = "file:///android_asset/KVPY/kvpy.html";

            } else if (jsonObject.optString("course").equalsIgnoreCase("2")) {
                swiftFragments(JEEAdvanceTemplates.newInstance(data), "examtemplate");
            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public void showAssignmentList() {

        swiftFragments(AssignmentList.newInstance(), "assignmentlist");

    }

    public void showAssignment(String data) {

        swiftFragments(Assignment.newInstance(data), "assignment");

    }

    public void showAssignmentResult(String id, String subject) {
        App_Table table = new App_Table(this);
        Object results = table.getAssignmentHistoryResult(id);
        if (results != null) {
            swiftFragments(AssignResultsPage.newInstance(results.toString()), "assignment");
        } else {
            Toast.makeText(this, "Please Start Your Assignment", Toast.LENGTH_SHORT).show();
        }
    }

    private void showHistoryList() {

        try {
            App_Table table = new App_Table(this);
            Object results = table.getResultFiles();
            if (results != null) {
                JSONObject object = new JSONObject(results.toString());
                JSONArray array = object.getJSONArray("files_body");
                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obb = array.getJSONObject(i);
                        startUploadBackUp(obb.optString("path"), obb.optString("filename"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        swiftFragments(AssignmentHistory.newInstance(), "assignmenthistory");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {

            if (keyCode == 4) {

                if (fragStack.size() > 1) {

                    ParentFragment pf = fragStack.peek();

                    if (pf instanceof Dashboard) {
                        showExitDialog();
                        return true;
                    }  if (pf instanceof JEEemplates) {
                        return true;
                    }

                    if (pf instanceof NEETemplates) {
                        return true;
                    }

                    if (pf instanceof JIPMERSemplates) {
                        return true;
                    }

                    if (pf instanceof AIIMSemplates) {
                        return true;
                    }

                    if (pf instanceof KVPYTemplates) {
                        return true;
                    }

                    if (pf instanceof ExamTemplates) {
                        return true;
                    }
                    if (pf instanceof JEEAdvanceTemplates) {
                        return true;
                    }

                    if (pf instanceof Assignment) {
                       // showExitDialog();
                        return true;
                    }

                    if (pf instanceof ExamSubmitConfirmationPage) {
                        //showExitDialog();
                        return true;
                    }

                    if (pf instanceof AssignResultsPage) {
                        //showExitDialog();
                        return true;
                    }

                    if (pf instanceof ResultsPage) {

                        //showExitDialog();
                        return true;

                    }

                    /*if(pf instanceof ChangePassword)
                    {
                        startActivity(new Intent(SriVishwa.this, SriVishwa.class));
                    }

                    if(pf instanceof Subjects)
                    {
                        startActivity(new Intent(SriVishwa.this, SriVishwa.class));
                    }*/

                    if (pf.back())
                        return true;

                    fragStack.pop();

                    FragmentTransaction trans = manager.beginTransaction();
                    trans.remove(pf);

                    if (fragStack.size() > 0) {
                        ParentFragment pf1 = fragStack.get(fragStack.size() - 1);
                        trans.show(pf1);
                    } /*else {
                        swiftHomeFragments(null, fragHomeStack.peek());
                    }*/

                    trans.commit();

                    return true;
                }

                showExitDialog();
                return false;

            }
           /* if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SriVishwa.this);
                builder.setMessage(Html.fromHtml("<b>WARNING:<b>") + "Malicious Activity Detected - Please Dont Repeat The Action Again - Otherwise Your App Will Be Blocked Permanently\n.You left with\t" + count + "\tattempts");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return true;
            }*/
        } catch (Exception e) {
            TraceUtils.logException(e);
        }

        return super.onKeyDown(keyCode, event);
    }



    public void showExitDialog() {


        ExitDialog.newInstance().show(getSupportFragmentManager(), "dialog");

    }

    public void showToast(int text) {

        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        try {

            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.WHITE);

        } catch (Exception e) {

            TraceUtils.logException(e);

        } finally {

            toast.show();

        }

    }

    public void showokPopUp(int drawableId, String title, String message) {

        try {

            Bundle bundle = new Bundle();

            bundle.putString("title", title);

            bundle.putString("message", message);

            bundle.putInt("drawableId", drawableId);

            MessageDialog.newInstance(bundle).show(getSupportFragmentManager(), "dialog");

        } catch (Exception e) {

            TraceUtils.logException(e);

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

    public void showokPopUp(int drawableId, String title, String message, int requestId, String activateBut, String cancelBut, IDialogCallbacks callbacks) {

        try {

            Bundle bundle = new Bundle();

            bundle.putString("title", title);

            bundle.putString("message", message);

            bundle.putInt("requestId", requestId);

            bundle.putInt("drawableId", drawableId);

            bundle.putString("cancelBut", cancelBut);

            bundle.putString("activateBut", activateBut);

            bundle.putBoolean("showcancel", true);

            MessageDialog messages = MessageDialog.newInstance(bundle);

            messages.setIDialogListener(callbacks);

            messages.show(this.getSupportFragmentManager(), "dialog");

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public void showokPopUp(int drawableId, String title, String message, int requestId, String activateBut, IDialogCallbacks callbacks, boolean showCancel) {

        try {

            Bundle bundle = new Bundle();

            bundle.putString("title", title);

            bundle.putString("message", message);

            bundle.putInt("requestId", requestId);

            bundle.putInt("drawableId", drawableId);

            bundle.putBoolean("showcancel", showCancel);

            bundle.putString("activateBut", activateBut);

            MessageDialog messages = MessageDialog.newInstance(bundle);

            messages.setIDialogListener(callbacks);

            messages.show(this.getSupportFragmentManager(), "dialog");

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public int checkPermission(String permissions, int code) {

        if (ActivityCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED) {
            return 1;
        }

        if (ActivityCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissions)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{permissions},
                        code);

                return 0;

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{permissions},
                        code);

                return 0;
            }
        }
        return -1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case 100:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    swiftFragments(ExamList.newInstance(), "examlist");

                } else {

                    showToast(R.string.pd);

                }

                break;

            case 200:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fragStack.peek().onRequestPermissionsResult(requestCode, true);

                } else {

                    showToast(R.string.pd);

                }

                break;

            case 300:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showWiFiSettings();

                } else {

                    showToast(R.string.pd);

                }

                break;
            case 400:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    File mediaStorage = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    if (!mediaStorage.exists()) {
                        mediaStorage.mkdirs();
                    }
                    Utils.downloadDilog(this);
                   // https://bsecuresoftechsolutions.com/viswa_dev/assets/upload/version/
                    startService(ApkFileDownloader.getDownloadService(this, "https://bsecuresoftechsolutions.com/viswa/assets/upload/version/", String.valueOf(mediaStorage), "viswa.apk"));

                } else {

                    showToast(R.string.pd);

                }

                break;


        }
    }

    public void setAllQuestions(JSONArray aAllQuestions) {

        allQuestions = aAllQuestions;

    }

    public void allQuestions_view(JSONArray aAllQuestions) {

        swiftFragments(AllQuestions_New.newInstance(aAllQuestions.toString()), "allquestins");

    }

    public JSONArray getAllQuestions() {

        return allQuestions;

    }

    private void updateToken() {

        try {

            String token = AppPreferences.getInstance(this).getFromStore("token");

            if (token.trim().length() == 0)
                return;

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("student_id", studentDetails.optString("student_id"));

            jsonObject.put("regidand", AppPreferences.getInstance(this).getFromStore("token"));

            HTTPPostTask post = new HTTPPostTask(this, this);

            post.disableProgress();

            post.userRequest(getString(R.string.plwait), 1, "updatedevice", jsonObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void checkAppUpdate() {

        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("version", applicationVName());

            HTTPPostTask post = new HTTPPostTask(this, this);

            post.disableProgress();

            post.userRequest(getString(R.string.plwait), 21, "checkversion", jsonObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public void getNewQuestions() {

        try {
            myData="";
            note.setVisibility(View.VISIBLE);
            JSONObject jsonObject = new JSONObject();


            jsonObject.put("timestamp", sp.getString("time", ""));

           // jsonObject.put("pageno", pageCount);

            HTTPPostTask post = new HTTPPostTask(this, this);

            post.disableProgress();
            // get_new_questions

            post.userRequest(getString(R.string.plwait), 123, "get_new_questions", jsonObject.toString());


        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onFinish(Object results, int requestId) {

        try {

            switch (requestId) {

                case 191:
                    JSONObject jsonObject11 = new JSONObject(results.toString());

                    if (jsonObject11.optString("statuscode").equalsIgnoreCase("200")) {

                        if (checkPermission("android.permission.READ_EXTERNAL_STORAGE", 100) == 1) {
                            swiftFragments(ExamList.newInstance(), "examlist");

                        }

                    }else{
                        Toast.makeText(this, jsonObject11.optString("statusdescription"), Toast.LENGTH_SHORT).show();
//                        deleteDatabase("exam_sri.db");
                        AppPreferences.getInstance(this).clearSharedPreferences(true);
                        Intent login=new Intent(this,LoginActivity.class);
                        startActivity(login);
                    }
                    break;
                case 1:
                    JSONObject jsonObject = new JSONObject(results.toString());

                    if (jsonObject.optString("statuscode").equalsIgnoreCase("200")) {


                        //Nothing  to do here

                    }
                    break;
                case 21:
                    JSONObject jsonObject1 = new JSONObject(results.toString());

                    if (jsonObject1.optString("statuscode").equalsIgnoreCase("200")) {

                        if (jsonObject1.has("new_version")) {

                            String appServerVersion = "1.0.0";

                            String temp = jsonObject1.optString("new_version").trim();

                            if (temp.length() > 0)
                                appServerVersion = temp;

                            String appVersion = applicationVName();

                            int diff = compareVersions(appServerVersion, appVersion);

                            if (diff == 1) {
                              //  showUpdateDialog(jsonObject1.optString("mandatory").equalsIgnoreCase("true") ? "N" : "Y");

                               /* Intent updates=new Intent(this,UpdateScreen.class);
                                startActivity(updates);*/

                                AppPreferences.getInstance(SriVishwa.this).addToStore("exam_on","1",false);
                                if (checkPermission("android.permission.READ_EXTERNAL_STORAGE", 400) == 1) {

                                    File mediaStorage = new File(Environment.getExternalStorageDirectory()
                                            .toString());
                                    if (!mediaStorage.exists()) {
                                        mediaStorage.mkdirs();
                                    }
                                    //Utils.downloadDilog(this);
                                    Toast.makeText(this, "Downloading updates.Please don't Refresh the page or navigate to other pages until download is complete.", Toast.LENGTH_LONG).show();
                                    downloadnote.setVisibility(View.VISIBLE);
                                    startService(ApkFileDownloader.getDownloadService(this, AppSettings.getInstance().getPropertyValue("download_apk"), String.valueOf(mediaStorage), "viswa.apk"));

                                    downloadnote.setVisibility(View.GONE);
                                }
                               // swiftFragments(UpdateScreen.newInstance(), "Update");
                            }
                           // getNewQuestions();


                        }

                    } else {
                        //getNewQuestions();
                        Toast.makeText(this, "You Already Have The Latest Version" , Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 123:

                    /*SharedPreferences.Editor editor = sp.edit();
                    editor.putString("time", String.valueOf(System.currentTimeMillis()));
                    editor.apply();*/

                    final JSONObject jsonObject2 = new JSONObject(results.toString());

                    if (jsonObject2.optString("statuscode").equalsIgnoreCase("200")) {

                        count_page = jsonObject2.optInt("total_pages"); //count==9

                        JSONArray array = jsonObject2.getJSONArray("question_details");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            final String filename = obj.optString("file_name");
                            filename_zip = obj.optString("zip_folder");
                           // final  String paragraph_file = obj.optString("paragraph_file");


                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    questionFile(filename);
                                   // questionFile(paragraph_file);
                                    getZipFolder(filename_zip);
                                }
                            }).start();
                        }
                       // sendToEncrypt(Environment.getExternalStorageDirectory() + "/allimages/allimages");
                      /*  if (jsonObject2.has("question_images")) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONArray array_img = jsonObject2.getJSONArray("question_images");
                                        for (int i = 0; i < array_img.length(); i++) {

                                            Log.e("file_name", array_img.getString(i) + "" + i);
                                            imageDownloadAdmin(AppSettings.getInstance().getPropertyValue("download_img") + array_img.getString(i), array_img.getString(i), i);

                                        }
                                        pageCount++;
                                        if (count_page <= pageCount) {
                                            getNewQuestions();
                                        } else {
                                            //sendToEncrypt(PATH);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        }*/

                      String timestamp = jsonObject2.optString("timestamp");
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("time", timestamp);
                        editor.apply();


                    } else {
                        sendToEncrypt(Environment.getExternalStorageDirectory() +"/allimages/allimages");
                        note.setVisibility(View.GONE);
                        Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
                       // sendToEncrypt(PATH);
                    }
                    break;
                case 100:
                    JSONObject jsonObject4 = new JSONObject(results.toString());
                    if (jsonObject4.optString("statuscode").equalsIgnoreCase("200")) {

                        updateUser(studentDetails.optString("username"), "1");

                        Toast.makeText(this, jsonObject4.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

           /* if (requestId == 1) {

                JSONObject jsonObject = new JSONObject(results.toString());

                if (jsonObject.optString("statuscode").equalsIgnoreCase("200")) {


                    //Nothing  to do here

                }

            } else if (requestId == 21) {

                JSONObject jsonObject = new JSONObject(results.toString());

                if (jsonObject.optString("statuscode").equalsIgnoreCase("200")) {

                    if (jsonObject.has("new_version")) {

                        String appServerVersion = "1.0.0";

                        String temp = jsonObject.optString("new_version").trim();

                        if (temp.length() > 0)
                            appServerVersion = temp;

                        String appVersion = applicationVName();

                        int diff = compareVersions(appServerVersion, appVersion);

                        if (diff == 1) {
                            showUpdateDialog(jsonObject.optString("mandatory").equalsIgnoreCase("true") ? "N" : "Y");

                        }

                    }

                } else {
                    getNewQuestions();
                }

            } else if (requestId == 123) {

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("time", String.valueOf(System.currentTimeMillis()));
                editor.apply();

                JSONObject jsonObject = new JSONObject(results.toString());

                if (jsonObject.optString("statuscode").equalsIgnoreCase("200")) {

                    JSONArray array = jsonObject.getJSONArray("question_details");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String filename = obj.optString("file_name");
                        questionFile(filename);
                    }
                    if (jsonObject.has("question_images")) {
                        JSONArray array_img = jsonObject.getJSONArray("question_images");
                        for (int i = 0; i < array_img.length(); i++) {

                            imageDownloadAdmin(AppSettings.getInstance().getPropertyValue("download_img") + array_img.getString(i), array_img.getString(i));

                        }
                    }
                }
            } else if (requestId == 100) {
                JSONObject jsonObject = new JSONObject(results.toString());
                if (jsonObject.optString("statuscode").equalsIgnoreCase("200")) {

                    updateUser(studentDetails.optString("username"), "1");

                    Toast.makeText(this, jsonObject.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                }
            }*/

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }


    @Override
    public void onError(String errorCode, int requestId) {

        Toast.makeText(this, errorCode, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

    }

    public String applicationVName() {

        String versionName = "";

        PackageManager manager = getPackageManager();
        try {

            PackageInfo info = manager.getPackageInfo(getPackageName(),
                    0);
            versionName = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {

            TraceUtils.logException(e);

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return versionName;

    }

    public void showUpdateDialog(String showskip) {

        Bundle bundle = new Bundle();
        bundle.putString("showskip", showskip);

        AppUpdateDialog updateDialog = AppUpdateDialog.newInstance(bundle);
        updateDialog.setIUpdateCallback(this);
        updateDialog.show(getSupportFragmentManager(), "dialog");

    }

    @Override
    public void onUserAction(String showskip, int id) {

        switch (id) {

            case R.id.tv_updatecancel:

                if (showskip.equalsIgnoreCase("y")) {

                    return;

                }

                closeActivity();

                break;

            case R.id.tv_updateok:
                if (checkPermission("android.permission.READ_EXTERNAL_STORAGE", 400) == 1) {
                    File mediaStorage = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    if (!mediaStorage.exists()) {
                        mediaStorage.mkdirs();
                    }
                    Utils.downloadDilog(this);
                    startService(ApkFileDownloader.getDownloadService(this, AppSettings.getInstance().getPropertyValue("download_apk"), String.valueOf(mediaStorage), "viswa.apk"));
                }

                break;

        }

    }

    public void closeActivity() {

        SriVishwa.this.finish();

    } public void closeFrags() {

        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                |Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                |Intent.FLAG_ACTIVITY_SINGLE_TOP
                |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);

    }

    private int compareVersions(String ver1, String ver2) {

        String[] vals1 = ver1.split("\\.");

        String[] vals2 = ver2.split("\\.");

        int i = 0;

        while (i < vals1.length && i < vals2.length
                && vals1[i].equals(vals2[i])) {

            i++;

        }

        if (i < vals1.length && i < vals2.length) {

            int diff = Integer.valueOf(vals1[i]).compareTo(
                    Integer.valueOf(vals2[i]));

            return diff < 0 ? -1 : diff == 0 ? 0 : 1;

        }

        return vals1.length < vals2.length ? -1
                : vals1.length == vals2.length ? 0 : 1;

    }

    private BroadcastReceiver mBroadcastReceiverAttach = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("com.attach.apk")) {
                    String a_name = intent.getStringExtra("attachname");
                    String type = Utils.getMimeType(a_name);
                    if (type.startsWith("application/vnd.android.package-archive")) {
                        stopLockTask();
                        deleteDatabase("exam_sri.db");
                        stopService(new Intent(getApplicationContext(), DNotifyCloser.class));
                        AppPreferences.getInstance(SriVishwa.this).clearSharedPreferences(true);
                      //  AppPreferences.getInstance(SriVishwa.this).addToStore("inFlag","Yes",false);
                        Utils.dismissProgress();
                        String path = Environment.getExternalStorageDirectory()
                                .toString() + "/" + a_name.trim();

                       // triggerApk(a_name);
                        Uri paths = Uri.fromFile(new File(path));
                        downloadnote.setVisibility(View.GONE);
                        Intent intent_n = new Intent(Intent.ACTION_VIEW);
                     //   intent_n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent_n.setDataAndType(paths, type);
                        startActivity(intent_n);
                        apkDelte(path);

                       // SriVishwa.this.finish();
                    }
                    if (type.startsWith("text/plain")) {
                        String path = Environment.getExternalStorageDirectory()
                                .toString() + "/" + a_name.trim();
                        try {
                            FileInputStream fis = new FileInputStream(path);
                            DataInputStream in = new DataInputStream(fis);
                            BufferedReader br =
                                    new BufferedReader(new InputStreamReader(in));
                            String strLine;
                            while ((strLine = br.readLine()) != null) {
                                myData = myData + strLine;
                            }
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                        if (myData.length() > 0) {
                            new File(path).delete();
                            try {
                                JSONArray jsonArray = new JSONArray(myData.trim());
                                if (jsonArray.length()>0) {
                                    app_table.insertMultipleRecords(jsonArray, "QUESTIONS");
                                    Log.e("questions_server","Inserted");
                                }

                               /* runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (jsonArray.length() > 0) {
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    JSONObject oo = jsonArray.getJSONObject(i);
                                                    String d_urls[] = {
                                                            AppSettings.getInstance().getPropertyValue("download_img") + oo.optString("question_name"),
                                                            AppSettings.getInstance().getPropertyValue("download_img") + oo.optString("option_a"),
                                                            AppSettings.getInstance().getPropertyValue("download_img") + oo.optString("option_b"),
                                                            AppSettings.getInstance().getPropertyValue("download_img") + oo.optString("option_c"),
                                                            AppSettings.getInstance().getPropertyValue("download_img") + oo.optString("option_d"),
                                                    };
                                                    new DownloadFilesToMemory(d_urls, getApplicationContext()).execute();

                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });*/


                            } catch (Exception e) {
                                TraceUtils.logException(e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void triggerApk(String a_name) {
        Intent apkint=new Intent("come.main.apks");
        apkint.putExtra("apkname",a_name);
        sendBroadcast(apkint);
    }


    private void apkDelte(final String path) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                File apk_file = new File(path);
                if (apk_file.exists()) {
                    apk_file.delete();
                }

            }
        }, 6000000);

    }

    private BroadcastReceiver mBroadcastQuestion = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.file.data")) {

                String a_name = intent.getStringExtra("filename");
                questionFile(a_name);
            }
        }
    };

    private void questionFile(String a_name) {
        String externalDirectory = Environment.getExternalStorageDirectory().toString();
        File mediaStorage = new File(externalDirectory);
        if (!mediaStorage.exists()) {
            mediaStorage.mkdirs();
        }

        startService(ApkFileDownloader.getDownloadService(getApplicationContext(), AppSettings.getInstance().getPropertyValue("download_qs"), String.valueOf(mediaStorage), a_name));
    }

    @Override
    protected void onResume() {
        //getNewQuestions();
        registerReceiver(mBroadcastQuestion, new IntentFilter("com.file.data"));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiverAttach);
        unregisterReceiver(mBroadcastQuestion);
        super.onDestroy();
    }

    /* public boolean dispatchKeyEvent(KeyEvent keyEvent) {
     *//* if (this.blockedKeys.contains(Integer.valueOf(keyEvent.getKeyCode()))) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            return true;
        }*//*
        if (this.keys.contains(Integer.valueOf(keyEvent.getKeyCode()))) {
            // keyEvent.startTracking();
            count--;
            Toast.makeText(this, "you exceeded the limit " + count, Toast.LENGTH_SHORT).show();
            if (count <= 0) {
                if (!this.keys.contains(Integer.valueOf(4))) {
                    if (this.getApplicationContext() instanceof SriVishwa) {
                        blockUSer();
                        startActivity(new Intent(SriVishwa.this, LoginActivity.class));
                        AppPreferences.getInstance(this).addToStore("studentDetails", "", false);
                        SriVishwa.this.finish();
                        Toast.makeText(this, "you exceeded the limit.Your account is blocked", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            count = 5;
        }


        return super.dispatchKeyEvent(keyEvent);
    }*/

    private void blockUSer() {

        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("application_no", studentDetails.optString("application_no"));

            jsonObject.put("status", 1);

            HTTPPostTask post = new HTTPPostTask(this, this);

            post.userRequest(getString(R.string.plwait), 100, "set_student_status", jsonObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }
/*
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!z) {
            if (!this.keys.contains(Integer.valueOf(4))) {
                count--;
                Toast.makeText(this, "you exceeded the limit " + count, Toast.LENGTH_SHORT).show();
                if (count <= 0) {

                    if (this.getApplicationContext() instanceof SriVishwa) {

                        sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                        blockUSer();
                        startActivity(new Intent(SriVishwa.this, LoginActivity.class));
                        AppPreferences.getInstance(this).addToStore("studentDetails", "", false);
                        SriVishwa.this.finish();
                        Toast.makeText(this, "Malicious Activity Detected - Your User ID Is Blocked - Please Contact Administrator", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (this.keys.contains(Integer.valueOf(26)) || this.keys.contains(Integer.valueOf(25)) || this.keys.contains(Integer.valueOf(24))) {
                sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            } else {
                count = 5;
            }
        }
    }*/

    public boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo net = manager.getActiveNetworkInfo();
        if (net != null) {
            if (net.isConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    private void startUploadBackUp(String path, String file_name) {

        String url = AppSettings.getInstance().getPropertyValue("uploadfile_admin");

        FileUploader uploader = new FileUploader(this, this);

        uploader.setFileName(file_name, file_name);

        uploader.userRequest("", 11, url, path);
    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int reqID) {
        try {

            switch (what) {

                case -1: // failed

                    Toast.makeText(this, "Failed To Send", Toast.LENGTH_SHORT).show();

                    break;

                case 1: // progressBar

                    break;

                case 0: // success

                    JSONObject object = new JSONObject(obj.toString());
                    dataSendServer(object.optString("file_name"));
                    break;

                default:
                    break;
            }

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
        }

    }

    private void dataSendServer(String file_name) {

        try {

            App_Table table = new App_Table(this);

            String exam_id = table.getFileName(file_name);

            table.deleteRecord("exam_id='" + exam_id + "'", "FILESDATA");

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("exam_id", exam_id);

            jsonObject.put("student_id", studentDetails.getString("student_id"));

            jsonObject.put("file_name", file_name);

            HTTPPostTask post = new HTTPPostTask(this, this);

            post.userRequest(getString(R.string.plwait), 111, "submit_exam_result", jsonObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    private void updateUser(String username, String status) {

        SQLiteDatabase db = null;
        Database database = new Database(this);
        try {
            if (database != null) {
                db = database.getWritableDatabase();

                String iwhereClause = "username='" + username + "'";
                ContentValues cv = new ContentValues();
                cv.put("status", status);
                db.update("STUDENTS", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /*private void imageDownloadAdmin(final String path, final String file_name, int count) {

        Log.e("file_name", file_name + "--" + count);
        // new DownloadFilesToMemory(path, getApplicationContext(),file_name).execute();


        File kp = new File(PATH);
        if (!kp.exists()) {
            kp.mkdir();
        }
        if (Status.RUNNING == PRDownloader.getStatus(downloadID)) {
            PRDownloader.pause(downloadID);
            return;
        }

        if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
            PRDownloader.resume(downloadID);
            return;
        }
        downloadID = PRDownloader.download(path, PATH, file_name)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        // app_table.insertImageFile(file_name, "IMAGESQA","0");
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        //  app_table.insertImageFile(file_name, "IMAGESQA","0");
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        downloadID = 0;

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        app_table.insertImageFile(file_name, "IMAGESQA", "1");
                    }

                    @Override
                    public void onError(Error error) {
                        app_table.insertImageFile(file_name, "IMAGESQA", "0");

                    }
                });

    }*/

    private void sendToEncrypt(final String path) {
        try {

            ImageProcesser imageProcesser = new ImageProcesser(this, new IItemHandler() {

                @Override
                public void onFinish(Object results, int requestId) {

                    try {
                        Log.i("enc","completed");
                        File dir = new File(Environment.getExternalStorageDirectory() + "/allimages/allimages");
                        if (dir.isDirectory()) {
                            String[] children = dir.list();
                            if (children.length > 0) {
                                for (int i = 0; i < children.length; i++) {
                                    new File(dir, children[i]).delete();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String errorCode, int requestId) {
                    Toast.makeText(SriVishwa.this, errorCode, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onProgressChange(int requestId, Long... values) {

                }

            });


            imageProcesser.startProcess(1, path);
        } catch (Exception e) {

            TraceUtils.logException(e);

        }
    }

    private void getZipFolder(final String filename_zip) {
        String path_url=AppSettings.getInstance().getPropertyValue("zip_download")+filename_zip+".zip";
        File kps= new File(Environment.getExternalStorageDirectory() + "/"+filename_zip);
        if (!kps.exists()) {
            kps.mkdir();
        }
        DownloadFileAsync download = new DownloadFileAsync(Environment.getExternalStorageDirectory() + "/"+filename_zip+".zip", this, new DownloadFileAsync.PostDownload(){
            @Override
            public void downloadDone(File file) {
                Log.i("ZIP", "file download completed");

                ZipArchive zipArchive = new ZipArchive();
                zipArchive.unzip(Environment.getExternalStorageDirectory() + "/"+filename_zip+".zip",Environment.getExternalStorageDirectory() + "/allimages","");
                sendToEncrypt(Environment.getExternalStorageDirectory() + "/allimages/allimages");
                Log.i("ZIP", "file unzip completed");
                note.setVisibility(View.GONE);
                Toast.makeText(SriVishwa.this, "Download Complete", Toast.LENGTH_SHORT).show();
            }
        });
        download.execute(path_url);
    }
   /* @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            String exam_process= AppPreferences.getInstance(this).getFromStore("exam_on");
            if (exam_process.startsWith("0")) {
                sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            }
        }else{
            String exam_process= AppPreferences.getInstance(this).getFromStore("exam_on");
            if (exam_process.startsWith("0")) {
                sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            }
        }
    }*/
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

}
