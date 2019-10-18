package com.adi.exam.controls;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.adi.exam.R;
import com.adi.exam.services.DownloadService;

import org.json.JSONObject;

import java.util.Locale;

public class ProgressControl extends LinearLayout {

    private Context mContext = null;
    private TextView textView = null, tvCancel = null;
    private ProgressBar pbar = null;
    private String text = "";

    private String downloadCount = "";

    private String cid;

    private JSONObject jsonObject;

    public ProgressControl(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ProgressControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void init() {

        this.setOrientation(LinearLayout.VERTICAL);

        textView = new TextView(mContext);
        textView.setText(R.string.downloading);
        textView.setTextSize(12f);
        textView.setTextColor(Color.RED);
        textView.setSingleLine(true);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 10, 0, 0);
        textView.setLayoutParams(params);
        this.addView(textView);

        pbar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);

        params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        params.setMargins(2, 10, 2, 0);
        pbar.setLayoutParams(params);
        pbar.setMax(100);
        this.addView(pbar);

        pbar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.apptheme_scrubber_progress_horizontal_holo_light_yellow));

        tvCancel = new TextView(mContext);
        tvCancel.setText(R.string.cancel);
        tvCancel.setTextSize(12f);
        tvCancel.setTextColor(Color.WHITE);
        tvCancel.setSingleLine(true);
        tvCancel.setGravity(Gravity.CENTER);

        tvCancel.setBackgroundResource(R.drawable.template_pop_but_def_blue);

        LayoutParams cancelparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        cancelparams.setMargins(2, 20, 10, 0);
        cancelparams.gravity = Gravity.RIGHT;
        tvCancel.setPadding(5, 5, 5, 5);
        tvCancel.setLayoutParams(cancelparams);
        tvCancel.setOnClickListener(onClickListener);
        this.addView(tvCancel);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMaxProgress(int max) {
        pbar.setMax(max);
    }

    public void setCurrentProgress(int current) {
        pbar.setProgress(current);
    }

    public void updateProgressState(Long... values) {

        double total = values[2] / 1024.0;
        setMaxProgress((int) total);
        total = total / 1024.0;

        double received = values[1] / 1024.0;
        setCurrentProgress((int) received);
        received = received / 1024.0;

        String rec = (String.format(Locale.ENGLISH, "%.02f", received));
        String tot = (String.format(Locale.ENGLISH, "%.02f", total));
        String recieved = rec.replace(",", ".");
        String total_ = tot.replace(",", ".");

        textView.setText(mContext.getString(R.string.downloading) + "  " + text
                + "   " + recieved + " MB read / " + total_ + " MB" + "     "
                + downloadCount + "  " + msToString(values[4]));
    }

    public void clear() {
        textView.setText(R.string.downloading);
        pbar.setMax(0);
        pbar.setProgress(0);
    }

    private String msToString(long ms) {
        long totalSecs = ms / 1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;
        String minsString = (mins == 0) ? "00" : ((mins < 10) ? "0" + mins : ""
                + mins);
        String secsString = (secs == 0) ? "00" : ((secs < 10) ? "0" + secs : ""
                + secs);
        if (hours > 0)
            return hours + ":" + minsString + ":" + secsString;
        else if (mins > 0)
            return mins + ":" + secsString
                    + mContext.getString(R.string.minlft);
        else
            return secsString + mContext.getString(R.string.seclft);
    }

    public void setCancelEventId(String cid) {
        this.cid = cid;
    }

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {

            Intent downloadIntent = new Intent(mContext, DownloadService.class);
            downloadIntent.setAction("com.ooredoo.music.action.removecontent");
            downloadIntent.putExtra("requestId", Integer.parseInt(cid));
            mContext.startService(downloadIntent);

        }
    };

    public void setData(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }
}
