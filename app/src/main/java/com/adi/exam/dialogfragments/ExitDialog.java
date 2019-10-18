package com.adi.exam.dialogfragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.utils.TraceUtils;

public class ExitDialog extends DialogFragment {

    private SriVishwa activity = null;

    public static ExitDialog newInstance() {
        return new ExitDialog();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.activity = (SriVishwa) context;

    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);

        this.activity = (SriVishwa) activity;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_message_ok, container, false);

        if (getDialog() != null) {

            Window window = getDialog().getWindow();

            if (window != null) {

                window.requestFeature(Window.FEATURE_NO_TITLE);

                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                window.getAttributes().windowAnimations = R.style.DialogAnimation;

            }

            getDialog().setCanceledOnTouchOutside(false);

            getDialog().setCancelable(false);

            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    return keyCode == 4;

                }

            });

        }

        ((TextView) view.findViewById(R.id.tv_title)).setText(R.string.nonettitle);

        ((TextView) view.findViewById(R.id.tv_message)).setText(R.string.exitmsg);

        view.findViewById(R.id.tv_cancel).setVisibility(View.VISIBLE);

        view.findViewById(R.id.tv_ok).setOnClickListener(activity);

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ExitDialog.this.dismiss();

            }

        });

        return view;
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {

        try {

            FragmentTransaction ft = manager.beginTransaction();

            ft.add(this, tag);

            ft.commitAllowingStateLoss();

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

}
