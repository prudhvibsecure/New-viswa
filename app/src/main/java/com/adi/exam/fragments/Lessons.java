package com.adi.exam.fragments;

import android.app.Activity;
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

public class Lessons extends ParentFragment implements IItemHandler, View.OnClickListener {

    private View layout;

    private RecyclerView mRecyclerView;

    private ProgressBar progressBar;

    private TextView tv_content_txt;

    private OnFragmentInteractionListener mListener;

    private MaterialAdapter adapter;

    private SriVishwa mActivity;

    private JSONObject jsonObject;


    public Lessons() {
        // Required empty public constructor
    }

    public static Lessons newInstance(String data) {

        Lessons fragment = new Lessons();

        Bundle bundle = new Bundle();

        bundle.putString("data", data);

        fragment.setArguments(bundle);

        return fragment;

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

        try {

            if (getArguments() != null) {

                String data = getArguments().getString("data");

                jsonObject = new JSONObject(data);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_examlist, container, false);

        progressBar = layout.findViewById(R.id.pb_content_bar);

        tv_content_txt = layout.findViewById(R.id.tv_content_txt);

        tv_content_txt.setText(R.string.nl);

        return layout;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onFragmentInteraction(R.string.lessons, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);

        mRecyclerView = layout.findViewById(R.id.rv_content_list);

        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new MaterialAdapter("lesson_name");

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

            phncomp.defineWhereClause("subject='" + jsonObject.optString("course_subject").trim() + "'");

            phncomp.executeLocalDBInBackground("LESSONS");


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

            mActivity.showTopics(adapter.getItems().getJSONObject(itemPosition).toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void parseData(Object results) throws Exception {

        if (results != null) {


            JSONArray array = new JSONArray(results.toString());

            if (array.length() > 0) {

                adapter.setItems(array);

                adapter.notifyDataSetChanged();

                return;

            }

        }

        tv_content_txt.setVisibility(View.VISIBLE);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        super.onHiddenChanged(hidden);

        if (!hidden)
            mListener.onFragmentInteraction(R.string.lessons, false);

    }


}
