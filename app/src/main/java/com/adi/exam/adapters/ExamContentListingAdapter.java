package com.adi.exam.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.common.ColorGenerator;
import com.adi.exam.controls.TextDrawable;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExamContentListingAdapter extends RecyclerView.Adapter<ExamContentListingAdapter.ContactViewHolder> {

    private JSONArray array = new JSONArray();

    private OnClickListener onClickListener;

    private TextDrawable.IBuilder builder;

    private ColorGenerator generator;

    private Context mContext;

    public ExamContentListingAdapter(Context context) {

        generator = ColorGenerator.MATERIAL;

        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();

        mContext = context;

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int position) {

        try {

            JSONObject jsonObject = array.getJSONObject(position);

            String title = jsonObject.optString("exam_name").trim();

            contactViewHolder.tv_title.setText(title);

            contactViewHolder.tv_mins.setText(mContext.getString(R.string.hours, jsonObject.optString("duration").trim()));

            String noOfQuestions = jsonObject.optString("no_of_questions");

            int totalQuestions = 0;

            if(noOfQuestions.contains(",")) {

                String temp1[] = noOfQuestions.split(",");

                for (int i = 0; i < temp1.length; i++) {

                    if(temp1[i].trim().length() > 0) {

                        totalQuestions = totalQuestions+Integer.parseInt(temp1[i]);

                    }

                }

                noOfQuestions = totalQuestions+"";

            }

            //jsonObject.put("no_of_questions", noOfQuestions);

            //contactViewHolder.tv_ques.setText(mContext.getString(R.string.ques, jsonObject.optString("no_of_questions").trim()));

            contactViewHolder.tv_ques.setText(mContext.getString(R.string.ques, noOfQuestions+""));

            contactViewHolder.tv_subjects.setText(mContext.getString(R.string.subjects, jsonObject.optString("subjects").trim()));

            contactViewHolder.tv_startexam.setOnClickListener(onClickListener);

            contactViewHolder.tv_startexam.setTag(position);

            int color = generator.getColor(title);

            String tdText = title;

            if (title.contains(" ")) {

                String[] tempArray = title.split(" ");

                tdText = tempArray[0].charAt(0) + "" + tempArray[1].charAt(0);

            } else if (title.length() > 1) {

                tdText = title.substring(0, 1);

            }

            TextDrawable ic1 = builder.build(tdText, color);

            contactViewHolder.iv_iconexamcontent.setImageDrawable(ic1);

            if (jsonObject.has("question_details")) {

                JSONObject question_details = jsonObject.getJSONObject("question_details");

                contactViewHolder.tv_examdate.setText(mContext.getString(R.string.date, question_details.optString("exam_date").trim()));


                contactViewHolder.tv_examtime.setText(mContext.getString(R.string.time, question_details.optString("from_time").trim() + " - " + question_details.optString("to_time").trim()));

                String timestamp = new SimpleDateFormat("dd-MM-yyyy ")
                        .format(new Date()) // get the current date as String
                        .concat(question_details.optString("from_time").trim()
                );
                String timestamp1 = new SimpleDateFormat("dd-MM-yyyy ")
                        .format(new Date()) // get the current date as String
                        .concat(question_details.optString("to_time").trim()
                );
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                Date date1 = (Date)formatter.parse(timestamp);
                Date date2 = (Date)formatter.parse(timestamp1);

                long time = System.currentTimeMillis();//current time
                long tkl = date1.getTime();// from time
                long tk2 = date2.getTime();// to time

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String c_date=df.format(c);
                String exdt=question_details.optString("exam_date").trim();

                if (question_details.optString("down_status").equalsIgnoreCase("0")){
                    contactViewHolder.tv_startexam.setText("Download");
                }else{
                    contactViewHolder.tv_startexam.setText("Start");

                    if (c_date.equals(exdt)) {
                        boolean result = inRange(tkl, tk2, time);
                        if (result) {
                            contactViewHolder.tv_startexam.setVisibility(View.VISIBLE);
                            contactViewHolder.tv_startexam.setEnabled(true);
                            contactViewHolder.tv_startexam.setBackground(ContextCompat.getDrawable(mContext, R.drawable.button_bg_submit));
                        }else{
                            contactViewHolder.tv_startexam.setVisibility(View.VISIBLE);
                            contactViewHolder.tv_startexam.setEnabled(false);
                            contactViewHolder.tv_startexam.setBackground(ContextCompat.getDrawable(mContext, R.drawable.button_bg_submit_disable));
                        }
                    }else {
                        contactViewHolder.tv_startexam.setVisibility(View.VISIBLE);
                        contactViewHolder.tv_startexam.setEnabled(false);
                        contactViewHolder.tv_startexam.setBackground(ContextCompat.getDrawable(mContext, R.drawable.button_bg_submit_disable));
                        // contactViewHolder.tv_startexam.setEnabled(false);
                    }
                }

//                if (time >= tkl && tk2<=time) {
//                    contactViewHolder.tv_startexam.setVisibility(View.VISIBLE);
//                } else {
//                    contactViewHolder.tv_startexam.setVisibility(View.GONE);
//                }
            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }
    boolean inRange(long low, long high, long x)
    {
        return ((x-high)*(x-low) <= 0);
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_examcontent_item, viewGroup,
                false);

        return new ContactViewHolder(itemView);
    }

    public int getCount() {
        return array.length();
    }

    public long getItemId(int position) {
        return position + 1;
    }

    public void setItems(JSONArray aArray) {
        this.array = aArray;
    }

    public JSONArray getItems() {
        return this.array;
    }

    public void clear() {

        array = null;

        array = new JSONArray();

    }

    public void release() {

        array = null;

    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_iconexamcontent;

        TextView tv_title;

        TextView tv_mins;

        TextView tv_ques;

        TextView tv_startexam;

        TextView tv_subjects;

        TextView tv_examdate;

        TextView tv_examtime;

        ContactViewHolder(View v) {
            super(v);

            iv_iconexamcontent = v.findViewById(R.id.iv_iconexamcontent);

            tv_title = v.findViewById(R.id.tv_title);

            tv_mins = v.findViewById(R.id.tv_mins);

            tv_ques = v.findViewById(R.id.tv_ques);

            tv_startexam = v.findViewById(R.id.tv_startexam);

            tv_subjects = v.findViewById(R.id.tv_subjects);

            tv_examdate = v.findViewById(R.id.tv_examdate);

            tv_examtime = v.findViewById(R.id.tv_examtime);

        }
    }

}