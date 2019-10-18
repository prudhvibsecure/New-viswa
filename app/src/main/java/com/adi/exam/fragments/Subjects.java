package com.adi.exam.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.MaterialAdapter;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.database.PhoneComponent;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Subjects extends ParentFragment implements IItemHandler, View.OnClickListener {

    private View layout;

    private RecyclerView mRecyclerView;

    private ProgressBar progressBar;

    private TextView tv_content_txt;

    private OnFragmentInteractionListener mListener;

    private MaterialAdapter adapter;

    private SriVishwa mActivity;

    private JSONObject subject;

    public Subjects() {
        // Required empty public constructor
    }

    public static Subjects newInstance() {
        return new Subjects();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (OnFragmentInteractionListener) context;

        mActivity = (SriVishwa) context;

    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);

        mListener = (OnFragmentInteractionListener) activity;

        mActivity = (SriVishwa) activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_examlist, container, false);

        progressBar = layout.findViewById(R.id.pb_content_bar);

        tv_content_txt = layout.findViewById(R.id.tv_content_txt);

        tv_content_txt.setText(R.string.ns);

        return layout;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onFragmentInteraction(mActivity.getString(R.string.subject), false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);

        mRecyclerView = layout.findViewById(R.id.rv_content_list);

        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new MaterialAdapter("title");

        adapter.setOnClickListener(this);

        mRecyclerView.setAdapter(adapter);

        getData();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    private void getData() {

        try {

            PhoneComponent phncomp = new PhoneComponent(this, mActivity, 1);
                String name =  mActivity.getStudentDetails().optString("program_name").trim();
            phncomp.defineWhereClause("course='" + mActivity.getStudentDetails().optString("course").trim() + "'");

            phncomp.executeLocalDBInBackground("COURSES");

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

    }

    @Override
    public void onFinish(Object results, int requestId) {

        progressBar.setVisibility(View.GONE);

        try {

            parseData(results);

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onError(String errorCode, int requestType) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {

        try {

            int itemPosition = mRecyclerView.getChildLayoutPosition(view);

            JSONObject jsonObject = adapter.getItems().getJSONObject(itemPosition);

            jsonObject.put("course_name", subject.optString("course_name"));

            jsonObject.put("course_subject", jsonObject.optString("title"));

            mActivity.showLessons(jsonObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void parseData(Object results) throws Exception {

        if (results != null) {

            JSONArray finalArray = new JSONArray();

            JSONArray array = new JSONArray(results.toString());

            if (array.length() > 0) {

                subject = array.getJSONObject(0);

                String subjects = subject.getString("course_subjects");

                String[] temp = subjects.split(",");


                for (int i = 0; i < temp.length; i++) {

                    JSONObject jsonObject1 = new JSONObject();

                    jsonObject1.put("title", temp[i].trim());

                    finalArray.put(jsonObject1);

                }

                if (finalArray.length() > 0) {

                    adapter.setItems(finalArray);

                    adapter.notifyDataSetChanged();

                    return;

                }

            }

        }

        tv_content_txt.setVisibility(View.VISIBLE);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            mListener.onFragmentInteraction(R.string.subject, false);
    }
    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getActivity()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getActivity().getTaskId(), 0);
    }
}
