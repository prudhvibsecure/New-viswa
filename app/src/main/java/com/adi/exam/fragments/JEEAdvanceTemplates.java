package com.adi.exam.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.MainActivity;
import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.QuestionNumberListingAdapter;
import com.adi.exam.adapters.QuestionNumberListingAdapter_Em;
import com.adi.exam.callbacks.IFileUploadCallback;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AESEncryptionDecryption;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.common.AppSettings;
import com.adi.exam.controls.CustomCheckBox;
import com.adi.exam.controls.CustomEditText;
import com.adi.exam.database.App_Table;
import com.adi.exam.database.Database;
import com.adi.exam.database.PhoneComponent;
import com.adi.exam.tasks.FileUploader;
import com.adi.exam.tasks.HTTPPostTask;
import com.adi.exam.utils.TraceUtils;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.MODE_PRIVATE;

public class JEEAdvanceTemplates extends ParentFragment implements View.OnClickListener, IItemHandler, IFileUploadCallback {

    private OnFragmentInteractionListener mFragListener;

    private View layout;

    private SriVishwa activity;

    private RecyclerView rv_ques_nums;

    private Dialog mDialog;

    private QuestionNumberListingAdapter_Em adapter;

    private TextView tv_questionno;

    private TextView tv_notvisitedcnt;

    private TextView tv_notansweredcnt;

    private TextView tv_answeredcnt;

    private TextView tv_mfrcnt;

    private TextView tv_amfrcnt;

    private WebView iv_option1, iv_option2, iv_option3, iv_option4;

    private WebView iv_question, iv_questionimg,iv_questionimg2;

    private int currentExamId = -1;

    private int question_no = 0;

    private RadioGroup rg_options;

    private CustomCheckBox checkBox1, checkBox2, checkBox3, checkBox4;

    private JSONObject data = new JSONObject();

    private JSONArray array = new JSONArray();

    private ImageLoader imageLoader;

    private TextView tv_timer,para_title;

    private JSONObject json;

    private String subjects, no_of_questions, noOfQuestions = null;

    private String subjectsArray[];

    private int check = 0, clickcount = 0;

    private long questionStartTime = 0;

    private FileOutputStream fos = null;

    private TabLayout tl_subjects;

    private TabLayout tl_sections;

    private long timeTaken4Question = 0, timeTaken = 0;

    private Date edate, sdate;

    private static String FILE_NAME;

    private boolean isVisible = false;

    int tabPosition_sub = 0, tabPosition = 0;

    private String section = "", type_ID = "", db_fields = "";

    App_Table table;

    String path;

    private String PATH = Environment.getExternalStorageDirectory().toString();

    private final String IMGPATH = PATH + "/System/allFiles/";

    JSONObject studentDetails, question_details;

    private String First, Second, Third, Fourth;

    private String Total = "";

    private CustomEditText ed_texx = null;


    public JEEAdvanceTemplates() {
        // Required empty public constructor
    }

    public static JEEAdvanceTemplates newInstance(String data) {

        JEEAdvanceTemplates fragment = new JEEAdvanceTemplates();

        Bundle args = new Bundle();

        args.putString("data", data);

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            try {
                table = new App_Table(activity);
                data = new JSONObject(getArguments().getString("data"));
                studentDetails = new JSONObject(AppPreferences.getInstance(getActivity()).getFromStore("studentDetails"));
            } catch (Exception e) {

                TraceUtils.logException(e);

            }

        }

