package com.adi.exam.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.adi.exam.R;
import com.adi.exam.common.AppFonts;


public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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

}
