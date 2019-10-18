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
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.common.ColorGenerator;
import com.adi.exam.controls.TextDrawable;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExamListingAdapter extends RecyclerView.Adapter<ExamListingAdapter.ContactViewHolder> {

    private JSONArray array = new JSONArray();

    private OnClickListener onClickListener;

    private TextDrawable.IBuilder builder;

    private ColorGenerator generator;

    public ExamListingAdapter(Context context) {

        generator = ColorGenerator.MATERIAL;

        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int i) {

        try {

            JSONObject jsonObject = array.getJSONObject(i);

            String exam_name = jsonObject.optString("exam_name").trim();

            contactViewHolder.tv_examtitle.setText(exam_name);

            int color = generator.getColor(exam_name);

            String tdText = exam_name;

            if (exam_name.contains(" ")) {

                String[] tempArray = exam_name.split(" ");

                tdText = tempArray[0].charAt(0) + "" + tempArray[1].charAt(0);

            } else if (exam_name.length() > 1) {

                tdText = exam_name.substring(0, 1);

            }

            TextDrawable ic1 = builder.build(tdText, color);

            contactViewHolder.iv_iconexam.setImageDrawable(ic1);

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_exam_item, viewGroup,
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

        TextView tv_examtitle;

        ImageView iv_iconexam;

        ContactViewHolder(View v) {
            super(v);

            tv_examtitle = v.findViewById(R.id.tv_examtitle);

            iv_iconexam = v.findViewById(R.id.iv_iconexam);

        }
    }

}