        imageLoader = ImageLoader.getInstance();

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.jee_advance_template, container, false);


        activity.getSupportActionBar().setHomeButtonEnabled(false);

        tv_timer = layout.findViewById(R.id.tv_timer);

        para_title = layout.findViewById(R.id.para_title);

        rg_options = layout.findViewById(R.id.rg_options);

        iv_question = layout.findViewById(R.id.iv_question);

        iv_questionimg = layout.findViewById(R.id.iv_questionimg);
        iv_questionimg2 = layout.findViewById(R.id.iv_questionimg2);

        iv_option1 = layout.findViewById(R.id.iv_option1);

        iv_option2 = layout.findViewById(R.id.iv_option2);

        iv_option3 = layout.findViewById(R.id.iv_option3);

        iv_option4 = layout.findViewById(R.id.iv_option4);


        checkBox1 = layout.findViewById(R.id.ch_first);
        checkBox2 = layout.findViewById(R.id.ch_second);
        checkBox3 = layout.findViewById(R.id.ch_third);
        checkBox4 = layout.findViewById(R.id.ch_four);
        ed_texx = layout.findViewById(R.id.ed_texx);


        tv_questionno = layout.findViewById(R.id.tv_questionno);

        tv_notvisitedcnt = layout.findViewById(R.id.tv_notvisitedcnt);

        tv_notansweredcnt = layout.findViewById(R.id.tv_notansweredcnt);

        tv_answeredcnt = layout.findViewById(R.id.tv_answeredcnt);

        tv_mfrcnt = layout.findViewById(R.id.tv_mfrcnt);

        tv_amfrcnt = layout.findViewById(R.id.tv_amfrcnt);

        layout.findViewById(R.id.tv_savennext).setOnClickListener(this);

        ((TextView) layout.findViewById(R.id.user_name)).setText(studentDetails.optString("student_name"));
        //   layout.findViewById(R.id.tv_savenmarkforreview).setOnClickListener(this);

        layout.findViewById(R.id.tv_clearresponse).setOnClickListener(this);

        layout.findViewById(R.id.tv_mfrn).setOnClickListener(this);


        layout.findViewById(R.id.tv_submit).setOnClickListener(this);

        //    layout.findViewById(R.id.tv_next).setOnClickListener(this);

        tl_subjects = layout.findViewById(R.id.tl_subjects);

        tl_sections = layout.findViewById(R.id.tl_sections);

        adapter = new QuestionNumberListingAdapter_Em(activity);

        adapter.setOnClickListener(this);

        rv_ques_nums = layout.findViewById(R.id.rv_ques_nums);

        rv_ques_nums.setLayoutManager(new GridLayoutManager(activity, 5));

        rv_ques_nums.setAdapter(adapter);

        try {

            JSONObject question_details = data.getJSONObject("question_details");

            PhoneComponent phncomp = new PhoneComponent(this, activity, 1);

            phncomp.defineWhereClause("question_paper_id = '" + question_details.optString("question_paper_id") + "'");

            phncomp.executeLocalDBInBackground("STUDENTQUESTIONPAPER");

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        subjects = data.optString("subjects").trim();

        if (subjects.contains(",")) {

            subjectsArray = subjects.split(",");

        } else {

            subjectsArray = new String[]{subjects};

        }
        try {
            question_details = data.getJSONObject("question_details");
            type_ID = table.getTypeID(question_details.optString("question_paper_id"), 1, subjectsArray[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < subjectsArray.length; i++) {

            String subject = subjectsArray[i];

            TextView textView = (TextView) View.inflate(activity, R.layout.tab_subjects, null);

            textView.setText(subject);

            tl_subjects.addTab(tl_subjects.newTab().setCustomView(textView));

        }

        for (int k = 1; k < 4; k++) {
            TextView textView = (TextView) View.inflate(activity, R.layout.tab_subjects, null);

            textView.setText("Section-" + k);

            tl_sections.addTab(tl_sections.newTab().setCustomView(textView));
        }


        tl_subjects.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                tabPosition_sub = tab.getPosition();

                if (tabPosition_sub == 0) {

                    updateQuestionTime();

                    showNextQuestion(0);

                    return;

                }

                noOfQuestions = data.optString("no_of_questions");

                if (noOfQuestions.contains(",")) {

                    String temp1[] = noOfQuestions.split(",");

                    int questionIndex = 0;

                    for (int i = 0; i < tabPosition_sub; i++) {

                        questionIndex = questionIndex + Integer.parseInt(temp1[i]);

                    }

                    updateQuestionTime();

                    showNextQuestion(questionIndex);

                }

                // getQuestionsFromDBNShow(noOfQuestions);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        tl_sections.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                try {
                    tabPosition = tab.getPosition();

                    if (tabPosition == 0) {

                        JSONObject question_details = data.getJSONObject("question_details");
                        type_ID = table.getTypeID(question_details.optString("question_paper_id"), 1, subjectsArray[tabPosition_sub]);

                    } else if (tabPosition == 1) {
                        JSONObject question_details = data.getJSONObject("question_details");
                        type_ID = table.getTypeID(question_details.optString("question_paper_id"), 2, subjectsArray[tabPosition_sub]);

                    } else if (tabPosition == 2) {
                        JSONObject question_details = data.getJSONObject("question_details");
                        type_ID = table.getTypeID(question_details.optString("question_paper_id"), 3, subjectsArray[tabPosition_sub]);

                    }

                    if (tabPosition == 0) {

                        if (tabPosition_sub == 0) {

                            updateQuestionTime();

                            showNextQuestion(0);
                        } else {
                            int questionIndex = 0;

                            String temp2[] = noOfQuestions.split(",");

                            for (int i = 0; i < tabPosition_sub; i++) {

                                questionIndex = questionIndex + Integer.parseInt(temp2[i]);

                            }
                            updateQuestionTime();

                            showNextQuestion(questionIndex);
                        }

                        return;

                    }

                    String no_OfQuestions = table.getQuestions(question_details.optString("question_paper_id"), tabPosition + 1, subjectsArray[tabPosition_sub]);

                    if (no_OfQuestions.contains(",")) {

                        String temp1[] = no_OfQuestions.split(",");
                        int questionIndex = 0;
                        if (noOfQuestions != null) {

                            String temp2[] = noOfQuestions.split(",");

                            if (tabPosition_sub == 0){
                                if (tabPosition == 0) {
                                    updateQuestionTime();
                                    showNextQuestion(0);
                                }else{
                                    for (int i = 0; i < tabPosition; i++) {

                                        questionIndex = questionIndex + Integer.parseInt(temp1[i]);

                                    }
                                }
                            }
                           else if (tabPosition_sub == 1) {
                                if (tabPosition == 1) {

                                    questionIndex = questionIndex + Integer.parseInt(temp2[0]) + Integer.parseInt(temp1[1]);
                                } else {
                                    questionIndex = questionIndex + Integer.parseInt(temp2[0]) + Integer.parseInt(temp1[1]) + Integer.parseInt(temp1[2]);
                                }
                            } else {
                                if (tabPosition == 1) {

                                    questionIndex = questionIndex + Integer.parseInt(temp2[0]) + Integer.parseInt(temp1[0]) + Integer.parseInt(temp2[1]);
                                } else {
                                    questionIndex = questionIndex + Integer.parseInt(temp2[0]) + Integer.parseInt(temp1[0]) + Integer.parseInt(temp1[1]) + Integer.parseInt(temp2[1]);
                                }
                            }


                        } else {

                            for (int i = 0; i < tabPosition; i++) {

                                questionIndex = questionIndex + Integer.parseInt(temp1[i]);

                            }
                        }
                        updateQuestionTime();

                        showNextQuestion(questionIndex);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

          showTimer((data.optInt("duration_sec") * 1000));

        sdate = new Date();

        return layout;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mFragListener = (SriVishwa) context;

        activity = (SriVishwa) context;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        mFragListener.onFragmentInteraction(data.optString("exam_name"), false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragListener.onFragmentInteraction(data.optString("exam_name"), false);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_exams, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_gi:

                activity.showInstructionsScreen(data, false);

                break;

            case R.id.action_aq:
                Toast.makeText(activity, "Can't preview...", Toast.LENGTH_SHORT).show();
              //  activity.allQuestions_view(adapter.getItems());

               // activity.showAllQuestions();

                break;
        }

        return true;

    }

    private void updateQuestionTime() {

        edate = new Date();

        Long minutes = ((edate.getTime() - sdate.getTime()) / (60 * 1000)) * 60;
        timeTaken4Question = minutes + (edate.getTime() - sdate.getTime()) / 1000;

        sdate = edate;
        edate = null;


        try {

            if (currentExamId != -1) {

                App_Table table = new App_Table(activity);

                JSONObject jsonObject = adapter.getItems().getJSONObject(currentExamId);

                String iwhereClause = "exam_id = '" + data.optString("exam_id") + "' AND question_id = '" + jsonObject.optInt("question_id") + "'";

                JSONArray jsonArray = table.getRecords(iwhereClause, "STUDENTQUESTIONTIME");

                if (jsonArray.length() > 0) {

                    //long timeTaken = 0;

                    //int clickcount = 0;

                    JSONObject jsonObject1 = jsonArray.getJSONObject(jsonArray.length() - 1);

                    String question_time = jsonObject1.optString("question_time").trim();

                    String clicks = jsonObject1.optString("no_of_clicks").trim();

                    if (question_time.length() > 0) {

                        timeTaken = Long.parseLong(question_time);

                    }
                    if (clicks.length() > 0) {
                        clickcount = Integer.parseInt(clicks);
                    }

                    clickcount = clickcount + check;
                    timeTaken = timeTaken + timeTaken4Question;
                    jsonObject1.put("question_time", timeTaken);
                    jsonObject1.put("given_option", jsonObject.optString("qanswer"));
                    jsonObject1.put("correct_option", jsonObject.optString("answer"));
                    jsonObject1.put("result", "");
                    jsonObject1.put("question_time", timeTaken4Question + "");
                    jsonObject1.put("no_of_clicks", clickcount);
                    jsonObject1.put("marked_for_review", "0");

                    iwhereClause = "exam_id = '" + data.optString("exam_id") + "' AND question_id = '" + jsonObject.optInt("question_id") + "'";

                    table.checkNInsertARecord(jsonObject1, "STUDENTQUESTIONTIME", iwhereClause);

                    check = 0;
                    clickcount = 0;
                    timeTaken4Question = 0;
                    timeTaken = 0;

                    return;
                }

                // {"question_id":"827","topic_id":"51","topic_name":"To find equations of locus - Problems connected to it","question_name":"5841.PNG","question_name1":"NULL","question_name2":"NULL","question_name3":"NULL","option_a":"5842.PNG","option_b":"5843.PNG","option_c":"5844.PNG","option_d":"5845.PNG","answer":"c","solution1":"6301.PNG","solution2":"NULL","solution3":"NULL","solution4":"NULL","difficulty":"0.0","status":"1.0","qstate":2,"qanswer":"a","sno":1}
                // {"exam_id":"4","exam_name":"JEE Test Exam","course":"1","subjects":"Mathematics,Physics,Chemistry","no_of_questions":"15,15,15","marks_per_question":"1","negative_marks":"0.25","duration":"2","question_details":{"question_paper_id":"34","exam_id":"4","exam_date":"07\/08\/2019","from_time":"08:00 AM","to_time":"10:00 AM","subjects":"Mathematics 1A,Mathematics 1B,Physics","topicids":"1,2,3,4,5,6,7,8,9,10,11,12,13,51,52,54,56,57,58,59,60,61,62,63,64,683,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116"}}

                int student_question_time_id = AppPreferences.getInstance(activity).getIntegerFromStore("student_question_time_id");

                AppPreferences.getInstance(activity).addIntegerToStore("student_question_time_id", ++student_question_time_id, false);

                JSONObject questionTimeObject = new JSONObject();

                questionTimeObject.put("student_question_time_id", student_question_time_id);
                questionTimeObject.put("student_id", activity.getStudentDetails().optInt("student_id"));
                questionTimeObject.put("exam_id", data.optInt("exam_id"));
                questionTimeObject.put("question_no", jsonObject.optString("sno"));
                questionTimeObject.put("question_id", jsonObject.optInt("question_id"));
                questionTimeObject.put("topic_id", jsonObject.optInt("topic_id"));
                questionTimeObject.put("lesson_id", getLessonID(jsonObject.optInt("topic_id")));
                questionTimeObject.put("subject", ((TextView) (tl_subjects.getTabAt(tl_subjects.getSelectedTabPosition()).getCustomView())).getText().toString());
                questionTimeObject.put("given_option", jsonObject.optString("qanswer"));
                questionTimeObject.put("correct_option", jsonObject.optString("answer"));

                questionTimeObject.put("result", "");
                questionTimeObject.put("question_time", timeTaken4Question + "");
                questionTimeObject.put("no_of_clicks", check);
                questionTimeObject.put("marked_for_review", "0");

                table.insertSingleRecords(questionTimeObject, "STUDENTQUESTIONTIME");
                check = 0;
                timeTaken4Question = 0;

                // question_no++;

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }


    }

    private int getLessonID(int tid) {
        int lesson_id = 0;
        Database database = new Database(getActivity());
        try {
            if (database != null) {

                String cursor_q = "select * from TOPICS where topic_id=" + tid;
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            lesson_id = cursor.getInt(cursor.getColumnIndex("lessons_id"));
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

        return lesson_id;
    }

    @Override
    public void onClick(View v) {
        //qstate = //0 = not visited, 1 = not answered, 2 = answered, 3 = marked for review, 4 = answered and marked for review
        try {

            switch (v.getId()) {

                case R.id.ll_questionno:

                    int position = rv_ques_nums.getChildAdapterPosition(v);

                    String allquestions = table.getAllQuestionView(data.optInt("exam_id"));
                    JSONArray jsonArray = new JSONArray(allquestions);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject ood = jsonArray.getJSONObject(i);
                        if (position == i) {
                            type_ID = ood.optString("type_id");
                        }
                    }
                    currentExamId = position;
                    JSONObject jsonObject = adapter.getItems().getJSONObject(position);
                    if (jsonObject.optString("qstate").equalsIgnoreCase("2")) {
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_anse));
                        //  v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_answered));
                    } else if (jsonObject.optString("qstate").equalsIgnoreCase("0")) {
                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {
                            rg_options.clearCheck();
                        } else if (type_ID.equalsIgnoreCase("2")) {
                            checkBox1.setChecked(false);
                            checkBox2.setChecked(false);
                            checkBox3.setChecked(false);
                            checkBox4.setChecked(false);
                        } else if (type_ID.equalsIgnoreCase("3")) {
                            ed_texx.setText("");
                        }
                        jsonObject.put("qstate", 1);
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_preve));
                        // v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_not_answered));
                    } else if (jsonObject.optString("qstate").equalsIgnoreCase("3")) {
                        jsonObject.put("qstate", 3);
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_marked_for_review));
                        // v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_marked_for_review));
                    }else if (jsonObject.optString("qstate").equalsIgnoreCase("4")) {
                        jsonObject.put("qstate", 4);
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_answered_marked));
                        // v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_answered_marked));
                    }  else {
                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {
                            rg_options.clearCheck();
                        } else if (type_ID.equalsIgnoreCase("2")) {
                            checkBox1.setChecked(false);
                            checkBox2.setChecked(false);
                            checkBox3.setChecked(false);
                            checkBox4.setChecked(false);
                        } else if (type_ID.equalsIgnoreCase("3")) {
                            ed_texx.setText("");
                        }
                        jsonObject.put("qstate", 1);
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_preve));
                        //  v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_not_answered));
                    }
                    adapter.notifyItemChanged(position);

                    showNextQuestion(position);

                    break;

                case R.id.tv_savennext:
                    if (currentExamId >= adapter.getCount()) {
                        currentExamId = adapter.getCount() - 1;
                        int selRatioId = rg_options.getCheckedRadioButtonId();
                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {

                            if (selRatioId == -1) {

                                activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psao));

                                return;
                            }
                        }else if (type_ID.equalsIgnoreCase("2")) {
                            boolean check = checkBox1.isChecked() || checkBox2.isChecked() || checkBox3.isChecked() || checkBox4.isChecked();
                            if (check == false) {
                                activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psao));
                                return;
                            }
                        }else {
                            if (ed_texx.length() == 0) {
                                activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psas));
                                return;
                            }
                        }
                        allquestions = table.getAllQuestionView(data.optInt("exam_id"));
                        jsonArray = new JSONArray(allquestions);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ood = jsonArray.getJSONObject(i);
                            if (currentExamId == i) {
                                type_ID = ood.optString("type_id");
                            }
                        }

                        if (currentExamId == adapter.getCount()) {

                            return;

                        }


                        jsonObject = adapter.getItems().getJSONObject(currentExamId);
                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {

                            jsonObject.put("qstate", 2);

                            jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());

                        } else if (type_ID.equalsIgnoreCase("2")) {

                            if (checkBox1.isChecked()) {
                                Total="";
                                Total = "a";
                            }
                            if (checkBox2.isChecked()) {
                                Total="";
                                Total = "b";
                            }
                            if (checkBox3.isChecked()) {
                                Total="";
                                Total = "c";
                            }
                            if (checkBox4.isChecked()) {
                                Total="";
                                Total = "d";
                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b";


                            }
                            if (checkBox1.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "c";


                            }
                            if (checkBox1.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "d";

                            }
                            if (checkBox2.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "c";


                            }
                            if (checkBox2.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "d";


                            }
                            if (checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "c" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "c";

                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "c" + "," + "d";


                            }
                            if (checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "c" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "c" + "," + "d";


                            }
                            jsonObject.put("qstate", 2);

                            jsonObject.put("qanswer", Total);

                        } else if (type_ID.equalsIgnoreCase("3")) {


                            jsonObject.put("qstate", 2);

                            jsonObject.put("qanswer", ed_texx.getText().toString().trim());
                        } else {

                            jsonObject.put("qstate", 2);

                            jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());
                        }

                        adapter.notifyItemChanged(currentExamId);

                        if (jsonObject.optInt("sno") < adapter.getItemCount()) {
                            if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {
                                rg_options.clearCheck();
                            } else if (type_ID.equalsIgnoreCase("2")) {
                                checkBox1.setChecked(false);
                                checkBox2.setChecked(false);
                                checkBox3.setChecked(false);
                                checkBox4.setChecked(false);
                            } else if (type_ID.equalsIgnoreCase("3")) {
                                ed_texx.setText("");
                            }

                        }

                        updateQuestionTime();
                        showNextQuestion(currentExamId);


                    }
                    if (currentExamId != -1) {
                        int selRatioId = rg_options.getCheckedRadioButtonId();
                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {

                            if (selRatioId == -1) {

                                activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psao));

                                return;
                            }
                        }else if (type_ID.equalsIgnoreCase("2")) {
                            boolean check = checkBox1.isChecked() || checkBox2.isChecked() || checkBox3.isChecked() || checkBox4.isChecked();
                            if (check == false) {
                                activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psao));
                                return;
                            }
                        }else {
                            if (ed_texx.length() == 0) {
                                activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psas));
                                return;
                            }
                        }
                        allquestions = table.getAllQuestionView(data.optInt("exam_id"));
                        jsonArray = new JSONArray(allquestions);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ood = jsonArray.getJSONObject(i);
                            if (currentExamId == i) {
                                type_ID = ood.optString("type_id");
                            }
                        }

                        if (currentExamId == adapter.getCount()) {

                            return;

                        }


                        jsonObject = adapter.getItems().getJSONObject(currentExamId);
                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {

                            jsonObject.put("qstate", 2);

                            jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());

                        } else if (type_ID.equalsIgnoreCase("2")) {

                            if (checkBox1.isChecked()) {
                                Total="";
                                Total = "a";
                            }
                            if (checkBox2.isChecked()) {
                                Total="";
                                Total = "b";
                            }
                           if (checkBox3.isChecked()) {
                               Total="";
                                Total = "c";
                            }
                           if (checkBox4.isChecked()) {
                               Total="";
                                Total = "d";
                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b";


                            }
                            if (checkBox1.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "c";


                            }
                            if (checkBox1.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "d";

                            }
                            if (checkBox2.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "c";


                            }
                            if (checkBox2.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "d";


                            }
                            if (checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "c" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "c";

                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "c" + "," + "d";


                            }
                             if (checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "c" + "," + "d";


                            }

                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "c" + "," + "d";


                            }
                            jsonObject.put("qstate", 2);

                            jsonObject.put("qanswer", Total);

                        } else if (type_ID.equalsIgnoreCase("3")) {


                            jsonObject.put("qstate", 2);

                            jsonObject.put("qanswer", ed_texx.getText().toString().trim());
                        } else {

                            jsonObject.put("qstate", 2);

                            jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());
                        }

                        adapter.notifyItemChanged(currentExamId);

                        if (jsonObject.optInt("sno") < adapter.getItemCount()) {
                            if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {
                                rg_options.clearCheck();
                            } else if (type_ID.equalsIgnoreCase("2")) {
                                checkBox1.setChecked(false);
                                checkBox2.setChecked(false);
                                checkBox3.setChecked(false);
                                checkBox4.setChecked(false);
                            } else if (type_ID.equalsIgnoreCase("3")) {
                                ed_texx.setText("");
                            }

                        }

                        updateQuestionTime();


                        showNextQuestion(currentExamId + 1);


                    }

                    break;


                case R.id.tv_clearresponse:
                    if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {
                        rg_options.clearCheck();
                    } else if (type_ID.equalsIgnoreCase("2")) {
                        checkBox1.setChecked(false);
                        checkBox2.setChecked(false);
                        checkBox3.setChecked(false);
                        checkBox4.setChecked(false);
                    } else if (type_ID.equalsIgnoreCase("3")) {
                        ed_texx.setText("");
                    }

                    jsonObject = adapter.getItems().getJSONObject(currentExamId);
                    jsonObject.put("qstate", 1);
                    jsonObject.put("qanswer", "");

                    adapter.notifyItemChanged(currentExamId);
                    updateQuestionTime();

                    break;

                case R.id.tv_mfrn:
                    if (currentExamId >= adapter.getCount()) {
                        currentExamId=adapter.getCount();
                        allquestions = table.getAllQuestionView(data.optInt("exam_id"));
                        jsonArray = new JSONArray(allquestions);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ood = jsonArray.getJSONObject(i);
                            if (currentExamId == i) {
                                type_ID = ood.optString("type_id");
                            }
                        }
                        jsonObject = adapter.getItems().getJSONObject(currentExamId);
                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {

                            int selRatioId = rg_options.getCheckedRadioButtonId();
                            if (selRatioId != -1) {
                                jsonObject.put("qstate", 4);

                                jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());
                            } else {
                                jsonObject.put("qstate", 3);

                                jsonObject.put("qanswer", "");
                            }

                        } else if (type_ID.equalsIgnoreCase("2")) {
                            if (checkBox1.isChecked()) {
                                Total="";
                                Total = "a";
                            }
                            if (checkBox2.isChecked()) {
                                Total="";
                                Total = "b";
                            }
                            if (checkBox3.isChecked()) {
                                Total="";
                                Total = "c";
                            }
                            if (checkBox4.isChecked()) {
                                Total="";
                                Total = "d";
                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b";


                            }
                            if (checkBox1.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "c";


                            }
                            if (checkBox1.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "d";

                            }
                            if (checkBox2.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "c";


                            }
                            if (checkBox2.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "d";


                            }
                            if (checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "c" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "c";

                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "c" + "," + "d";


                            }
                            if (checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "c" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "d";


                            }

                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "c" + "," + "d";
                            }
                            if (TextUtils.isEmpty(Total)) {
                                jsonObject.put("qstate", 3);
                            }else{
                                jsonObject.put("qstate", 4);
                            }

                            jsonObject.put("qanswer", Total);
                        } else if (type_ID.equalsIgnoreCase("3")) {
                            if (ed_texx.getText().toString().length()==0) {

                                jsonObject.put("qstate", 3);

                                jsonObject.put("qanswer", ed_texx.getText().toString().trim());
                            }else{
                                jsonObject.put("qstate", 4);

                                jsonObject.put("qanswer", ed_texx.getText().toString().trim());
                            }
                        }
                        adapter.notifyItemChanged(currentExamId);

                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {
                            rg_options.clearCheck();
                        } else if (type_ID.equalsIgnoreCase("2")) {
                            checkBox1.setChecked(false);
                            checkBox2.setChecked(false);
                            checkBox3.setChecked(false);
                            checkBox4.setChecked(false);
                        } else if (type_ID.equalsIgnoreCase("3")) {
                            ed_texx.setText("");
                        }

                        updateQuestionTime();

                        showNextQuestion(currentExamId);
                        return;
                    }
                    if (currentExamId != -1) {
                        allquestions = table.getAllQuestionView(data.optInt("exam_id"));
                        jsonArray = new JSONArray(allquestions);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ood = jsonArray.getJSONObject(i);
                            if (currentExamId == i) {
                                type_ID = ood.optString("type_id");
                            }
                        }
                        jsonObject = adapter.getItems().getJSONObject(currentExamId);
                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {

                                int selRatioId = rg_options.getCheckedRadioButtonId();
                                if (selRatioId != -1) {
                                    jsonObject.put("qstate", 4);

                                    jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());
                                } else {
                                    jsonObject.put("qstate", 3);

                                    jsonObject.put("qanswer", "");
                                }

                        } else if (type_ID.equalsIgnoreCase("2")) {
                            if (checkBox1.isChecked()) {
                                Total="";
                                Total = "a";
                            }
                            if (checkBox2.isChecked()) {
                                Total="";
                                Total = "b";
                            }
                            if (checkBox3.isChecked()) {
                                Total="";
                                Total = "c";
                            }
                            if (checkBox4.isChecked()) {
                                Total="";
                                Total = "d";
                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b";


                            }
                            if (checkBox1.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "c";


                            }
                            if (checkBox1.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "d";

                            }
                            if (checkBox2.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "c";


                            }
                            if (checkBox2.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "d";


                            }
                            if (checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "c" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "c";

                            }
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "d";


                            }
                            if (checkBox1.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "c" + "," + "d";


                            }
                            if (checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "b" + "," + "c" + "," + "d";


                            }

                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked() && checkBox4.isChecked()) {
                                Total = "";
                                Total = "a" + "," + "b" + "," + "c" + "," + "d";
                            }
                            if (TextUtils.isEmpty(Total)) {
                                jsonObject.put("qstate", 3);
                            }else{
                                jsonObject.put("qstate", 4);
                            }

                            jsonObject.put("qanswer", Total);
                        } else if (type_ID.equalsIgnoreCase("3")) {
                            if (ed_texx.getText().toString().length()==0) {

                                jsonObject.put("qstate", 3);

                                jsonObject.put("qanswer", ed_texx.getText().toString().trim());
                            }else{
                                jsonObject.put("qstate", 4);

                                jsonObject.put("qanswer", ed_texx.getText().toString().trim());
                            }
                        }
                        adapter.notifyItemChanged(currentExamId);

                        if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {
                            rg_options.clearCheck();
                        } else if (type_ID.equalsIgnoreCase("2")) {
                            checkBox1.setChecked(false);
                            checkBox2.setChecked(false);
                            checkBox3.setChecked(false);
                            checkBox4.setChecked(false);
                        } else if (type_ID.equalsIgnoreCase("3")) {
                            ed_texx.setText("");
                        }

                        updateQuestionTime();

                        showNextQuestion(currentExamId + 1);

                    }

                    break;

                case R.id.tv_submit:
                    mDialog = new Dialog(getActivity());
                    mDialog.setContentView(R.layout.popup_message_ok);
                    mDialog.show();
                    mDialog.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            showResults();
                        }
                    });
                    ((TextView) mDialog.findViewById(R.id.tv_title)).setText(R.string.nonettitle);
                    ((TextView) mDialog.findViewById(R.id.tv_message)).setText(R.string.sbs);
                    mDialog.findViewById(R.id.tv_cancel).setVisibility(View.VISIBLE);
                    mDialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });

                    break;

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void showNextQuestion(int position) {

        try {
            String allquestions = table.getAllQuestionView(data.optInt("exam_id"));
            JSONArray jsonArray_f = new JSONArray(allquestions);
            for (int i = 0; i < jsonArray_f.length(); i++) {
                JSONObject ood = jsonArray_f.getJSONObject(i);
                if (position == i) {
                    type_ID = ood.optString("type_id");
                }
            }
            questionStartTime = System.currentTimeMillis();

            currentExamId = position;

            if (position == adapter.getCount()) {
                JSONArray jsonArray = adapter.getItems();

                int notvisited = 0;

                int notanswered = 0;

                int answered = 0;

                int mfr = 0;

                int amfr = 0;

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    if (jsonObject1.optString("qstate").equalsIgnoreCase("0")) {

                        ++notvisited;

                    } else if (jsonObject1.optString("qstate").equalsIgnoreCase("1")) {

                        ++notanswered;

                    } else if (jsonObject1.optString("qstate").equalsIgnoreCase("2")) {

                        ++answered;

                    } else if (jsonObject1.optString("qstate").equalsIgnoreCase("3")) {

                        ++mfr;

                    } else if (jsonObject1.optString("qstate").equalsIgnoreCase("4")) {

                        ++amfr;

                    }


                }
                tv_notvisitedcnt.setText(notvisited + "");

                tv_notansweredcnt.setText(notanswered + "");

                tv_answeredcnt.setText(answered + "");

                tv_mfrcnt.setText(mfr + "");

                tv_amfrcnt.setText(amfr + "");
                return;
            }

            JSONArray jsonArray = adapter.getItems();

            JSONObject jsonObject = jsonArray.getJSONObject(position);

            adapter.setSelectedPosition(position);

            adapter.notifyDataSetChanged();

            tv_questionno.setText(jsonObject.optString("sno"));

            File file = new File(IMGPATH, jsonObject.optString("question_name"));

            StringBuilder html_content = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    html_content.append(line);
                    html_content.append('\n');
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String my_qs_file =AESEncryptionDecryption.decrypt(html_content.toString());
                    //
            String[] data_questions = my_qs_file.split("</html>");

            if (type_ID.equalsIgnoreCase("4")){
                iv_questionimg.setVisibility(View.VISIBLE);
                para_title.setVisibility(View.VISIBLE);
            }else{
                iv_questionimg.setVisibility(View.GONE);
                para_title.setVisibility(View.GONE);

            }
            iv_questionimg.clearHistory();
            iv_question.clearHistory();
            iv_option1.clearHistory();
            iv_option2.clearHistory();
            iv_option3.clearHistory();
            iv_option4.clearHistory();
           // iv_questionimg.loadData(data_questions[0], "text/html", "utf-8");
            iv_question.loadDataWithBaseURL("",data_questions[0], "text/html", "utf-8",null);
            iv_questionimg.loadDataWithBaseURL("",data_questions[0], "text/html", "utf-8",null);
            iv_option1.loadDataWithBaseURL("",data_questions[1], "text/html", "utf-8",null);
            iv_option2.loadDataWithBaseURL("",data_questions[2], "text/html", "utf-8",null);
            iv_option3.loadDataWithBaseURL("",data_questions[3], "text/html", "utf-8",null);
            iv_option4.loadDataWithBaseURL("",data_questions[4], "text/html", "utf-8",null);
            if (type_ID.equalsIgnoreCase("1")||type_ID.equalsIgnoreCase("4")) {
                layout.findViewById(R.id.opt_four).setVisibility(View.GONE);
                layout.findViewById(R.id.mult_ll).setVisibility(View.GONE);
                layout.findViewById(R.id.rg_options).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.qs_options).setVisibility(View.VISIBLE);
                ed_texx.setVisibility(View.GONE);

                if (jsonObject.optString("qanswer").equalsIgnoreCase("a")) {

                    ((RadioButton) rg_options.findViewById(R.id.rb_first)).setChecked(true);

                } else if (jsonObject.optString("qanswer").equalsIgnoreCase("b")) {

                    ((RadioButton) rg_options.findViewById(R.id.rb_second)).setChecked(true);

                } else if (jsonObject.optString("qanswer").equalsIgnoreCase("c")) {

                    ((RadioButton) rg_options.findViewById(R.id.rb_third)).setChecked(true);

                } else if (jsonObject.optString("qanswer").equalsIgnoreCase("d")) {

                    ((RadioButton) rg_options.findViewById(R.id.rb_fourth)).setChecked(true);

                }
            } else if (type_ID.equalsIgnoreCase("2")) {
                layout.findViewById(R.id.mult_ll).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.rg_options).setVisibility(View.GONE);
                layout.findViewById(R.id.opt_four).setVisibility(View.GONE);
                layout.findViewById(R.id.qs_options).setVisibility(View.VISIBLE);
                ed_texx.setVisibility(View.GONE);
                if (jsonObject.optString("qanswer").equalsIgnoreCase("a")) {

                    checkBox1.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                    Total = First;

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("b")) {

                    checkBox2.setChecked(true);
                    checkBox1.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("c")) {

                    checkBox3.setChecked(true);
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox4.setChecked(false);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("d")) {

                    checkBox4.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox1.setChecked(false);
                    checkBox3.setChecked(false);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("a,b")) {

                    checkBox4.setChecked(false);
                    checkBox2.setChecked(true);
                    checkBox1.setChecked(true);
                    checkBox3.setChecked(false);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("a,c")) {

                    checkBox4.setChecked(false);
                    checkBox3.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox1.setChecked(true);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("a,d")) {

                    checkBox4.setChecked(true);
                    checkBox1.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("a,b,c,d")) {

                    checkBox4.setChecked(true);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                    checkBox1.setChecked(true);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("a,b,c")) {

                    checkBox1.setChecked(true);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                    checkBox4.setChecked(false);

                } if (jsonObject.optString("qanswer").equalsIgnoreCase("a,c,d")) {

                    checkBox1.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(true);
                    checkBox4.setChecked(true);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("a,b,d")) {

                    checkBox1.setChecked(true);
                    checkBox2.setChecked(true);
                    checkBox4.setChecked(true);
                    checkBox3.setChecked(false);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("b,c,d")) {

                    checkBox4.setChecked(true);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                    checkBox1.setChecked(false);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("b,c")) {

                    checkBox4.setChecked(false);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                    checkBox1.setChecked(false);

                }
                if (jsonObject.optString("qanswer").equalsIgnoreCase("b,d")) {

                    checkBox4.setChecked(true);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(false);
                    checkBox1.setChecked(false);

                } if (jsonObject.optString("qanswer").equalsIgnoreCase("c,d")) {

                    checkBox4.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(true);
                    checkBox1.setChecked(false);

                }
            } else if (type_ID.equalsIgnoreCase("3")) {
                layout.findViewById(R.id.mult_ll).setVisibility(View.GONE);
                layout.findViewById(R.id.rg_options).setVisibility(View.GONE);
                layout.findViewById(R.id.opt_four).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.qs_options).setVisibility(View.GONE);
                ed_texx.setVisibility(View.VISIBLE);
                String ed_txt=jsonObject.optString("qanswer");
                if (ed_txt.length()==0){
                    ed_texx.setText("");
                }else {
                    ed_texx.setText(ed_txt);

                }

            }
            iv_questionimg.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.clearHistory();
                }
            });
            iv_option1.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.clearHistory();
                }
            });iv_option2.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.clearHistory();
                }
            });iv_option3.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.clearHistory();
                }
            });iv_option4.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.clearHistory();
                }
            });
            int notvisited = 0;

            int notanswered = 0;

            int answered = 0;

            int mfr = 0;

            int amfr = 0;

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                if (jsonObject1.optString("qstate").equalsIgnoreCase("0")) {

                    ++notvisited;

                } else if (jsonObject1.optString("qstate").equalsIgnoreCase("1")) {

                    ++notanswered;

                } else if (jsonObject1.optString("qstate").equalsIgnoreCase("2")) {

                    ++answered;

                } else if (jsonObject1.optString("qstate").equalsIgnoreCase("3")) {

                    ++mfr;

                } else if (jsonObject1.optString("qstate").equalsIgnoreCase("4")) {

                    ++amfr;

                }


            }

            //0 = not visited, 1 = not answered, 2 = answered, 3 = marked review, 4 = answered and marked for review,

            tv_notvisitedcnt.setText(notvisited + "");

            tv_notansweredcnt.setText(notanswered + "");

            tv_answeredcnt.setText(answered + "");

            tv_mfrcnt.setText(mfr + "");

            tv_amfrcnt.setText(amfr + "");


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

                JSONArray jsonArray = (JSONArray) results;

                if (jsonArray.length() > 0) {

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject student_question_paper_details = jsonArray.getJSONObject(i);

                        String questions = student_question_paper_details.optString("questions");
                        String type_ID = student_question_paper_details.optString("type_id");

//                        if (type_ID.equalsIgnoreCase("4")) {
//
//                            getParagraphQuestion(questions);
//
//                        } else {

                            getQuestionsFromDBNShow(questions);
//                        }

                    }

                }

            } else if (requestId == 2) {

                JSONArray jsonArray = (JSONArray) results;

                if (jsonArray.length() > 0) {
                    int c = jsonArray.length();
                    if (adapter.getItems().length() == 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            jsonObject.put("qstate", 0); //0 = not visited, 1 = not answered, 2 = answered, 3 = marked review, 4 = answered and marked for review, 5 = visited

                            jsonObject.put("qanswer", "");

                            jsonObject.put("sno", i + 1);

                            if (i == 0) {

                                jsonObject.put("qstate", 1);

                            }

                        }

                        adapter.setItems(jsonArray);

                    } else {

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            jsonObject.put("qstate", 0); //0 = not visited, 1 = not answered, 2 = answered, 3 = marked review, 4 = answered and marked for review, 5 = visited

                            jsonObject.put("qanswer", "");

                            jsonObject.put("sno", adapter.getCount() + 1);

                            adapter.getItems().put(jsonArray.getJSONObject(i));

                        }

                    }

                    adapter.notifyDataSetChanged();

                    showNextQuestion(0);

                }

            } else if (requestId == 3) {
                JSONObject obj = new JSONObject(results.toString());
                if (obj.optString("statuscode").equalsIgnoreCase("200")) {
                    // activity.onKeyDown(4, null);
//                    Toast.makeText(activity, "success", Toast.LENGTH_SHORT).show();
                    App_Table table = new App_Table(activity);
                    table.deleteRecord("exam_id='" + data.optInt("exam_id") + "'", "FILESDATA");
                    File file = new File(path);
                    file.delete();

                    path = "";

                } else {
                    App_Table table = new App_Table(activity);
                    table.deleteRecord("exam_id='" + data.optInt("exam_id") + "'", "FILESDATA");
                    File file = new File(path);
                    file.delete();

                    path = "";
                    //activity.onKeyDown(4, null);
                }
            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onError(String errorCode, int requestId) {

    }

   /* private void getParagraphQuestion(String pargagraph_ids) {
        try {
            if (pargagraph_ids.contains(",")) {
                String[] temp = pargagraph_ids.split(",");
                for (int i = 0; i < temp.length; i++) {
                    table.getparagrahs(temp[i]);
                   String questions=table.getparagrahQuestions(temp[i]);
                    if (questions.contains(",")) {
                        //JSONArray array = new JSONArray();
                        String[] temp_q = questions.split(",");
                        for (int j = 0; j < temp_q.length; j++) {
                            array.put(getQuestion_paragraph(temp_q[j],table.getparagrahs(temp[i])));
                        }
                        setData(array);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
    private void getQuestionsFromDBNShow(String questions) {

        try {

            if (questions.contains(",")) {

                String[] temp = questions.split(",");
                JSONArray   array = new JSONArray();

                for (int i = 0; i < temp.length; i++) {
                    array.put(getQuestion(temp[i]));
                }

                setData(array);
               /* whereQuestions = whereQuestions.trim();
                whereQuestions = whereQuestions.substring(0, whereQuestions.length() - 1);*/

            }

           /* phncomp.defineWhereClause("question_id IN (" + whereQuestions + ")");
            phncomp.executeLocalDBInBackground("QUESTIONS");*/

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void showResults() {

        try {

            App_Table table = new App_Table(activity);


            json = table.getExamsResult(data.optInt("exam_id"), activity.getStudentDetails().optInt("student_id"));
            json.put("student_exam_result_id", "");
            json.put("student_id", activity.getStudentDetails().optInt("student_id"));
            json.put("exam_id", data.optInt("exam_id"));
            json.put("exam_name", data.optString("exam_name"));
            FILE_NAME = System.currentTimeMillis() + "_Result.txt";
            fos = getActivity().openFileOutput(FILE_NAME, MODE_PRIVATE);

            fos.write(json.toString().getBytes());
            path = getActivity().getFilesDir().getAbsolutePath() + "/" + FILE_NAME;
            startUploadBackUp(path, FILE_NAME);
            Intent str=new Intent(getActivity(), MainActivity.class);
            startActivity(str);
            Toast.makeText(getActivity(), "Exam Submitted successfully...", Toast.LENGTH_SHORT).show();
          //  activity.showokPopUp(R.drawable.pop_ic_failed, activity.getString(R.string.errorTxt), activity.getString(R.string.isr));

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
     activity.onKeyDown(4,null);

    }

    private void startUploadBackUp(String path, String file_name) {

        String url = AppSettings.getInstance().getPropertyValue("uploadfile_admin");

        FileUploader uploader = new FileUploader(getActivity(), this);

        uploader.setFileName(file_name, file_name);

        uploader.userRequest("", 11, url, path);
    }

    private void showTimer(long millisInFuture) {

        new CountDownTimer(millisInFuture, 1000) {

            public void onTick(long millisUntilFinished) {

                //tv_timer.setText(activity.getString(R.string.time, millisUntilFinished / 1000));
                tv_timer.setText(activity.getString(R.string.time, convertSecondsToHMmSs((millisUntilFinished / 1000))));

            }

            public void onFinish() {
                showResults();
                tv_timer.setText("00:00:00");
            }

        }.start();

    }

    private String convertSecondsToHMmSs(long seconds) {

        long s = seconds % 60;

        long m = (seconds / 60) % 60;

        long h = (seconds / (60 * 60)) % 24;

        return String.format(Locale.ENGLISH, "%d:%02d:%02d", h, m, s);

    }


    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int reqID) {

        try {

            switch (what) {

                case -1: // failed

                    Toast.makeText(getActivity(), "Failed To Send", Toast.LENGTH_SHORT).show();

                    break;

                case 1: // progressBar

                    break;

                case 0: // success

                    JSONObject object = new JSONObject(obj.toString());
                    //     {"status":"0","status_description":"File Uploaded Successfully","attachname":"1552318451_Screenshot_20181203-194010_20190311_090349.png"}
                    dataSendServer(object.optString("file_name"));
                    //sendImage();
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

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("exam_id", data.optInt("exam_id"));

            jsonObject.put("student_id", activity.getStudentDetails().optInt("student_id"));

            jsonObject.put("file_name", file_name);

            HTTPPostTask post = new HTTPPostTask(getActivity(), this);

            post.userRequest(getString(R.string.plwait), 3, "submit_exam_result", jsonObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
    }

    public JSONObject getQuestion(String qid) {
        JSONObject obj = new JSONObject();
        try {
            Database database = new Database(getActivity());
            SQLiteDatabase db;
            if (database != null) {

                String cursor_q = "select * from QUESTIONS where question_id=" + Integer.parseInt(qid);

                db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();

                            obj.put("question_id", cursor.getString(cursor.getColumnIndex("question_id")));
                            obj.put("topic_id", cursor.getString(cursor.getColumnIndex("topic_id")));
                            obj.put("topic_name", cursor.getString(cursor.getColumnIndex("topic_name")));
                            obj.put("question_name", cursor.getString(cursor.getColumnIndex("question_name")));
                            obj.put("question_name1", cursor.getString(cursor.getColumnIndex("question_name1")));
                            obj.put("question_name2", cursor.getString(cursor.getColumnIndex("question_name2")));
                            obj.put("question_name3", cursor.getString(cursor.getColumnIndex("question_name3")));
                            obj.put("option_a", cursor.getString(cursor.getColumnIndex("option_a")));
                            obj.put("option_b", cursor.getString(cursor.getColumnIndex("option_b")));
                            obj.put("option_c", cursor.getString(cursor.getColumnIndex("option_c")));
                            obj.put("option_d", cursor.getString(cursor.getColumnIndex("option_d")));
                            obj.put("answer", cursor.getString(cursor.getColumnIndex("answer")));
                            obj.put("solution1", cursor.getString(cursor.getColumnIndex("solution1")));
                            obj.put("solution2", cursor.getString(cursor.getColumnIndex("solution2")));
                            obj.put("solution3", cursor.getString(cursor.getColumnIndex("solution3")));
                            obj.put("solution4", cursor.getString(cursor.getColumnIndex("solution4")));
                            obj.put("difficulty", cursor.getString(cursor.getColumnIndex("difficulty")));
                            obj.put("status", cursor.getString(cursor.getColumnIndex("status")));
                            obj.put("paragraph", cursor.getString(cursor.getColumnIndex("paragraph")));

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
        return obj;
    }

  //  public JSONObject getQuestion_paragraph(String qid,String paragraph_img) {

       /* try {
            Database database = new Database(getActivity());
            SQLiteDatabase db;
            if (database != null) {

                String cursor_q = "select * from QUESTIONS where question_id=" + Integer.parseInt(qid);

                db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();

                            obj.put("question_id", cursor.getString(cursor.getColumnIndex("question_id")));
                            obj.put("topic_id", cursor.getString(cursor.getColumnIndex("topic_id")));
                            obj.put("topic_name", cursor.getString(cursor.getColumnIndex("topic_name")));
                            obj.put("question_name", cursor.getString(cursor.getColumnIndex("question_name")));
                            obj.put("question_name1", cursor.getString(cursor.getColumnIndex("question_name1")));
                            obj.put("question_name2", cursor.getString(cursor.getColumnIndex("question_name2")));
                            obj.put("question_name3", cursor.getString(cursor.getColumnIndex("question_name3")));
                            obj.put("option_a", cursor.getString(cursor.getColumnIndex("option_a")));
                            obj.put("option_b", cursor.getString(cursor.getColumnIndex("option_b")));
                            obj.put("option_c", cursor.getString(cursor.getColumnIndex("option_c")));
                            obj.put("option_d", cursor.getString(cursor.getColumnIndex("option_d")));
                            obj.put("answer", cursor.getString(cursor.getColumnIndex("answer")));
                            obj.put("solution1", cursor.getString(cursor.getColumnIndex("solution1")));
                            obj.put("solution2", cursor.getString(cursor.getColumnIndex("solution2")));
                            obj.put("solution3", cursor.getString(cursor.getColumnIndex("solution3")));
                            obj.put("solution4", cursor.getString(cursor.getColumnIndex("solution4")));
                            obj.put("difficulty", cursor.getString(cursor.getColumnIndex("difficulty")));
                            obj.put("status", cursor.getString(cursor.getColumnIndex("status")));
                            obj.put("paragraph", paragraph_img);

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
        return obj;*/
   // }

    public void setData(JSONArray jsonArray) {
        if (jsonArray.length() > 0) {
            int c = jsonArray.length();
            if (adapter.getItems().length() == 0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = jsonArray.getJSONObject(i);


                        jsonObject.put("qstate", 0); //0 = not visited, 1 = not answered, 2 = answered, 3 = marked review, 4 = answered and marked for review, 5 = visited

                        jsonObject.put("qanswer", "");

                        jsonObject.put("sno", i + 1);

                        if (i == 0) {

                            jsonObject.put("qstate", 1);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                adapter.setItems(jsonArray);

            } else {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = jsonArray.getJSONObject(i);


                        jsonObject.put("qstate", 0); //0 = not visited, 1 = not answered, 2 = answered, 3 = marked review, 4 = answered and marked for review, 5 = visited

                        jsonObject.put("qanswer", "");

                        jsonObject.put("sno", adapter.getCount() + 1);

                        adapter.getItems().put(jsonArray.getJSONObject(i));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            adapter.notifyDataSetChanged();

            showNextQuestion(0);


        }

    }

    private boolean decryptCipher(String localLogoPath, String tmpFilePath) {

        FileInputStream fis = null;

        FileOutputStream fos = null;

        CipherInputStream cis = null;

        try {

            fis = new FileInputStream(localLogoPath);

            fos = new FileOutputStream(tmpFilePath);

            Cipher cipher = Cipher.getInstance("ARC4");

            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec("filepickerapp".getBytes(), "ARC4"));

            cis = new CipherInputStream(fis, cipher);

            int b;

            int chunkSize = 1024;

            byte[] d = new byte[chunkSize];

            while ((b = cis.read(d)) != -1) {

                fos.write(d, 0, b);

            }

            fos.flush();

            fis.close();

            fos.close();

            cis.close();

        } catch (Exception e) {

            TraceUtils.logException(e);

            return false;

        } finally {

            try {
                if (fis != null)
                    fis.close();

                if (fos != null)
                    fos.close();

                if (cis != null)
                    cis.close();

            } catch (Exception e) {

                TraceUtils.logException(e);

            }

        }

        return true;

    }

}