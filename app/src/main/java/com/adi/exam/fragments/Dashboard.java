package com.adi.exam.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.MyItemRecyclerViewAdapter;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Dashboard extends ParentFragment {

    private OnListFragmentInteractionListener mListener;

    private OnFragmentInteractionListener mFragListener;

    private SriVishwa activity;

    public Dashboard() {

    }

    public static Dashboard newInstance() {
        return new Dashboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Context context = view.getContext();

        RecyclerView rv_list = view.findViewById(R.id.rv_list);

        rv_list.setNestedScrollingEnabled(false);

        try {

            JSONArray jsonArray = new JSONArray();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", activity.getString(R.string.newexam));
            jsonObject.put("colorId", R.color.newexam);
            jsonObject.put("position", 1);
            jsonArray.put(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("title", activity.getString(R.string.examhistory));
            jsonObject.put("colorId", R.color.historyexam);
            jsonObject.put("position", 2);
            jsonArray.put(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("title", activity.getString(R.string.newassignment));
            jsonObject.put("colorId", R.color.newassignment);
            jsonObject.put("position", 3);
            jsonArray.put(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("title", activity.getString(R.string.assignmenthistory));
            jsonObject.put("colorId", R.color.historyassignment);
            jsonObject.put("position", 4);
            jsonArray.put(jsonObject);

            rv_list.setLayoutManager(new GridLayoutManager(context, 2));

            rv_list.setAdapter(new MyItemRecyclerViewAdapter(activity, jsonArray, mListener));

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (OnListFragmentInteractionListener) context;

        mFragListener = (SriVishwa) context;

        activity = (SriVishwa) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mFragListener = null;
    }

    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(JSONObject item);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        mFragListener.onFragmentInteraction(R.string.dashboard, true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragListener.onFragmentInteraction(R.string.dashboard, true);

    }
    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getActivity()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getActivity().getTaskId(), 0);
    }
}
