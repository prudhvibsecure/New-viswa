package com.adi.exam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.controls.ProgressControl;
import com.adi.exam.controls.progress.CircleProgressView;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.ContactViewHolder> {

    private JSONArray array = new JSONArray();

    private OnClickListener onClickListener;

    private String mTagName = "";

    private HashMap<String, Integer> positionMap = new HashMap<>();

    private boolean[] mDataUpdatingFlags;

    public MaterialsAdapter(String aTagName) {

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
    public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int position) {

        try {

            JSONObject jsonObject = array.getJSONObject(position);

            positionMap.put(jsonObject.optString("material_data_id"), position);

            contactViewHolder.tv_title.setText(jsonObject.optString(mTagName).trim());

            if (jsonObject.optString("material_original_name").toLowerCase().contains(".pdf")) {

                contactViewHolder.iv_icon.setImageResource(R.drawable.pdf_red);

            } else if (jsonObject.optString("material_original_name").toLowerCase().contains(".doc") || jsonObject.optString("material_original_name").toLowerCase().contains(".docx")) {

                contactViewHolder.iv_icon.setImageResource(R.drawable.word);

            } else if (jsonObject.optString("material_original_name").toLowerCase().contains(".png") || jsonObject.optString("material_original_name").toLowerCase().contains(".jpeg") || jsonObject.optString("material_original_name").toLowerCase().contains(".jpg")) {

                contactViewHolder.iv_icon.setImageResource(R.drawable.image);

            }

            if (jsonObject.optString("downloadstatus").equalsIgnoreCase("C")) {

                contactViewHolder.downloadStatusIV.setImageResource(R.drawable.dwn_complete);
                contactViewHolder.circleProgressView.setVisibility(View.GONE);
                //contactViewHolder.rlDownloadLayout.setOnClickListener(null);

            } else if (jsonObject.optString("downloadstatus").equalsIgnoreCase("Q")
                    || jsonObject.optString("downloadstatus").equalsIgnoreCase("P")) {

                contactViewHolder.downloadStatusIV.setImageResource(R.drawable.ic_download);
                contactViewHolder.circleProgressView.setVisibility(View.GONE);
                //contactViewHolder.rlDownloadLayout.setOnClickListener(listener);

            } else {

                contactViewHolder.downloadStatusIV.setImageResource(R.drawable.ic_download);
                contactViewHolder.circleProgressView.setVisibility(View.GONE);
                //contactViewHolder.rlDownloadLayout.setOnClickListener(listener);

            }

            contactViewHolder.pc_dprg.setVisibility(View.GONE);

            contactViewHolder.tv_failedmsg.setVisibility(View.VISIBLE);

            contactViewHolder.tv_failedmsg.setText(jsonObject.optString("errorMsg"));

            if (isItemUpdating(position)) {

                contactViewHolder.pc_dprg.setVisibility(View.GONE);

                contactViewHolder.tv_failedmsg.setVisibility(View.GONE);

            }

            contactViewHolder.pc_dprg.setCancelEventId(jsonObject.optString("material_data_id"));

            contactViewHolder.pc_dprg.setData(jsonObject);

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_materials_frag, viewGroup,
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
        mDataUpdatingFlags = new boolean[array.length()];
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

        protected TextView tv_failedmsg;

        protected ImageView iv_icon;

        protected ImageView downloadStatusIV;

        protected CircleProgressView circleProgressView;

        protected ProgressControl pc_dprg;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = v.findViewById(R.id.tv_title);

            tv_failedmsg = v.findViewById(R.id.tv_failedmsg);

            iv_icon = v.findViewById(R.id.iv_icon);

            downloadStatusIV = v.findViewById(R.id.downloadStatusIV);

            circleProgressView = itemView.findViewById(R.id.circleProgressView);

            pc_dprg = itemView.findViewById(R.id.pc_dprg);

        }

    }

    public int getProgressPosition(String cid) {

        return positionMap.get(cid) == null ? -1 : positionMap.get(cid);

    }

    private boolean isItemUpdating(int position) {
        return mDataUpdatingFlags[position];
    }

    public void setItemUpdating(int position, boolean isUpdating, Long... values) {
        if (mDataUpdatingFlags != null)
            mDataUpdatingFlags[position] = isUpdating;
    }

    public void removeItemUpdating(int position) {
        try {
            if (mDataUpdatingFlags != null)
                mDataUpdatingFlags[position] = false;
        } catch (Exception e) {
            TraceUtils.logException(e);
        }
    }

}