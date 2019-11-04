package com.adi.exam.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.QuestionNumberListingAdapter;
import com.adi.exam.callbacks.IFileUploadCallback;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AESEncryptionDecryption;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.common.AppSettings;
import com.adi.exam.database.App_Table;
import com.adi.exam.database.Database;
import com.adi.exam.database.PhoneComponent;
import com.adi.exam.tasks.FileUploader;
import com.adi.exam.tasks.HTTPPostTask;
import com.adi.exam.utils.TraceUtils;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.MODE_PRIVATE;

public class JIPMERSemplates extends ParentFragment implements View.OnClickListener, IItemHandler, IFileUploadCallback {

    private ParentFragment.OnFragmentInteractionListener mFragListener;

    private View layout;

    private SriVishwa activity;
    private Dialog mDialog;
    private RecyclerView rv_ques_nums;

    private QuestionNumberListingAdapter adapter;

    private TextView tv_questionno;

    private TextView tv_notvisitedcnt;

    private TextView tv_notansweredcnt;

    private TextView tv_answeredcnt;

    private TextView tv_mfrcnt;

    private TextView tv_amfrcnt;

    private WebView iv_option1, iv_option2, iv_option3, iv_option4;

    private WebView iv_question, iv_questionimg;

    private int currentExamId = -1;

    private int question_no = 0;

    private RadioGroup rg_options;

    private JSONObject data = new JSONObject();

    private JSONArray array = new JSONArray();

    private ImageLoader imageLoader;

    private TextView tv_timer;

    private JSONObject json;

    private int check = 0;

    private long questionStartTime = 0;

    private FileOutputStream fos = null;

    private TabLayout tl_subjects;

    private long timeTaken4Question = 0;

    private Date edate, sdate;

    AssetManager assetManager;

    private static final String FILE_NAME = System.currentTimeMillis() + "_Result.txt";

    String path;

    private String PATH = Environment.getExternalStorageDirectory().toString();

    private final String IMGPATH = PATH + "/System/allFiles/";

    private boolean isVisible = false;

    JSONObject studentDetails;

    public JIPMERSemplates() {
        // Required empty public constructor
    }

