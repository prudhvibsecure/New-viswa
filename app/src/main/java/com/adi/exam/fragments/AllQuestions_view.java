package com.adi.exam.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.AllQuestionsAdapter;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class AllQuestions_view extends ParentFragment {

    private OnFragmentInteractionListener mFragListener;

    private ProgressBar progressBar;

    private TextView tv_content_txt;

    private SriVishwa activity;

    private AllQuestionsAdapter adapterContent;
    private JSONArray data;

    public AllQuestions_view() {
        // Required empty public constructor
    }

    public static AllQuestions_view newInstance(String allQus) {

        AllQuestions_view allQuestions_view=new AllQuestions_view();
        Bundle args = new Bundle();

        args.putString("data", allQus);

        allQuestions_view.setArguments(args);
        return allQuestions_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            try {

                data = new JSONArray(getArguments().getString("data"));

            } catch (Exception e) {

                TraceUtils.logException(e);

            }

        }


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
       SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
       mSwipeRefreshLayout.setEnabled(false);
       mSwipeRefreshLayout.setRefreshing(false);
        rv_content_list.setLayoutManager(layoutManager);

        rv_content_list.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration did = new DividerItemDecoration(rv_content_list.getContext(), layoutManager.getOrientation());

        rv_content_list.addItemDecoration(did);

        adapterContent = new AllQuestionsAdapter(activity);

        rv_content_list.setAdapter(adapterContent);
        progressBar.setVisibility(View.VISIBLE);
        showData();

        return layout;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mFragListener = (OnFragmentInteractionListener) context;

        activity = (SriVishwa) context;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        mFragListener.onFragmentInteraction(R.string.aq, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragListener.onFragmentInteraction(R.string.aq, false);

    }

    @Override
    public void onDetach() {
        super.onDetach();

        mFragListener = null;

    }

    private void showData() {

        try {

            JSONArray jsonArray = data;

            if (jsonArray != null && jsonArray.length() > 0) {

                tv_content_txt.setVisibility(View.GONE);

                progressBar.setVisibility(View.GONE);

                adapterContent.setItems(jsonArray);

                adapterContent.notifyDataSetChanged();

                return;

            }

            tv_content_txt.setVisibility(View.VISIBLE);

        } catch (Exception e) {

            TraceUtils.logException(e);

        }


    }


}
