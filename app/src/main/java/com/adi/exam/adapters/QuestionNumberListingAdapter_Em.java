package com.adi.exam.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class QuestionNumberListingAdapter_Em extends RecyclerView.Adapter<QuestionNumberListingAdapter_Em.ContactViewHolder> {

    private JSONArray array = new JSONArray();

    private OnClickListener onClickListener;

    private int whiteColor = -1;

    private int blackColor = -1;

    private Drawable notVisited;

    private Drawable notAnswered;

    private Drawable answered;

    private Drawable markedforReview;

    private Drawable answeredNMarkedforReview;

    private int selectedPosition = -1;

    public QuestionNumberListingAdapter_Em(Context context) {

        whiteColor = ContextCompat.getColor(context, android.R.color.white);

        blackColor = ContextCompat.getColor(context, android.R.color.black);

        notVisited = ContextCompat.getDrawable(context, R.drawable.ic_not_visited);

        notAnswered = ContextCompat.getDrawable(context, R.drawable.ic_preve);

        answered = ContextCompat.getDrawable(context, R.drawable.ic_anse);

        markedforReview = ContextCompat.getDrawable(context, R.drawable.ic_marked_for_review);

        answeredNMarkedforReview = ContextCompat.getDrawable(context, R.drawable.ic_answered_marked);

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

            int qstate = jsonObject.optInt("qstate");

            contactViewHolder.tv_questionno.setText(jsonObject.optString("sno"));

            contactViewHolder.tv_questionno.setTextColor(whiteColor);

            if (qstate == 0) {

                contactViewHolder.tv_questionno.setTextColor(blackColor);

                contactViewHolder.tv_questionno.setBackground(notVisited);

            } else if (qstate == 1) {

                contactViewHolder.tv_questionno.setBackground(notAnswered);

            } else if (qstate == 2) {

                contactViewHolder.tv_questionno.setBackground(answered);

            } else if (qstate == 3) {

                contactViewHolder.tv_questionno.setBackground(markedforReview);

            } else if (qstate == 4) {

                contactViewHolder.tv_questionno.setBackground(answeredNMarkedforReview);

            }

            contactViewHolder.fl_questionno.setVisibility(View.INVISIBLE);

            if (selectedPosition == position) {

                contactViewHolder.fl_questionno.setVisibility(View.VISIBLE);

            }

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_questionnumber_item, viewGroup,
                false);

        itemView.setOnClickListener(onClickListener);

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

        TextView tv_questionno;

        View fl_questionno;

        ContactViewHolder(View v) {
            super(v);

            tv_questionno = v.findViewById(R.id.tv_questionno);

            fl_questionno = v.findViewById(R.id.fl_questionno);

        }
    }

    public void setSelectedPosition(int aSelectedPosition) {
        selectedPosition = aSelectedPosition;
    }

}