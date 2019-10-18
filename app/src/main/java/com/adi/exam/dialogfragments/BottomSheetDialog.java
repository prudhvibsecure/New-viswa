package com.adi.exam.dialogfragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.utils.TraceUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private JSONObject jsonObject;

    private SriVishwa activity;

    public static BottomSheetDialog newInstance(Bundle bundle) {

        BottomSheetDialog playlistDialog = new BottomSheetDialog();
        playlistDialog.setArguments(bundle);
        return playlistDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (SriVishwa) context;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = (SriVishwa) activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.requestFeature(Window.FEATURE_NO_TITLE);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        View optionsDialog = inflater.inflate(R.layout.dialog_survey, container, false);

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().setCancelable(true);

        ((TextView) optionsDialog.findViewById(R.id.tv_title)).setText(jsonObject.optString("alert"));

        ((TextView) optionsDialog.findViewById(R.id.tv_action)).setText(jsonObject.optString("buttontitle"));

        optionsDialog.findViewById(R.id.tv_action).setTag(jsonObject);

        optionsDialog.findViewById(R.id.tv_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.onClick(v);
                BottomSheetDialog.this.dismiss();
            }
        });

        optionsDialog.findViewById(R.id.tv_notnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog.this.dismiss();
            }
        });

        return optionsDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        try {
            Bundle mArgs = getArguments();
            if (mArgs != null) {
                jsonObject = new JSONObject(mArgs.getString("data"));
            }
        } catch (JSONException e) {
            TraceUtils.logException(e);
        }
    }

    public String getString(String key, String defaultValue, Bundle bundle) {
        if (bundle == null)
            return defaultValue;
        String s = bundle.getString(key);
        return (s == null) ? defaultValue : s;
    }

}

