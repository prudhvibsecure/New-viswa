package com.adi.exam.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.AssignmentListingAdapter;
import com.adi.exam.adapters.ExamContentListingAdapter;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.common.AppSettings;
import com.adi.exam.database.App_Table;
import com.adi.exam.database.Database;
import com.adi.exam.database.PhoneComponent;
import com.adi.exam.services.DownloadFileAsync;
import com.adi.exam.tasks.HTTPPostTask;
import com.adi.exam.tasks.ImageProcesser;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ir.mahdi.mzip.zip.ZipArchive;

public class AssignmentList extends ParentFragment implements View.OnClickListener, IItemHandler , AssignmentListingAdapter.AssignmentListListener {

    //TODO: error handling -> make use of tv_content_txt

    private OnFragmentInteractionListener mFragListener;

    private ProgressBar progressBar;

    private TextView tv_content_txt;

    private SriVishwa activity;

    private AssignmentListingAdapter adapterContent;

    private int iCounter = 0;

    private long left_over_time=0;

    private SwipeRefreshLayout mSwipeRefreshLayout = null;

    public AssignmentList() {
        // Required empty public constructor
    }

    public static AssignmentList newInstance() {
        return new AssignmentList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_examlist, container, false);

        progressBar = layout.findViewById(R.id.pb_content_bar);

        tv_content_txt = layout.findViewById(R.id.tv_content_txt);

        tv_content_txt.setText(R.string.cydhaen);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        RecyclerView rv_content_list = layout.findViewById(R.id.rv_content_list);

        rv_content_list.setLayoutManager(layoutManager);

        rv_content_list.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration did = new DividerItemDecoration(rv_content_list.getContext(), layoutManager.getOrientation());

        rv_content_list.addItemDecoration(did);

        adapterContent = new AssignmentListingAdapter(activity,this);

        adapterContent.setOnClickListener(this);

