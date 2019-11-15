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


public class WifiScanAdapter extends RecyclerView.Adapter<WifiScanAdapter.ContactViewHolder> {

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
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.network_list, viewGroup,
                false);

        return new ContactViewHolder(itemView);
//            View itemView = LayoutInflater.
//                    from(viewGroup.getContext()).
//                    inflate(R.layout.network_list, viewGroup, false);
//
//         MyViewHolder holder = new MyViewHolder(itemView);
//            itemView.setTag(holder);
//            itemView.setOnClickListener(mOnClickListener);
//            return holder;

    }
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {


           WifiFragment.device device=wifiList.get(position);
           String name=device.getName().toString();

             holder.vName.setText(name);
             holder.vName.setTag(device);


             holder.vImage.setImageResource(R.mipmap.ic_action_wifi);
             holder.context = context;
            holder.position = position;
             applyClickEvents(holder.vName,  holder.wfi_connect,holder.password_wifi ,position,device, holder.wifi_hiden,holder.wfi_forgot);
    }

    private void applyClickEvents(TextView name, final TextView wfi_connect, final EditText password_wifi, final int position, final  WifiFragment.device device, final LinearLayout wifi_ll,final TextView wfi_forgot) {
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(wfi_connect,password_wifi, device,position,wifi_ll);
            }
        });
        wfi_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageForgot(wfi_connect,password_wifi, device,position,wifi_ll,wfi_forgot);
            }
        });
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
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        protected ImageView vImage;
        protected TextView vName;
        protected TextView wfi_forgot;
        protected  Context context;
        protected int position;
        LinearLayout wifi_hiden;
        EditText password_wifi;
        TextView wfi_connect;

        public ContactViewHolder(View v) {
            super(v);
            vName = v.findViewById(R.id.ssid_name);
            vImage = v.findViewById(R.id.Wifilogo);
            wifi_hiden = v.findViewById(R.id.wifi_hiden);
            password_wifi = v.findViewById(R.id.password_wifi);
            wfi_connect = v.findViewById(R.id.wfi_connect);
            wfi_forgot = v.findViewById(R.id.wfi_forgot);

        }
    }
    public interface ContactAdapterListener {

        void onMessageRowClicked(TextView wfi_connect, EditText password_wifi, WifiFragment.device device, int position, LinearLayout wifi_ll);

        void onMessageForgot(TextView wfi_connect, EditText password_wifi, WifiFragment.device device, int position, LinearLayout wifi_ll, TextView wfi_forgot);
    }

}
