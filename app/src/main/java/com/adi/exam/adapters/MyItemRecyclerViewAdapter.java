package com.adi.exam.adapters;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adi.exam.R;
import com.adi.exam.fragments.Dashboard.OnListFragmentInteractionListener;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private JSONArray jsonArray;

    private final OnListFragmentInteractionListener mListener;

    private Context mContext;

    public MyItemRecyclerViewAdapter(Context context, JSONArray jsonArray, OnListFragmentInteractionListener listener) {

        this.jsonArray = jsonArray;

        mListener = listener;

        mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_dashboard_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        try {

            holder.mItem = jsonArray.getJSONObject(position);

            holder.mIdView.setText(holder.mItem.optString("title"));

            holder.mView.setBackgroundColor(ContextCompat.getColor(mContext, holder.mItem.optInt("colorId")));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (null != mListener) {

                        mListener.onListFragmentInteraction(holder.mItem);

                    }

                }

            });

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
    }

    @Override
    public int getItemCount() {

        return jsonArray.length();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView mView;

        TextView mIdView;

        JSONObject mItem;

        ViewHolder(View view) {
            super(view);

            mIdView = view.findViewById(R.id.tv_title);

            mView = view.findViewById(R.id.cv_block);

        }

    }

}