        rv_content_list.setAdapter(adapterContent);

        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryDark,
                R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                checkAssignment();
            }
        });

        if (isNetworkAvailable()) {

            JSONArray jsonArray = (JSONArray) getAssaignments();

            if (jsonArray.length() > 0) {

                adapterContent.setItems(jsonArray);

                adapterContent.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                //updateOtherDetails(adapterContent.getItems());

            }

            checkAssignment();
        }else{
            progressBar.setVisibility(View.GONE);

            JSONArray jsonArray = (JSONArray) getAssaignments();

            if (jsonArray.length() > 0) {

                adapterContent.setItems(jsonArray);

                adapterContent.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                //updateOtherDetails(adapterContent.getItems());

            }
            else
            {
                Toast.makeText(activity, "No Data Found", Toast.LENGTH_SHORT).show();
            }
            //PhoneComponent phncomp = new PhoneComponent(this, activity, 3);
            //phncomp.executeLocalDBInBackground("ASSIGNMENT");
        }

        return layout;
    }
    public boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) getActivity()
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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mFragListener = (OnFragmentInteractionListener) context;

        activity = (SriVishwa) context;

    }

    @Override
    public void onResume() {
        try {
            //activity.getNewQuestions();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        mFragListener.onFragmentInteraction(R.string.exam, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragListener.onFragmentInteraction(R.string.exam, false);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragListener = null;
    }

    @Override
    public void onClick(View view) {

        try {

            switch (view.getId()) {

                case R.id.tv_startexam:

                    JSONObject jsonObject1 = adapterContent.getItems().getJSONObject((int) view.getTag());
                    //write here also diff
                    App_Table table = new App_Table(activity);
                    String iwhereClause = "assignment_id = '" + jsonObject1.optString("assignment_id") + "'";

                    boolean isRecordExits = table.isRecordExits(iwhereClause, "ASSIGNMENTRESULTS");

                    if (isRecordExits) {

                        activity.showokPopUp(R.drawable.pop_ic_failed, "", activity.getString(R.string.yhadwte));

                        return;

                    }
                    //  JSONObject question_details = jsonObject1.getJSONObject("question_details");

                    if (jsonObject1.optString("down_status").equalsIgnoreCase("0")){

                        final String zip_file_name= jsonObject1.optString("zip_file_name");

                        getZipFolderFile(zip_file_name,jsonObject1.optString("assignment_id"));

                    }else {

                   /* JSONObject question_details = jsonObject1.getJSONObject("question_details");
                    String timestamp = new SimpleDateFormat("dd-MM-yyyy ")
                            .format(new Date()) // get the current date as String
                            .concat(question_details.optString("from_time").trim()
                            );
                    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                    Date date1 = (Date) formatter.parse(timestamp);*/

                        //JSONObject question_details = jsonObject1.getJSONObject("question_details");
                        String timestamp = new SimpleDateFormat("dd-MM-yyyy ")
                                .format(new Date()) // get the current date as String
                                .concat(jsonObject1.optString("from_time").trim()
                                );
                        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                        Date date1 = (Date) formatter.parse(timestamp);

                        long duration_secs = jsonObject1.optLong("duration_sec");
                        long current_time = System.currentTimeMillis();//current time
                        long from_time = date1.getTime();// from time

                        if (from_time < current_time) {
                            long left_time = current_time - from_time;
                            left_over_time = duration_secs - (left_time / 1000);

                        }
                        jsonObject1.put("duration_sec", left_over_time);

                        // JSONObject question_details = jsonObject1.getJSONObject("question_details");

                   /* String dateTime = jsonObject1.optString("exam_date").trim() + " " + jsonObject1.optString("from_time").trim();

                    Date examDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(dateTime);

                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);

                    String formattedDate1 = df1.format(c.getTime());

                    Date currentDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(formattedDate1);

                    long[] diff = getDifference(currentDateTime, examDateTime);

                    if (diff[0] > 0) {

                        activity.showokPopUp(R.drawable.pop_ic_info, "", activity.getString(R.string.indays, diff[0] + ""));

                        return;

                    }

                    if (diff[1] > 0) {

                        activity.showokPopUp(R.drawable.pop_ic_info, "", activity.getString(R.string.inhours, diff[1] + "", diff[2] + "", diff[3] + ""));

                        return;

                    }

                    if (diff[2] > 0) {

                        activity.showokPopUp(R.drawable.pop_ic_info, "", activity.getString(R.string.inmins, diff[2] + "", diff[3] + ""));

                        return;

                    }

                    if (diff[3] > 0) {

                        activity.showokPopUp(R.drawable.pop_ic_info, "", activity.getString(R.string.insecs, diff[3] + ""));

                        return;

                    }

                    if (diff[0] < 0) {

                        activity.showokPopUp(R.drawable.pop_ic_info, "", activity.getString(R.string.ethbf));

                        return;

                    }
                    String dateTime1 = jsonObject1.optString("exam_date").trim() + " " + jsonObject1.optString("to_time").trim();

                    Date examDateTime1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(dateTime1);

                    Calendar c1 = Calendar.getInstance();

                    SimpleDateFormat df11 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);

                    String formattedDate11 = df11.format(c1.getTime());

                    Date currentDateTime1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(formattedDate11);

                    long[] diff1 = getDifference(examDateTime1,currentDateTime1);//changed here diff
                    if (diff1[1] < 0) {

                        long hoursInSecs = diff1[1] * 60 * 60;

                        long minsInSecs = diff1[2] * 60;

                        long secInSecs = diff1[3];

                        long totalDelay = Math.abs(hoursInSecs) + Math.abs(minsInSecs) + Math.abs(secInSecs);

                        long duration_secs = jsonObject1.optLong("duration_sec");

                        long leftOverSeconds = duration_secs - totalDelay;

                        if (leftOverSeconds < 0) {

                            activity.showokPopUp(R.drawable.pop_ic_info, "", activity.getString(R.string.ethbf));

                            return;

                        } else {

                            jsonObject1.put("duration_sec", leftOverSeconds);

                        }

                    } else if (diff1[2] < 0) {

                        long minsInSecs = diff1[2] * 60;

                        long secInSecs = diff1[3];

                        long totalDelay = Math.abs(minsInSecs) + Math.abs(secInSecs);

                        long duration_secs = jsonObject1.optLong("duration_sec");

                        long leftOverSeconds = duration_secs - totalDelay;

                        if (leftOverSeconds < 0) {

                            activity.showokPopUp(R.drawable.pop_ic_info, "", activity.getString(R.string.ethbf));

                            return;

                        } else {

                            jsonObject1.put("duration_sec", leftOverSeconds);

                        }

                    } else if (diff1[3] < 0) {

                        long secInSecs = diff1[3];

                        long totalDelay = Math.abs(secInSecs);

                        long duration_secs = jsonObject1.optLong("duration_sec");

                        long leftOverSeconds = duration_secs - totalDelay;

                        if (leftOverSeconds < 0) {

                            activity.showokPopUp(R.drawable.pop_ic_info, "", activity.getString(R.string.ethbf));

                            return;

                        } else {

                            jsonObject1.put("duration_sec", leftOverSeconds);

                        }

                    }*/

                        activity.showAssignment(jsonObject1.toString());
                    }

                    break;

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void checkAssignment() {

        try {

            progressBar.setVisibility(View.VISIBLE);

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("course", activity.getStudentDetails().optString("program_name"));

            jsonObject.put("section", activity.getStudentDetails().optString("section"));

            HTTPPostTask post = new HTTPPostTask(activity, this);

            post.disableProgress();

            //TODO:////

            JSONObject object = new JSONObject(AppPreferences.getInstance(getActivity()).getFromStore("studentDetails"));

            JSONObject sobj = new JSONObject();

            sobj.put("course",object.optString("program_name"));

            sobj.put("section",object.optString("section"));

            post.userRequest(getString(R.string.plwait), 1, "checkassignment", sobj.toString() /*"{\"course\":\"JEE MAINS\",\"section\":\"201\"}"*//*jsonObject.toString()*/);


        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

    }

    @Override
    public void onFinish(Object results, int requestId) {

        try {

            if (requestId == 1) {

                JSONObject jsonObject = new JSONObject(results.toString());

                if (jsonObject.optString("statuscode").equalsIgnoreCase("200")) {
                    tv_content_txt.setVisibility(View.GONE);
                    if (jsonObject.has("assignment_ids")) {

                        String assignment_ids = jsonObject.optString("assignment_ids").trim();

                        if (assignment_ids.length() > 0) {

                            if (assignment_ids.contains(",")) {

                                String[] temp = assignment_ids.split(",");

                                for (int i = 0; i < temp.length; i++) {

                                    getAssignments(temp[i]);

                                }

                            } else {

                                getAssignments(assignment_ids);

                            }

                            return;

                        }else{
                            mSwipeRefreshLayout.setRefreshing(false);
                            mSwipeRefreshLayout.setEnabled(true);
                            tv_content_txt.setVisibility(View.VISIBLE);
                        }

                    }

                }else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setEnabled(true);
                    tv_content_txt.setVisibility(View.VISIBLE);
                }

                progressBar.setVisibility(View.GONE);

               // tv_content_txt.setVisibility(View.VISIBLE);

            } else if (requestId == 2) {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(true);

                JSONObject jsonObject = new JSONObject(results.toString());

                if (jsonObject.optString("statuscode").equalsIgnoreCase("200")) {

                    App_Table table = new App_Table(activity);

                    if (jsonObject.has("assignment_details")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("assignment_details");

                        progressBar.setVisibility(View.GONE);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String iwhereClause = "assignment_id = '" + jsonObject1.optString("assignment_id") + "'";
                            boolean isRecordAvailable = table.isRecordExits(iwhereClause, "ASSIGNMENT");
                            if (!isRecordAvailable) {
                                table.checkNInsertARecord(jsonObject1, "ASSIGNMENT", iwhereClause);
                            }

                        }

                        /*if (adapterContent.getItems().length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                adapterContent.getItems().put(jsonArray.getJSONObject(i));

                            }

                            adapterContent.notifyDataSetChanged();

                            return;

                        }

                        adapterContent.setItems(jsonArray);

                        adapterContent.notifyDataSetChanged();

*/
                      //  if (adapterContent.getItems().length() > 0) {
                            JSONArray jsonArray1 = getAssaignments();

                            if (jsonArray1.length() > 0) {

                                adapterContent.setItems(jsonArray1);

                                adapterContent.notifyDataSetChanged();

                                progressBar.setVisibility(View.GONE);

                                //updateOtherDetails(adapterContent.getItems());

                            }else{
                                mSwipeRefreshLayout.setRefreshing(false);
                                mSwipeRefreshLayout.setEnabled(true);
                                tv_content_txt.setVisibility(View.VISIBLE);
                            }
                       // }

                    }

                }
                else {
                    Toast.makeText(activity, "No Data Found", Toast.LENGTH_SHORT).show();
                }

            }else if (requestId==3){
                JSONArray jsonArray = (JSONArray) results;

                if (jsonArray.length() > 0) {

                    adapterContent.setItems(jsonArray);

                    adapterContent.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                  //  updateOtherDetails(adapterContent.getItems());

                }

            }else if (requestId==99){
                JSONObject jsonObject = new JSONObject(results.toString());
                App_Table table = new App_Table(activity);
                if (jsonObject.optString("statuscode").equalsIgnoreCase("200")){
                    if (jsonObject.has("question_details")) {
                        JSONArray question_details = jsonObject.getJSONArray("question_details");
                        table.insertMultipleRecords(question_details, "QUESTIONS");

                    }
                    table.updateDownloadStatus_qs1(jsonObject.optString("assignment_id"), "1","ASSIGNMENT");

                    checkAssignment();
                }

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onError(String errorCode, int requestId) {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(true);
    }

    private void updateOtherDetails(final JSONArray jsonArray) throws Exception {

        for (iCounter = 0; iCounter < jsonArray.length(); iCounter++) {

            final JSONObject jsonObject = jsonArray.getJSONObject(iCounter);

            String exam_id = jsonObject.optString("exam_id").trim();

            if (exam_id.length() > 0) {

                PhoneComponent phncomp = new PhoneComponent(new IItemHandler() {
                    @Override
                    public void onFinish(Object results, int requestId) {

                        try {

                            if (results != null) {

                                JSONArray jsonArray1 = (JSONArray) results;

                                adapterContent.getItems().getJSONObject(requestId).put("question_details", jsonArray1.getJSONObject(0));

                                adapterContent.notifyDataSetChanged();

                            }

                        } catch (Exception e) {

                            TraceUtils.logException(e);

                        }

                    }

                    @Override
                    public void onError(String errorCode, int requestId) {

                    }

                    @Override
                    public void onProgressChange(int requestId, Long... values) {

                    }

                }, activity, iCounter);

                phncomp.defineWhereClause("exam_id = '" + exam_id + "'");

                phncomp.executeLocalDBInBackground("QUESTIONPAPER");

            }

        }

    }

    private void getAssignments(String aAssignmentId) {

        try {

            JSONObject reqObject = new JSONObject();

            reqObject.put("student_id", activity.getStudentDetails().optString("student_id"));

            reqObject.put("assignment_id", aAssignmentId);

            HTTPPostTask post = new HTTPPostTask(activity, this);

            post.disableProgress();

            post.userRequest(getString(R.string.plwait), 2, "getassignment", reqObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

   /* private void showSubCategories(JSONObject jsonObject) {

        try {

            PhoneComponent phncomp = new PhoneComponent(this, activity, 4);

            phncomp.defineWhereClause("exam_id = '" + jsonObject.optString("exam_id") + "' ");

            phncomp.executeLocalDBInBackground("QUESTIONPAPER");


        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }*/

    private long[] getDifference(Date startDate, Date endDate) {
        //milliseconds

        long[] vals = new long[4];

        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;

        vals[0] = elapsedDays;

        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;

        vals[1] = elapsedHours;

        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;

        vals[2] = elapsedMinutes;

        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        vals[3] = elapsedSeconds;

       /* System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);*/

        return vals;

    }


    public JSONArray getAssaignments() {

        Database database = new Database(getActivity());

        SQLiteDatabase db;

        JSONArray array = new JSONArray();

        try {
            if (database != null) {

                //String cursor_q = "select * from EXAM";
                String cursor_q = "SELECT * from assignment order by from_time";
                db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            if(cursor.moveToFirst()) {
                                do {
                                    JSONObject obj = new JSONObject();

                                    obj.put("assignment_id", cursor.getString(cursor.getColumnIndex("assignment_id")));
                                    obj.put("assignment_name", cursor.getString(cursor.getColumnIndex("assignment_name")));
                                    obj.put("course", cursor.getString(cursor.getColumnIndex("course")));
                                    obj.put("course_name", cursor.getString(cursor.getColumnIndex("course_name")));
                                    obj.put("subject", cursor.getString(cursor.getColumnIndex("subject")));
                                    obj.put("section", cursor.getString(cursor.getColumnIndex("section")));
                                    obj.put("lessons", cursor.getString(cursor.getColumnIndex("lessons")));
                                    obj.put("topics", cursor.getString(cursor.getColumnIndex("topics")));
                                    obj.put("exam_date", cursor.getString(cursor.getColumnIndex("exam_date")));
                                    obj.put("from_time", cursor.getString(cursor.getColumnIndex("from_time")));
                                    obj.put("to_time", cursor.getString(cursor.getColumnIndex("to_time")));
                                    obj.put("duration", cursor.getString(cursor.getColumnIndex("duration")));
                                    obj.put("no_of_questions", cursor.getString(cursor.getColumnIndex("no_of_questions")));
                                    obj.put("marks_per_question", cursor.getString(cursor.getColumnIndex("marks_per_question")));
                                    obj.put("questions", cursor.getString(cursor.getColumnIndex("questions")));
                                    obj.put("exam_time", cursor.getString(cursor.getColumnIndex("exam_time")));
                                    obj.put("duration_sec", cursor.getString(cursor.getColumnIndex("duration_sec")));
                                    obj.put("zip_file_name", cursor.getString(cursor.getColumnIndex("zip_file_name")));
                                    obj.put("down_status", cursor.getString(cursor.getColumnIndex("down_status")));
                                    //obj.put("question_details", obj1);

                                    String dateTime = cursor.getString(cursor.getColumnIndex("exam_date")).trim() + " " + cursor.getString(cursor.getColumnIndex("to_time")).trim();

                                    Date examDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(dateTime);

                                    Calendar c = Calendar.getInstance();

                                    SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);

                                    String formattedDate1 = df1.format(c.getTime());

                                    Date currentDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(formattedDate1);

                                    long[] diff = getDifference(currentDateTime, examDateTime);

                                    String edate = cursor.getString(cursor.getColumnIndex("exam_date")).trim();
                                    String cdate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.getTime());
                                    Date exdate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(edate);
                                    Date cudate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(cdate);

                                    //array.put(obj);

                                    if(exdate.equals(cudate) || examDateTime.compareTo(currentDateTime)>0) {

                                      /* if(diff[1] > 0 || diff[2] > 0 || diff[3] > 0)
                                       {*/
                                        array.put(obj);
                                        //}
                                    }

                                } while(cursor.moveToNext());
                            }
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }
    private void getQuestionsZip(String qp_id) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("assignment_id", qp_id);

            HTTPPostTask post = new HTTPPostTask(activity, this);

            post.disableProgress();

            post.userRequest(getString(R.string.plwait), 99, "question_files", jsonObject.toString());

        }catch (Exception e){
            e.printStackTrace();}
    }
    private void getZipFolderFile(final String zip_file_name, final String qs_ids) {


        String path_url= AppSettings.getInstance().getPropertyValue("rtf_zip_download")+zip_file_name+".zip";
        File kps= new File(Environment.getExternalStorageDirectory() + "/"+zip_file_name);
        if (!kps.exists()) {
            kps.mkdir();
        }
        DownloadFileAsync download = new DownloadFileAsync(Environment.getExternalStorageDirectory() + "/"+zip_file_name+".zip", getActivity(), new DownloadFileAsync.PostDownload(){
            @Override
            public void downloadDone(File file) {
                Log.i("ZIP", "file download completed");

                ZipArchive zipArchive = new ZipArchive();
                zipArchive.unzip(Environment.getExternalStorageDirectory() + "/"+zip_file_name+".zip",Environment.getExternalStorageDirectory() + "/allFiles","");
                sendToEncrypt(Environment.getExternalStorageDirectory() + "/allFiles");
                Log.i("ZIP", "file unzip completed");
                // checkQuestionPaper();
                getQuestionsZip(qs_ids);
               // Toast.makeText(getActivity(), "Download Complete", Toast.LENGTH_SHORT).show();
            }
        });
        download.execute(path_url);
    }
    private void sendToEncrypt(final String path) {
        try {

            ImageProcesser imageProcesser = new ImageProcesser(getActivity(), new IItemHandler() {

                @Override
                public void onFinish(Object results, int requestId) {

                    try {
                        Log.i("enc","completed");
                        File dir = new File(Environment.getExternalStorageDirectory() + "/allFiles");
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
                    Toast.makeText(getActivity(), errorCode, Toast.LENGTH_LONG).show();
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

    @Override
    public void onRowClicked(JSONObject jsonObject, int position, TextView tv_processing, TextView tv_start) {
        try {


                JSONObject jsonObject1 = jsonObject;
                //write here also diff
                App_Table table = new App_Table(activity);
                String iwhereClause = "assignment_id = '" + jsonObject1.optString("assignment_id") + "'";

                boolean isRecordExits = table.isRecordExits(iwhereClause, "ASSIGNMENTRESULTS");

                if (isRecordExits) {

                    activity.showokPopUp(R.drawable.pop_ic_failed, "", activity.getString(R.string.yhadwte));

                    return;

                }
                //  JSONObject question_details = jsonObject1.getJSONObject("question_details");

                if (jsonObject1.optString("down_status").equalsIgnoreCase("0")) {
                    if (isNetworkAvailable()) {
                        tv_processing.setVisibility(View.VISIBLE);
                        tv_start.setVisibility(View.GONE);

                        final String zip_file_name = jsonObject1.optString("zip_file_name");

                        getZipFolderFile(zip_file_name, jsonObject1.optString("assignment_id"));
                    } else{
                    Toast.makeText(activity, "Please check your network", Toast.LENGTH_LONG).show();
                }

                } else {
                    tv_processing.setVisibility(View.GONE);
                    tv_start.setVisibility(View.VISIBLE);

                    String timestamp = new SimpleDateFormat("dd-MM-yyyy ")
                            .format(new Date()) // get the current date as String
                            .concat(jsonObject1.optString("from_time").trim()
                            );
                    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                    Date date1 = (Date) formatter.parse(timestamp);

                    long duration_secs = jsonObject1.optLong("duration_sec");
                    long current_time = System.currentTimeMillis();//current time
                    long from_time = date1.getTime();// from time

                    if (from_time < current_time) {
                        long left_time = current_time - from_time;
                        left_over_time = duration_secs - (left_time / 1000);

                    }
                    jsonObject1.put("duration_sec", left_over_time);

                    activity.showAssignment(jsonObject1.toString());
                }



        } catch (Exception e) {

            TraceUtils.logException(e);

        }
    }
}
