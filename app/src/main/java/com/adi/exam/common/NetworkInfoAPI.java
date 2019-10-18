package com.adi.exam.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.adi.exam.utils.TraceUtils;

import java.util.Locale;

public class NetworkInfoAPI {

    private boolean registered = false;

    private ConnectivityManager cmanager = null;

    private BroadcastReceiver receiver = null;

    private final String TYPE_NONE = "none";

    private String lastStatus = "";

    private Context mContext = null;

    private OnNetworkChangeListener callback = null;

    public interface OnNetworkChangeListener {
        void onNetworkChange(String status, String lastStatus);
    }

    public void setOnNetworkChangeListener(OnNetworkChangeListener aCallback) {
        this.callback = aCallback;
        setOnNetworkChange();
    }

    public void initialize(Context context) {
        this.cmanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.mContext = context;
    }

    private void setOnNetworkChange() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (this.receiver == null) {
            this.receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateConnectionInfo(cmanager.getActiveNetworkInfo());
                }
            };
            mContext.registerReceiver(this.receiver, intentFilter);
            this.registered = true;
        }
    }

    public String execute(String action) {
        if (action.equals("getConnectionInfo")) {
            NetworkInfo info = cmanager.getActiveNetworkInfo();
            return this.getConnectionInfo(info);
        }
        return TYPE_NONE;
    }

    private void updateConnectionInfo(NetworkInfo info) {
        String status = this.getConnectionInfo(info);

        if (!status.equals(lastStatus)) {
            sendUpdate(status, lastStatus);
            lastStatus = status;
        }
    }

    public boolean isConnected(Context context) {

        if (cmanager == null) {
            this.cmanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            this.mContext = context;
        }

        NetworkInfo net = cmanager.getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    private String getConnectionInfo(NetworkInfo info) {
        String type = TYPE_NONE;
        if (info != null) {

            if (!info.isConnected()) {
                type = TYPE_NONE;
            } else {
                type = getType(info);
            }
        }
        return type;
    }

    private void sendUpdate(String status, String lastStatus) {
        callback.onNetworkChange(status, lastStatus);
    }

    private String getType(NetworkInfo info) {
        if (info != null) {
            String type = info.getTypeName();

            if (type.toLowerCase(Locale.ENGLISH).equalsIgnoreCase("wifi")) {
                return "wifi";
            } else if (type.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(
                    "mobile")) {
                type = info.getSubtypeName();
                if (type.toLowerCase(Locale.ENGLISH).equalsIgnoreCase("gsm")
                        || type.toLowerCase(Locale.ENGLISH).equals("gprs")
                        || type.toLowerCase(Locale.ENGLISH).equals("edge")) {
                    return "2g";
                } else if (type.toLowerCase(Locale.ENGLISH).startsWith("cdma")
                        || type.toLowerCase(Locale.ENGLISH).equals("umts")
                        || type.toLowerCase(Locale.ENGLISH).equals("1xrtt")
                        || type.toLowerCase(Locale.ENGLISH).equals("ehrpd")
                        || type.toLowerCase(Locale.ENGLISH).equals("hsupa")
                        || type.toLowerCase(Locale.ENGLISH).equals("hsdpa")
                        || type.toLowerCase(Locale.ENGLISH).equals("hspa")) {
                    return "3g";
                } else if (type.toLowerCase(Locale.ENGLISH).equals("lte")
                        || type.toLowerCase(Locale.ENGLISH).equals("umb")
                        || type.toLowerCase(Locale.ENGLISH).equals("hspa+")) {
                    return "4g";
                }
            }
        } else {
            return TYPE_NONE;
        }
        return "unknown";
    }

    public void onDestroy() {
        cmanager = null;
        if (this.receiver != null && this.registered) {
            try {
                mContext.unregisterReceiver(this.receiver);
                this.registered = false;
            } catch (Exception e) {
                TraceUtils.logException(e);
            }
        }
    }

}
