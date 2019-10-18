package com.adi.exam.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.common.ColorGenerator;
import com.adi.exam.controls.TextDrawable;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class AssignmentHistoryAdapter extends RecyclerView.Adapter<AssignmentHistoryAdapter.ContactViewHolder> {

    private JSONArray array = new JSONArray();

    private OnClickListener onClickListener;

    private TextDrawable.IBuilder builder;

    private ColorGenerator generator;

    private Context mContext;

    public AssignmentHistoryAdapter(Context context) {

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

            String title = jsonObject.optString("assignment_name").trim();

            contactViewHolder.tv_title.setText(title);

            int color = generator.getColor(title);

//            String tdText = title;

//            if (title.contains(" ")) {
//
//                String[] tempArray = title.split(" ");
//
//                tdText = tempArray[0].charAt(0) + "" + tempArray[1].charAt(0);
//
//            } else if (title.length() > 1) {

            title = title.substring(0, 1);

         //   }

            TextDrawable ic1 = builder.build(title, color);

            contactViewHolder.iv_iconexamcontent.setImageDrawable(ic1);
            contactViewHolder.row_vv.setOnClickListener(onClickListener);
            contactViewHolder.row_vv.setTag(position);
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

        ImageView iv_iconexamcontent;
        LinearLayout row_vv;
        TextView tv_title;


        ContactViewHolder(View v) {
            super(v);

            iv_iconexamcontent = v.findViewById(R.id.iv_iconexam);

            tv_title = v.findViewById(R.id.tv_examtitle);
            row_vv = v.findViewById(R.id.ll_exam);

        }
    }

}