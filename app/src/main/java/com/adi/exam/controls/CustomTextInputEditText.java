package com.adi.exam.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;

import com.adi.exam.R;
import com.adi.exam.common.AppFonts;
import com.google.android.material.textfield.TextInputEditText;

public class CustomTextInputEditText extends TextInputEditText {

    public CustomTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomTextView);

        int fontType = a.getInteger(R.styleable.CustomTextView_fontType, -1);
        a.recycle();

        switch (fontType) {

            case 1:
                this.setTypeface(AppFonts.getInstance(context).getBahnschrift());
                break;

            case 2:
                this.setTypeface(AppFonts.getInstance(context).getSwissbold(),
                        Typeface.BOLD);
                break;

            default:
                break;
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public int getAutofillType() {
        return AUTOFILL_TYPE_NONE;
    }
}
