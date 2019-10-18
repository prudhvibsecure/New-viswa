package com.adi.exam.dialogfragments;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adi.exam.R;
import com.adi.exam.utils.TraceUtils;

public class AppUpdateDialog extends DialogFragment {

    private IUpdateCallback mCallback;

    private String showskip = "";

    public static AppUpdateDialog newInstance(Bundle bundle) {

        AppUpdateDialog dialog = new AppUpdateDialog();

        dialog.setArguments(bundle);

        return dialog;

    }

    public void setIUpdateCallback(IUpdateCallback aCallback) {
        mCallback = aCallback;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_appupdate, container, false);

        Bundle bundle = getArguments();

        if (bundle != null) {

            showskip = bundle.getString("showskip");

        }

        if (getDialog() != null) {

            Window window = getDialog().getWindow();

            if (window != null) {

                window.requestFeature(Window.FEATURE_NO_TITLE);

                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                window.getAttributes().windowAnimations = R.style.DialogAnimation;

            }

            getDialog().setCanceledOnTouchOutside(false);

            getDialog().setCancelable(false);

            getDialog().setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    return keyCode == 4;

                }

            });

        }

        view.findViewById(R.id.tv_updateok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                processUserAction(showskip, view.getId());

            }

        });

        view.findViewById(R.id.tv_updatecancel).setVisibility(showskip.equalsIgnoreCase("y") ? View.VISIBLE : View.GONE);

        view.findViewById(R.id.tv_updatecancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                processUserAction(showskip, view.getId());

            }

        });

        return view;

    }

    private void processUserAction(String mandatory, int id) {

        AppUpdateDialog.this.dismiss();

        mCallback.onUserAction(mandatory, id);

    }

    public interface IUpdateCallback {

        void onUserAction(String mandatory, int id);

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
