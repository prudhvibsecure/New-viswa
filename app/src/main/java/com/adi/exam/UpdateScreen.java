package com.adi.exam;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adi.exam.fragments.ParentFragment;

import org.json.JSONObject;

public class UpdateScreen extends ParentFragment {
    private SriVishwa activity;
    private View layout;

    private OnFragmentInteractionListener mFragListener;

    public UpdateScreen() {
        // Required empty public constructor
    }
    public static UpdateScreen newInstance() {
        return new UpdateScreen();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.popup_appupdate, container, false);


        layout.findViewById(R.id.tv_updateok).setOnClickListener(activity);
        layout.findViewById(R.id.tv_updatecancel).setOnClickListener(activity);
        layout.findViewById(R.id.tv_updatecancel).setVisibility(View.GONE);
        return layout;
    }
    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getActivity()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(activity.getTaskId(), 0);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);



        mFragListener = (SriVishwa) context;

        activity = (SriVishwa) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
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
}
