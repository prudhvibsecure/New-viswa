package com.adi.exam.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.WiFiSettingsInAppAdapter;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class WiFiSettingsInApp extends ParentFragment implements View.OnClickListener {

    private View layout;

    private RecyclerView mRecyclerView;

    private ProgressBar progressBar;

    private TextView tv_content_txt;

    private OnFragmentInteractionListener mListener;

    private WiFiSettingsInAppAdapter adapter;

    private SriVishwa mActivity;

    private WifiManager mWifiManager;

    private WifiScanReceiver mWifiScanReceiver;

    public WiFiSettingsInApp() {
        // Required empty public constructor
    }

    public static WiFiSettingsInApp newInstance() {
        return new WiFiSettingsInApp();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (OnFragmentInteractionListener) context;

        mActivity = (SriVishwa) context;

    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);

        mListener = (OnFragmentInteractionListener) activity;

        mActivity = (SriVishwa) activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_examlist, container, false);

        progressBar = layout.findViewById(R.id.pb_content_bar);

        tv_content_txt = layout.findViewById(R.id.tv_content_txt);

        tv_content_txt.setText(R.string.ns);

        mWifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mWifiScanReceiver = new WifiScanReceiver();

        mActivity.registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiManager.startScan();

        return layout;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onFragmentInteraction(mActivity.getString(R.string.subject), false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);

        mRecyclerView = layout.findViewById(R.id.rv_content_list);

        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new WiFiSettingsInAppAdapter("title");

        adapter.setOnClickListener(this);

        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @Override
    public void onClick(View view) {

        try {

            int itemPosition = mRecyclerView.getChildLayoutPosition(view);

            JSONObject jsonObject = adapter.getItems().getJSONObject(itemPosition);

            String ssid = jsonObject.optString("title");

            connectToWifi(ssid);

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            mListener.onFragmentInteraction(R.string.subject, false);
    }

    class WifiScanReceiver extends BroadcastReceiver {

        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {

            try {

                progressBar.setVisibility(View.GONE);

                List<ScanResult> wifiScanList = mWifiManager.getScanResults();

                JSONArray jsonArray = new JSONArray();

                int netCount = wifiScanList.size();

                netCount = netCount - 1;

                while (netCount >= 0) {

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("title", wifiScanList.get(netCount).SSID.toString());

                    netCount = netCount - 1;

                    jsonArray.put(jsonObject);

                }

                if (jsonArray.length() > 0) {

                    adapter.setItems(jsonArray);

                    adapter.notifyDataSetChanged();

                    return;

                }

                tv_content_txt.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);

            } catch (Exception e) {

                TraceUtils.logException(e);

            }

        }

    }

    private void connectToWifi(final String wifiSSID) {

        final Dialog dialog = new Dialog(mActivity);

        dialog.setContentView(R.layout.connect);

        dialog.setTitle("Connect to Network");

        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);

        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);

        final EditText pass = (EditText) dialog.findViewById(R.id.textPassword);

        textSSID.setText(wifiSSID);

        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String checkPassword = pass.getText().toString();

                finallyConnect(checkPassword, wifiSSID);

                dialog.dismiss();

            }

        });

        dialog.show();

    }

    private void finallyConnect(String networkPass, String networkSSID) {

        WifiConfiguration wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = String.format("\"%s\"", networkSSID);

        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        // remember id
        int netId = mWifiManager.addNetwork(wifiConfig);

        mWifiManager.disconnect();

        mWifiManager.enableNetwork(netId, true);

        mWifiManager.reconnect();

        WifiConfiguration conf = new WifiConfiguration();

        conf.SSID = "\"\"" + networkSSID + "\"\"";

        conf.preSharedKey = "\"" + networkPass + "\"";

        mWifiManager.addNetwork(conf);

    }

}
