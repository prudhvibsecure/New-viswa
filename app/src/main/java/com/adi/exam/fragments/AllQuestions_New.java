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

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.AllQuestionsAdapter;
import com.adi.exam.models.QuestonsModel;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;

import java.util.ArrayList;

public class AllQuestions_New extends ParentFragment {

    private OnFragmentInteractionListener mFragListener;

    private ProgressBar progressBar;

    private TextView tv_content_txt;

    private SriVishwa activity;

    private AllQuestionsAdapter adapterContent;
    private JSONArray data;

    private ArrayList<QuestonsModel> questonsModels;
    public AllQuestions_New() {
        // Required empty public constructor
    }

    public static AllQuestions_New newInstance(String allQus) {

        AllQuestions_New allQuestions_view=new AllQuestions_New();
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

        View layout = inflater.inflate(R.layout.question_list_layout, container, false);

      //  progressBar = layout.findViewById(R.id.pb_content_bar);

       // tv_content_txt = layout.findViewById(R.id.tv_content_txt);

       // tv_content_txt.setText(R.string.cydhaen);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        RecyclerView rv_content_list = layout.findViewById(R.id.rv_content_list);
//       SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
//       mSwipeRefreshLayout.setEnabled(false);
//       mSwipeRefreshLayout.setRefreshing(false);
        rv_content_list.setLayoutManager(layoutManager);

        rv_content_list.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration did = new DividerItemDecoration(rv_content_list.getContext(), layoutManager.getOrientation());

        rv_content_list.addItemDecoration(did);

        adapterContent = new AllQuestionsAdapter(activity);

        rv_content_list.setAdapter(adapterContent);
       // progressBar.setVisibility(View.VISIBLE);
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



                return;

            }


        } catch (Exception e) {

            TraceUtils.logException(e);

        }


    }


}
