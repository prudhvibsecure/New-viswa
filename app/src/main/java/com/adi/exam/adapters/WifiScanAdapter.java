package com.adi.exam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.fragments.WifiFragment;

import java.util.List;


public class WifiScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private List<WifiFragment.device> wifiList;
private Context context;
private View.OnClickListener mOnClickListener;
    private ContactAdapterListener listener;
    public WifiScanAdapter(List<WifiFragment.device> wifiList, Context context,ContactAdapterListener listener) {
        this.wifiList = wifiList;
        this.context=context;
        this.listener = listener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.network_list, viewGroup, false);

         MyViewHolder holder = new MyViewHolder(itemView);
            itemView.setTag(holder);
            itemView.setOnClickListener(mOnClickListener);
            return holder;

    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


           WifiFragment.device device=wifiList.get(position);
           String name=device.getName().toString();

             ((MyViewHolder) holder).vName.setText(name);
             ((MyViewHolder) holder).vName.setTag(device);


            ((MyViewHolder) holder).vImage.setImageResource(R.mipmap.ic_action_wifi);
            ((MyViewHolder) holder).context = context;
            ((MyViewHolder) holder).position = position;
             applyClickEvents(((MyViewHolder) holder).vName, ((MyViewHolder) holder).wfi_connect,((MyViewHolder) holder). password_wifi ,position,device,((MyViewHolder) holder).wifi_hiden);
    }

    private void applyClickEvents(TextView name, final TextView wfi_connect, final EditText password_wifi, final int position, final  WifiFragment.device device, final LinearLayout wifi_ll) {
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(wfi_connect,password_wifi, device,position,wifi_ll);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

   /* private void applyClickEvents(ContactViewHolder contactViewHolder, final List<ClassModel> classModelList, final int position) {
        contactViewHolder.mViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageRowClicked(classModelList, position);
                } catch (Exception e) {

                }
            }
        });*/

  //  }
    @Override
    public int getItemCount() {

        int itemCount = wifiList.size();

        return itemCount;
    }
    public void setOnClickListener(View.OnClickListener lis) {
        mOnClickListener = lis;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected ImageView vImage;
        protected TextView vName;
        protected  Context context;
        protected int position;
        LinearLayout wifi_hiden;
        EditText password_wifi;
        TextView wfi_connect;

        public MyViewHolder(View v) {
            super(v);
            vName = v.findViewById(R.id.ssid_name);
            vImage = v.findViewById(R.id.Wifilogo);
            wifi_hiden = v.findViewById(R.id.wifi_hiden);
            password_wifi = v.findViewById(R.id.password_wifi);
            wfi_connect = v.findViewById(R.id.wfi_connect);

        }
    }
    public interface ContactAdapterListener {

        void onMessageRowClicked(TextView wfi_connect, EditText password_wifi, WifiFragment.device device, int position, LinearLayout wifi_ll);
    }

}
