package com.adi.exam.dialogfragments;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adi.exam.R;
import com.adi.exam.callbacks.IDialogCallbacks;
import com.adi.exam.utils.TraceUtils;

public class MessageDialog extends DialogFragment {

    private IDialogCallbacks mCallbacks;

    public static MessageDialog newInstance(Bundle bundle) {

        MessageDialog messageDialog = new MessageDialog();

        messageDialog.setArguments(bundle);

        return messageDialog;

    }

    public void setIDialogListener(IDialogCallbacks aCallbacks) {

        mCallbacks = aCallbacks;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Bundle mArgs = getArguments();

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

            getDialog().setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    return keyCode == 4;

                }

            });

        }

        ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(getInt("drawableId", mArgs, R.mipmap.ic_launcher));

        ((TextView) view.findViewById(R.id.tv_title)).setText(getString("title", "", mArgs));

        ((TextView) view.findViewById(R.id.tv_message)).setText(Html.fromHtml(getString("message", "", mArgs)));

        String cancelBut = getString("cancelBut", "", mArgs);

        if (cancelBut.length() > 0) {

            ((TextView) view.findViewById(R.id.tv_cancel)).setText(cancelBut);

        }

        String activateBut = getString("activateBut", "", mArgs);

        if (activateBut.length() > 0) {

            ((TextView) view.findViewById(R.id.tv_ok)).setText(activateBut);

        }

        view.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mCallbacks != null) {

                    mCallbacks.onOK(getInt("requestId", mArgs, 0));

                }

                MessageDialog.this.dismiss();

            }

        });

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mCallbacks != null) {

                    mCallbacks.onCancel(getInt("requestId", mArgs, 0));

                }

                MessageDialog.this.dismiss();

            }

        });

        view.findViewById(R.id.tv_cancel).setVisibility(getBoolean("showcancel", mArgs) ? View.VISIBLE : View.GONE);

        return view;
    }

    private String getString(String key, String defaultValue, Bundle bundle) {

        if (bundle == null)
            return defaultValue;

        String s = bundle.getString(key);

        return (s == null) ? defaultValue : s;

    }

    private int getInt(String key, Bundle bundle, int defaultValue) {

        if (bundle == null)
            return 0;

        return bundle.getInt(key, defaultValue);

    }

    private boolean getBoolean(String key, Bundle bundle) {

        return bundle != null && bundle.getBoolean(key, false);

    }

}
