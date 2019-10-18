package com.adi.exam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class WiFiSettingsInAppAdapter extends RecyclerView.Adapter<WiFiSettingsInAppAdapter.ContactViewHolder> {

    private JSONArray array = new JSONArray();

    private OnClickListener onClickListener;

    private String mTagName = "";

    public WiFiSettingsInAppAdapter(String aTagName) {

        mTagName = aTagName;

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

            contactViewHolder.tv_title.setText(jsonObject.optString(mTagName).trim());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_material_frag, viewGroup,
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

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_title;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = v.findViewById(R.id.tv_title);

        }
    }

}