    public static JIPMERSemplates newInstance(String data) {

        JIPMERSemplates fragment = new JIPMERSemplates();

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

        layout = inflater.inflate(R.layout.fragment_kvpyttemplates, container, false);

        activity.getSupportActionBar().setHomeButtonEnabled(false);

        tv_timer = layout.findViewById(R.id.tv_timer);

        rg_options = layout.findViewById(R.id.rg_options);

        iv_question = layout.findViewById(R.id.iv_question);

        iv_questionimg = layout.findViewById(R.id.iv_questionimg);

        iv_option1 = layout.findViewById(R.id.iv_option1);

        iv_option2 = layout.findViewById(R.id.iv_option2);

        iv_option3 = layout.findViewById(R.id.iv_option3);

        iv_option4 = layout.findViewById(R.id.iv_option4);

        tv_questionno = layout.findViewById(R.id.tv_questionno);

        tv_notvisitedcnt = layout.findViewById(R.id.tv_notvisitedcnt);

        tv_notansweredcnt = layout.findViewById(R.id.tv_notansweredcnt);

        tv_answeredcnt = layout.findViewById(R.id.tv_answeredcnt);

        tv_mfrcnt = layout.findViewById(R.id.tv_mfrcnt);

        tv_amfrcnt = layout.findViewById(R.id.tv_amfrcnt);

        layout.findViewById(R.id.tv_savennext).setOnClickListener(this);

        layout.findViewById(R.id.tv_savennext).setVisibility(View.GONE);

        layout.findViewById(R.id.profile).setVisibility(View.VISIBLE);

        ((TextView) layout.findViewById(R.id.user_name)).setText(studentDetails.optString("student_name"));

        // layout.findViewById(R.id.tv_savenmarkforreview).setOnClickListener(this);

        layout.findViewById(R.id.tv_clearresponse).setOnClickListener(this);

        layout.findViewById(R.id.tv_mfrn).setOnClickListener(this);

        // layout.findViewById(R.id.tv_back).setOnClickListener(this);

        layout.findViewById(R.id.tv_submit).setOnClickListener(this);

        //   layout.findViewById(R.id.tv_next).setOnClickListener(this);

        tl_subjects = layout.findViewById(R.id.tl_subjects);

        adapter = new QuestionNumberListingAdapter(activity);

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

        String subjectsArray[];

        String subjects = data.optString("subjects").trim();

        if (subjects.contains(",")) {

            subjectsArray = subjects.split(",");

        } else {

            subjectsArray = new String[]{subjects};

        }

        for (int i = 0; i < subjectsArray.length; i++) {

            String subject = subjectsArray[i];

            TextView textView = (TextView) View.inflate(activity, R.layout.tab_subjects, null);

            textView.setText(subject);

            tl_subjects.addTab(tl_subjects.newTab().setCustomView(textView));

        }
        tl_subjects.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int tabPosition = tab.getPosition();

                if (tabPosition == 0) {

                    updateQuestionTime();

                    showNextQuestion(0);

                    return;

                }

                String noOfQuestions = data.optString("no_of_questions");

                if (noOfQuestions.contains(",")) {

                    String temp1[] = noOfQuestions.split(",");

                    int questionIndex = 0;

                    for (int i = 0; i < tabPosition; i++) {

                        questionIndex = questionIndex + Integer.parseInt(temp1[i]);

                    }

                    updateQuestionTime();

                    showNextQuestion(questionIndex);

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
//        String  dat=data.optString("duration").trim();
//        long time = (long) Double.parseDouble(dat);
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

                activity.setAllQuestions(adapter.getItems());

              //  activity.showAllQuestions();

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

                    long timeTaken = 0;

                    JSONObject jsonObject1 = jsonArray.getJSONObject(jsonArray.length() - 1);

                    String question_time = jsonObject1.optString("question_time").trim();

                    if (question_time.length() > 0) {

                        timeTaken = Long.parseLong(question_time);

                    }

                    timeTaken = timeTaken + timeTaken4Question;
                    jsonObject1.put("question_time", timeTaken);
                    jsonObject1.put("given_option", jsonObject.optString("qanswer"));
                    jsonObject1.put("correct_option", jsonObject.optString("answer"));

                    String res = "";


                    if (TextUtils.isEmpty(jsonObject.optString("qanswer"))) {
                        res = "2";
                    } else if (jsonObject.optString("qanswer").equalsIgnoreCase(jsonObject.optString("answer"))) {
                        res = "0";
                    } else {
                        res = "1";
                    }
                    jsonObject1.put("result", res);
                    jsonObject1.put("question_time", timeTaken4Question + "");
                    jsonObject1.put("no_of_clicks", check++);
                    jsonObject1.put("marked_for_review", jsonObject.optInt("qstate") == 3 ? "1" : "0");

                    iwhereClause = "exam_id = '" + data.optString("exam_id") + "' AND question_id = '" + jsonObject.optInt("question_id") + "' AND student_question_time_id = '" + jsonObject1.optInt("student_question_time_id") + "'";

                    table.checkNInsertARecord(jsonObject1, "STUDENTQUESTIONTIME", iwhereClause);

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
                String res = "";


                if (TextUtils.isEmpty(jsonObject.optString("qanswer"))) {
                    res = "2";
                } else if (jsonObject.optString("qanswer").equalsIgnoreCase(jsonObject.optString("answer"))) {
                    res = "0";
                } else {
                    res = "1";
                }
                questionTimeObject.put("result", res);
                questionTimeObject.put("question_time", timeTaken4Question + "");
                questionTimeObject.put("no_of_clicks", check++);
                questionTimeObject.put("marked_for_review", jsonObject.optInt("qstate") == 3 ? "1" : "0");

                table.insertSingleRecords(questionTimeObject, "STUDENTQUESTIONTIME");
                check = 0;

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
                    // question_no = position + 1;
                    currentExamId = position;
                    JSONObject jsonObject = adapter.getItems().getJSONObject(position);
                    if (jsonObject.optString("qstate").equalsIgnoreCase("2")) {
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_answered));
                        //  v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_answered));
                    } else if (jsonObject.optString("qstate").equalsIgnoreCase("0")) {
                        rg_options.clearCheck();
                        jsonObject.put("qstate", 1);
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_not_answered));
                        // v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_not_answered));
                    } else if (jsonObject.optString("qstate").equalsIgnoreCase("3")) {
                        jsonObject.put("qstate", 3);
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_marked_for_review));
                        // v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_marked_for_review));
                    } else if (jsonObject.optString("qstate").equalsIgnoreCase("4")) {
                        jsonObject.put("qstate", 4);
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_answered_marked));
                        // v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_answered_marked));
                    } else {
                        rg_options.clearCheck();
                        jsonObject.put("qstate", 1);
                        v.findViewById(R.id.tv_questionno).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_not_answered));
                        //  v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_not_answered));
                    }
                    adapter.notifyItemChanged(position);

                    // updateQuestionTime();

                    showNextQuestion(position);

                    break;

                case R.id.tv_savennext:

                    if (currentExamId >= adapter.getCount()) {
                        currentExamId=adapter.getCount()-1;

                        int selRatioId = rg_options.getCheckedRadioButtonId();
                        if (selRatioId == -1) {

                            activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psao));

                            return;
                        }
                        jsonObject = adapter.getItems().getJSONObject(currentExamId);
                        jsonObject.put("qstate", 2);
                        Object vv = layout.findViewById(selRatioId).getTag();
                        jsonObject.put("qanswer", vv.toString());
                        adapter.notifyItemChanged(currentExamId);

                        updateQuestionTime();
                        showNextQuestion(currentExamId);
                        return;
                    }
                    if (currentExamId != -1) {
                        //  question_no++;

                        int selRatioId = rg_options.getCheckedRadioButtonId();

                        if (selRatioId == -1) {

                            activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psao));

                            return;
                        }

                        jsonObject = adapter.getItems().getJSONObject(currentExamId);

                        jsonObject.put("qstate", 2);
                        Object vv = layout.findViewById(selRatioId).getTag();
                        jsonObject.put("qanswer", vv.toString());

                        adapter.notifyItemChanged(currentExamId);


                        updateQuestionTime();
                        if (jsonObject.optInt("sno") < adapter.getItemCount()) {
                            rg_options.clearCheck();

                        }
                        if (currentExamId == adapter.getCount()) {
                            showNextQuestion(currentExamId - 1);
                        } else {
                            showNextQuestion(currentExamId + 1);
                        }


                    }

                    break;

                case R.id.tv_savenmarkforreview:

                    if (currentExamId >= adapter.getCount()) {
                        currentExamId=adapter.getCount()-1;
                        int selRatioId = rg_options.getCheckedRadioButtonId();
                        if (selRatioId == -1) {

                            activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psao));

                            return;
                        }
                        jsonObject = adapter.getItems().getJSONObject(currentExamId);
                        jsonObject.put("qstate", 4);
                        jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());

                        adapter.notifyItemChanged(currentExamId);

                        rg_options.clearCheck();

                        updateQuestionTime();
                        showNextQuestion(currentExamId);
                        return;
                    }

                    if (currentExamId != -1) {
                        int selRatioId = rg_options.getCheckedRadioButtonId();

                        if (selRatioId == -1) {

                            activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.alert), activity.getString(R.string.psao));

                            return;
                        }

                        jsonObject = adapter.getItems().getJSONObject(currentExamId);

                        jsonObject.put("qstate", 4);

                        jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());

                        adapter.notifyItemChanged(currentExamId);

                        rg_options.clearCheck();

                        updateQuestionTime();
                        if (currentExamId == adapter.getCount()) {
                            showNextQuestion(currentExamId - 1);
                        } else if (currentExamId > adapter.getCount()) {
                            Toast.makeText(activity, "Are you finished your exam..", Toast.LENGTH_SHORT).show();
                        } else {
                            showNextQuestion(currentExamId + 1);
                        }

                    }
                    break;

                case R.id.tv_clearresponse:

                    if (currentExamId>adapter.getCount()){
                        currentExamId=adapter.getCount();
                        return;
                    }
                    if (currentExamId == adapter.getCount()) {
                        rg_options.clearCheck();
                        jsonObject = adapter.getItems().getJSONObject(currentExamId - 1);
                        jsonObject.put("qstate", 1);
                        jsonObject.put("qanswer", "");

                        adapter.notifyItemChanged(currentExamId);
                        updateQuestionTime();
                        showNextQuestion(currentExamId);
                        return;
                    }
                    rg_options.clearCheck();
                    //  }
                    jsonObject = adapter.getItems().getJSONObject(currentExamId);
                    jsonObject.put("qstate", 1);
                    jsonObject.put("qanswer", "");

                    adapter.notifyItemChanged(currentExamId);
                    updateQuestionTime();

                    break;

                case R.id.tv_mfrn:


                    if (currentExamId >= adapter.getCount()) {
                        currentExamId=adapter.getCount();

                            int selRatioId = rg_options.getCheckedRadioButtonId();

                            jsonObject = adapter.getItems().getJSONObject(currentExamId);

                            jsonObject.put("qstate", 4);

                            jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());

                            adapter.notifyItemChanged(currentExamId);

                            rg_options.clearCheck();

                            updateQuestionTime();

                            showNextQuestion(currentExamId);
                        return;
                    }
                    if (currentExamId != -1) {

                        //question_no++;
                        int selRatioId = rg_options.getCheckedRadioButtonId();

                        jsonObject = adapter.getItems().getJSONObject(currentExamId);

                        jsonObject.put("qstate", 3);

                        jsonObject.put("qanswer", layout.findViewById(selRatioId).getTag());

                        adapter.notifyItemChanged(currentExamId);
//                        if (jsonObject.optString("qstate").equalsIgnoreCase("1")) {
//                            rg_options.clearCheck();
//                        }
                        rg_options.clearCheck();

                        updateQuestionTime();

                        showNextQuestion(currentExamId + 1);

                    }

                    break;

                case R.id.tv_back:

                    if (currentExamId == -1) {
                        currentExamId = 0;
                        showNextQuestion(currentExamId);
                        return;
                    }
                    if (currentExamId == 0) {
                        showNextQuestion(currentExamId);
                        return;
                    }

                    if (currentExamId >= adapter.getCount()) {
                        currentExamId=adapter.getCount()-1;
                        jsonObject = adapter.getItems().getJSONObject(currentExamId);
                        if (jsonObject.optString("qstate").equalsIgnoreCase("1")) {
                            rg_options.clearCheck();
                            jsonObject.put("qstate", 1);
                            jsonObject.put("qanswer", "");
                        }
                        adapter.notifyItemChanged(currentExamId);
                        updateQuestionTime();
                        showNextQuestion( currentExamId);

                        return;
                    }


                    showNextQuestion(currentExamId - 1);
                    jsonObject = adapter.getItems().getJSONObject(currentExamId);

                    if (jsonObject.optString("qstate").equalsIgnoreCase("1")) {
                        rg_options.clearCheck();
                        jsonObject.put("qstate", 1);
                        jsonObject.put("qanswer", "");
                    }

                    adapter.notifyItemChanged(currentExamId);
                    updateQuestionTime();

                    if (currentExamId == 0) {
                        jsonObject = adapter.getItems().getJSONObject(currentExamId);
                        if (jsonObject.optString("qstate").equalsIgnoreCase("1")) {
                            rg_options.clearCheck();

                            jsonObject.put("qstate", 1);
                            jsonObject.put("qanswer", "");
                            currentExamId = 0;//this one
                        }
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

                case R.id.tv_next:


                    if (currentExamId >= adapter.getCount()) {
                        currentExamId= adapter.getCount();

                        jsonObject = adapter.getItems().getJSONObject(currentExamId);

                        if (jsonObject.optString("qstate").equalsIgnoreCase("0")) {

                            jsonObject.put("qstate", 1);

                        }

                        adapter.notifyItemChanged(currentExamId);
                        rg_options.clearCheck();
                        updateQuestionTime();
                        showNextQuestion(currentExamId);
                        return;
                    }
                    //question_no++;
                    jsonObject = adapter.getItems().getJSONObject(currentExamId);

                    if (jsonObject.optString("qstate").equalsIgnoreCase("0")) {

                        jsonObject.put("qstate", 1);

                    }

                    adapter.notifyItemChanged(currentExamId);
                    rg_options.clearCheck();
                    updateQuestionTime();

                    showNextQuestion(currentExamId + 1);
                    break;

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void showNextQuestion(int position) {

        try {

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

            tv_questionno.setText(getString(R.string.questionno, jsonObject.optString("sno")));

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
            //byte[] bytes = html_content.toString().getBytes();
            // String my_qs_file = new String(bytes, "UTF-8");
            String my_qs_file = AESEncryptionDecryption.decrypt(html_content.toString());
            String[] data_questions = my_qs_file.split("</html>");
            iv_question.clearHistory();
            iv_option1.clearHistory();
            iv_option2.clearHistory();
            iv_option3.clearHistory();
            iv_option4.clearHistory();
            iv_question.loadDataWithBaseURL("",data_questions[0], "text/html", "utf-8",null);
            iv_option1.loadDataWithBaseURL("",data_questions[1], "text/html", "utf-8",null);
            iv_option2.loadDataWithBaseURL("",data_questions[2], "text/html", "utf-8",null);
            iv_option3.loadDataWithBaseURL("",data_questions[3], "text/html", "utf-8",null);
            iv_option4.loadDataWithBaseURL("",data_questions[4], "text/html", "utf-8",null);



            if (jsonObject.optString("qanswer").equalsIgnoreCase("a")) {

                ((RadioButton) rg_options.findViewById(R.id.rb_first)).setChecked(true);

            } else if (jsonObject.optString("qanswer").equalsIgnoreCase("b")) {

                ((RadioButton) rg_options.findViewById(R.id.rb_second)).setChecked(true);

            } else if (jsonObject.optString("qanswer").equalsIgnoreCase("c")) {

                ((RadioButton) rg_options.findViewById(R.id.rb_third)).setChecked(true);

            } else if (jsonObject.optString("qanswer").equalsIgnoreCase("d")) {

                ((RadioButton) rg_options.findViewById(R.id.rb_fourth)).setChecked(true);

            }

            /*if (jsonObject.optInt("qanswer") == 1) {

                ((RadioButton) rg_options.findViewById(R.id.rb_first)).setChecked(true);

            } else if (jsonObject.optInt("qanswer") == 2) {

                ((RadioButton) rg_options.findViewById(R.id.rb_second)).setChecked(true);

            } else if (jsonObject.optInt("qanswer") == 3) {

                ((RadioButton) rg_options.findViewById(R.id.rb_third)).setChecked(true);

            } else if (jsonObject.optInt("qanswer") == 4) {

                ((RadioButton) rg_options.findViewById(R.id.rb_fourth)).setChecked(true);

            }*/

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

                        getQuestionsFromDBNShow(questions);

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

    private void getQuestionsFromDBNShow(String questions) {

        try {

            PhoneComponent phncomp = new PhoneComponent(this, activity, 2);

            String whereQuestions = "";

            if (questions.contains(",")) {

                String[] temp = questions.split(",");
                JSONArray array = new JSONArray();

                for (int i = 0; i < temp.length; i++) {
                    array.put(getQuestion(temp[i]));

                    // whereQuestions = whereQuestions + "'" + temp[i] + "',";

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

            int total_questions_attempted = 0;

            int no_of_correct_answers = 0;

            double marks_per_question = data.optDouble("marks_per_question");

            double negative_marks = data.optDouble("negative_marks");

            double score = 0;

            int total_not_answered = 0;

            int total_marked_for_review = 0;

            int total_not_visited = 0;

            int total_answered_and_marked_for_review = 0;

            int total_visited = 0;

            JSONArray jsonArray = adapter.getItems();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

//                json= new JSONObject();
//                json.put("student_question_time_id","");
//                json.put("student_id",activity.getStudentDetails().optInt("student_id"));
//                json.put("exam_id", data.optInt("exam_id"));
//                json.put("question_no",jsonObject.optString("sno"));
//                json.put("question_id",jsonObject.optString("question_id"));
//                json.put("topic_id",jsonObject.optString("topic_id"));
//                json.put("lesson_id","");
//                json.put("subject","");
//                json.put("given_option",jsonObject.optString("qstate"));
//                json.put("correct_option",jsonObject.optString("answer"));
//                json.put("result",jsonObject.optString("answer"));
//                json.put("question_time",60);
//                json.put("no_of_clicks","");
//                json.put("marked_for_review",jsonObject.optString("qstate"));
//                array.put(json);

                //qstate = //0 = not visited, 1 = not answered, 2 = answered, 3 = marked for review, 4 = answered and marked for review
                if (jsonObject.optString("qstate").equalsIgnoreCase("3")) {

                    ++total_marked_for_review;

                }

                if (jsonObject.optString("qstate").equalsIgnoreCase("0")) {

                    ++total_not_visited;

                }

                if (!jsonObject.optString("qstate").equalsIgnoreCase("0")) {

                    ++total_visited;

                }

                if (jsonObject.optString("qstate").equalsIgnoreCase("4")) {

                    ++total_answered_and_marked_for_review;

                }

                if (jsonObject.optString("qstate").equalsIgnoreCase("1")) {

                    ++total_not_answered;

                }

                String qanswer = jsonObject.optString("qanswer");

                String answer = jsonObject.optString("answer");

                if (qanswer.trim().length() > 0) {

                    ++total_questions_attempted;

                    if (qanswer.toLowerCase().equalsIgnoreCase(answer.toLowerCase())) {

                        ++no_of_correct_answers;

                        score = score + marks_per_question;

                    } else {

                        score = score - negative_marks;

                    }

                }

            }

            data.put("total_not_answered", total_not_answered);

            data.put("total_marked_for_review", total_marked_for_review);

            data.put("total_not_visited", total_not_visited);

            data.put("total_answered_and_marked_for_review", total_answered_and_marked_for_review);

            JSONObject question_details = data.getJSONObject("question_details");

            JSONObject STUDENTEXAMRESULT = new JSONObject();
            JSONObject backup_result = new JSONObject();
            long student_exam_result_id = System.currentTimeMillis();
//
//            int student_exam_result_id = AppPreferences.getInstance(activity).getIntegerFromStore("student_exam_result_id");
//
//            AppPreferences.getInstance(activity).addIntegerToStore("student_exam_result_id", ++student_exam_result_id, false);

            STUDENTEXAMRESULT.put("student_exam_result_id", student_exam_result_id);
            STUDENTEXAMRESULT.put("student_id", activity.getStudentDetails().optInt("student_id"));
            STUDENTEXAMRESULT.put("exam_id", data.optInt("exam_id"));
            STUDENTEXAMRESULT.put("exam_name", data.optString("exam_name"));
            STUDENTEXAMRESULT.put("exam_date", question_details.optString("exam_date"));
            //STUDENTEXAMRESULT.put("total_questions", data.optString("no_of_questions"));
            STUDENTEXAMRESULT.put("total_questions", adapter.getCount() + "");
            STUDENTEXAMRESULT.put("total_questions_attempted", total_questions_attempted + "");
            STUDENTEXAMRESULT.put("no_of_correct_answers", no_of_correct_answers + "");
            STUDENTEXAMRESULT.put("score", score + "");
            STUDENTEXAMRESULT.put("percentage", "");
            STUDENTEXAMRESULT.put("accuracy", "");
            STUDENTEXAMRESULT.put("exam_type", "");


//            backup_result.put("student_exam_result_id", student_exam_result_id);
//            backup_result.put("student_id", activity.getStudentDetails().optInt("student_id"));
//            backup_result.put("exam_id", data.optInt("exam_id"));
//            backup_result.put("exam_name", data.optString("exam_name"));
//            backup_result.put("exam_date", question_details.optString("exam_date"));
//            backup_result.put("total_questions", adapter.getCount() + "");
//            backup_result.put("total_questions_attempted", total_questions_attempted + "");
//            backup_result.put("no_of_correct_answers", no_of_correct_answers + "");
//            backup_result.put("score", score + "");
//            backup_result.put("percentage", "");
//            backup_result.put("accuracy", "");
//            backup_result.put("exam_type", "");


            App_Table table = new App_Table(activity);

            json = table.getExamsResult(data.optInt("exam_id"), activity.getStudentDetails().optInt("student_id"));
            json.put("student_exam_result_id", student_exam_result_id);
            json.put("student_id", activity.getStudentDetails().optInt("student_id"));
            json.put("exam_id", data.optInt("exam_id"));
            json.put("exam_name", data.optString("exam_name"));
            json.put("exam_date", question_details.optString("exam_date"));
            json.put("total_questions", adapter.getCount() + "");
            json.put("total_questions_attempted", total_questions_attempted + "");
            json.put("no_of_correct_answers", no_of_correct_answers + "");
            json.put("score", score + "");
            json.put("percentage", "");
            json.put("accuracy", "");
            json.put("exam_type", "");
//            array.put(json);

            //backup_result.put("student_question_time",array);

            fos = getActivity().openFileOutput(FILE_NAME, MODE_PRIVATE);

            fos.write(json.toString().getBytes());

            String path = getActivity().getFilesDir().getAbsolutePath() + "/" + FILE_NAME;

            long val = table.insertSingleRecords(STUDENTEXAMRESULT, "STUDENTEXAMRESULT");

            if (val > 0) {

                activity.setAllQuestions(jsonArray);

                activity.showExamSubmitConfirmationPage(data, student_exam_result_id, 1);

                startUploadBackUp(path, FILE_NAME);

                return;

            }

            activity.showokPopUp(R.drawable.pop_ic_failed, activity.getString(R.string.errorTxt), activity.getString(R.string.isr));

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

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

            post.userRequest(getString(R.string.plwait), 2, "submit_exam_result", jsonObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
    }

    public JSONObject getQuestion(String qid) {
        JSONObject obj = new JSONObject();
        try {
            Database database = new Database(activity);
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