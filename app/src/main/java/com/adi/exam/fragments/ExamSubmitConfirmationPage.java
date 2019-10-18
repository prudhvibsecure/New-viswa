package com.adi.exam.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.callbacks.IDialogCallbacks;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.database.PhoneComponent;
import com.adi.exam.utils.TraceUtils;
import com.google.android.gms.common.api.TransformedResult;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExamSubmitConfirmationPage extends ParentFragment implements IItemHandler, View.OnClickListener, IDialogCallbacks {


    private long student_exam_result_id = 0;

    private OnFragmentInteractionListener mFragListener;

    private JSONObject data = new JSONObject();

    private View layout;

    private SriVishwa activity;

    private int type = -1;

    public ExamSubmitConfirmationPage() {
        // Required empty public constructor
    }

    public static ExamSubmitConfirmationPage newInstance(String data, long student_exam_result_id, int type) { //type => 1 = exam, 2 = assignment
        ExamSubmitConfirmationPage frag = new ExamSubmitConfirmationPage();
        Bundle args = new Bundle();
        args.putString("data", data);
        args.putInt("type", type);
        args.putLong("student_exam_result_id", student_exam_result_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            if (getArguments() != null) {

                student_exam_result_id = getArguments().getLong("student_exam_result_id");

                data = new JSONObject(getArguments().getString("data"));

                type = getArguments().getInt("type");

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_exam_submitconfirmationpage, container, false);

        layout.findViewById(R.id.tv_no).setOnClickListener(this);

        layout.findViewById(R.id.tv_yes).setOnClickListener(this);

        try {

            if (type == 1) {

                PhoneComponent phncomp = new PhoneComponent(this, activity, 1);

                phncomp.defineWhereClause("student_exam_result_id = '" + student_exam_result_id + "' AND student_id = '" + activity.getStudentDetails().optInt("student_id") + "' AND exam_id = '" + data.optString("exam_id") + "'");

                phncomp.executeLocalDBInBackground("STUDENTEXAMRESULT");

            } else {

                PhoneComponent phncomp = new PhoneComponent(this, activity, 1);

                phncomp.defineWhereClause("assignment_result_id = '" + student_exam_result_id + "' AND student_id = '" + activity.getStudentDetails().optInt("student_id") + "'");

                phncomp.executeLocalDBInBackground("ASSIGNMENTRESULTS");


            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

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

        mFragListener.onFragmentInteraction(activity.getString(R.string.es), false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (data.optString("exam_name").trim().length() > 0) {
            mFragListener.onFragmentInteraction(activity.getString(R.string.es), false);
        } else {
            mFragListener.onFragmentInteraction(activity.getString(R.string.as), false);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        mFragListener = null;

    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

    }

    @Override
    public void onFinish(Object results, int requestId) {

        try {

            if (results != null) {

                JSONArray jsonArray = (JSONArray) results;

                JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);

                showExamSummary(jsonObject);

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onError(String errorCode, int requestId) {

    }

    private void showExamSummary(JSONObject jsonObject) {

        try {

            if (data.optString("exam_name").trim().length() > 0) {

                ((TextView) layout.findViewById(R.id.tv_sectionname)).setText("Exam Name");

                ((TextView) layout.findViewById(R.id.tv_sectionnameval)).setText(data.optString("exam_name"));

                ((TextView) layout.findViewById(R.id.tv_noofquestionsval)).setText(jsonObject.optString("total_questions"));

                ((TextView) layout.findViewById(R.id.tv_answeredval)).setText(jsonObject.optString("total_questions_attempted"));

                ((TextView) layout.findViewById(R.id.tv_notansweredval)).setText(data.optString("total_not_answered"));

                ((TextView) layout.findViewById(R.id.tv_mfrval)).setText(data.optString("total_marked_for_review"));

                ((TextView) layout.findViewById(R.id.tv_amfrval)).setText(data.optString("total_answered_and_marked_for_review"));

                ((TextView) layout.findViewById(R.id.tv_notvisitedval)).setText(data.optString("total_not_visited"));
            } else {
                ((TextView) layout.findViewById(R.id.tv_sectionname)).setText("Assignment Name");

                ((TextView) layout.findViewById(R.id.tv_sectionnameval)).setText(data.optString("assignment_name"));

                ((TextView) layout.findViewById(R.id.tv_noofquestionsval)).setText(jsonObject.optString("total_questions"));

                ((TextView) layout.findViewById(R.id.tv_answeredval)).setText(jsonObject.optString("total_questions_attempted"));

                ((TextView) layout.findViewById(R.id.tv_notansweredval)).setText(data.optString("total_not_answered"));

                ((TextView) layout.findViewById(R.id.tv_mfrval)).setText(data.optString("total_marked_for_review"));

                ((TextView) layout.findViewById(R.id.tv_amfrval)).setText(data.optString("total_answered_and_marked_for_review"));

                ((TextView) layout.findViewById(R.id.tv_notvisitedval)).setText(data.optString("total_not_visited"));

            }
            if (type == 2) {

                layout.findViewById(R.id.tv_mfr).setVisibility(View.GONE);

                layout.findViewById(R.id.tv_mfrval).setVisibility(View.GONE);

                layout.findViewById(R.id.tv_amfr).setVisibility(View.GONE);

                layout.findViewById(R.id.tv_amfrval).setVisibility(View.GONE);

            }


        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.tv_yes) {
            AppPreferences.getInstance(getActivity()).addToStore("exam_on","0",false);
            activity.showokPopUp(R.drawable.pop_ic_confirmation, activity.getString(R.string.confirmation), activity.getString(R.string.tyyrwbsffmcotcfs), 1, this, true);

        } else if (view.getId() == R.id.tv_no) {


            //activity.closeFrags();
            AppPreferences.getInstance(getActivity()).addToStore("exam_on","0",false);
            activity.swiftFragments(Dashboard.newInstance(),"DashBoard");


        }

    }


    @Override
    public void onOK(int requestId) {

        if (requestId == 1) {

            activity.showokPopUp(R.drawable.pop_ic_success, activity.getString(R.string.message), activity.getString(R.string.tyss), 2, activity.getString(R.string.viewresults), this, false);

        } else if (requestId == 2) {

            activity.onKeyDown(4, null);

            activity.showResults(data.toString(), student_exam_result_id, type);

        }

    }

    @Override
    public void onCancel(int requestId) {

    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getActivity()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getActivity().getTaskId(), 0);
    }
}
