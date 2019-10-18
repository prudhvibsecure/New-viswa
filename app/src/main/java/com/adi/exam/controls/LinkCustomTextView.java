package com.adi.exam.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;

import com.adi.exam.R;
import com.adi.exam.common.AppFonts;


public class LinkCustomTextView extends androidx.appcompat.widget.AppCompatTextView {

    public LinkCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LinkCustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        super.setText(content, type);
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
