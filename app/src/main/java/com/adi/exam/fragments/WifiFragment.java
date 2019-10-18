package com.adi.exam.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.WifiScanAdapter;
import com.adi.exam.callbacks.IOnFocusListenable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WifiFragment extends ParentFragment implements IOnFocusListenable, WifiScanAdapter.ContactAdapterListener {
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            getActivity().sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        } else {
            getActivity().sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        }
    }

    @Override
    public void onMessageRowClicked(TextView wfi_connect,final EditText password_wifi, final device device, int position, final  LinearLayout wifi_ll) {

        wifi_ll.setVisibility(View.VISIBLE);


        wfi_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifi_ll.setVisibility(View.GONE);
                password = password_wifi.getText().toString();
               // Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
                connectWiFi(String.valueOf(device.getName()), password, device.capabilities);
            }
        });
        //  result.setText(userInput.getText());



    }

    public class device {
        CharSequence name;

        public String getCapabilities() {
            return capabilities;
        }

        public void setCapabilities(String capabilities) {
            this.capabilities = capabilities;
        }

        String capabilities;

        public void setName(CharSequence name) {
            this.name = name;
        }

        public CharSequence getName() {
            return name;
        }
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 125;
    List<ScanResult> wifiList;
    private WifiManager wifi;
    List<device> values = new ArrayList<device>();
    int netCount = 0;
    RecyclerView recyclerView;
    WifiScanAdapter wifiScanAdapter;
    private SriVishwa mActivity;
    private static String TAG = "WifiFragment";
    private String password = null;
    //Option Menu for wifi connection
    // private OnFragmentInteractionListener mListener;

    private OnFragmentInteractionListener mFragListener;
    private static String screen = "";


    public WifiFragment() {
        // Required empty public constructor
    }

    public static WifiFragment newInstance(String type) {
        WifiFragment fragment = new WifiFragment();
        screen = type;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Make instance of Wifi
        if (screen.startsWith("Settings")) {
            mFragListener.onFragmentInteraction(R.string.wifisettings, false);
        }
        ImageView btnScan = (ImageView) getActivity().findViewById(R.id.scan);
        wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //Check wifi enabled or not
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getActivity(), "Wifi is disabled enabling...", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        //register Broadcast receiver
        try {
            getActivity().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (ContextCompat.checkSelfPermission((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
                    } else {
                        wifiList = wifi.getScanResults();
                        netCount = wifiList.size();
                    }
                    // wifiScanAdapter.notifyDataSetChanged();
                    Log.d("Wifi", "Total Wifi Network" + netCount);
                }
            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        wifi.startScan();
        try {
            netCount = netCount - 1;
            if (netCount >= 0) {
                while (netCount >= 0) {
                    getActivity().findViewById(R.id.no_scan).setVisibility(View.GONE);
                    device d = new device();
                    d.setName(wifiList.get(netCount).SSID.toString());
                    d.setCapabilities(wifiList.get(netCount).capabilities);
                    values.add(d);
                    wifiScanAdapter.notifyDataSetChanged();
                    netCount = netCount - 1;
                }
            } else {
                getActivity().findViewById(R.id.no_scan).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.d("Wifi", e.getMessage());
        }
        wifiScanAdapter = new WifiScanAdapter(values, getActivity(), this);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.wifiRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(wifiScanAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkandAskPermission();
        } else {
            wifi.startScan();
            values.clear();
            try {
                netCount = netCount - 1;
                while (netCount >= 0) {
                    device d = new device();
                    d.setName(wifiList.get(netCount).SSID.toString());
                    d.setCapabilities(wifiList.get(netCount).capabilities);
                    Log.d("WiFi", d.getName().toString());
                    values.add(d);
                    wifiScanAdapter.notifyDataSetChanged();
                    netCount = netCount - 1;
                    getActivity().findViewById(R.id.no_scan).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.d("Wifi", e.getMessage());
            }
        }
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wifi.startScan();
                values.clear();
                try {
                    netCount = netCount - 1;
                    while (netCount >= 0) {
                        getActivity().findViewById(R.id.no_scan).setVisibility(View.GONE);
                        device d = new device();
                        d.setName(wifiList.get(netCount).SSID.toString());
                        d.setCapabilities(wifiList.get(netCount).capabilities);
                        values.add(d);
                        wifiScanAdapter.notifyDataSetChanged();
                        netCount = netCount - 1;
                    }
                } catch (Exception e) {
                    Log.d("Wifi", e.getMessage());
                }
            }
        });
       /* wifiScanAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final device d = (device) v.findViewById(R.id.ssid_name).getTag();
                Log.d(TAG, "Selected Network is " + d.getName());
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.menuwifi, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextPassword);
                TextView ssidText = (TextView) promptsView.findViewById(R.id.textViewSSID);
                ssidText.setText("Connecting to " + d.getName());
                TextView security = (TextView) promptsView.findViewById(R.id.textViewSecurity);
                security.setText("Security for Network is:\n" + d.getCapabilities());
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        Log.d(TAG, "Password is:" + userInput.getText());
                                        password = userInput.getText().toString();
                                        //  result.setText(userInput.getText());
                                        connectWiFi(String.valueOf(d.getName()), password, d.capabilities);

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();

            }
        });*/
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (screen.equalsIgnoreCase("Settings")) {
            mFragListener = (OnFragmentInteractionListener) context;
            mActivity = (SriVishwa) context;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //  mListener = null;
        mFragListener = null;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (screen.startsWith("Settings")) {
            mFragListener.onFragmentInteraction(R.string.dashboard, true);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    wifi.startScan();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            break;

            case 11:

                wifiList = wifi.getScanResults();
                netCount = wifiList.size();

                break;
        }
    }

    private void checkandAskPermission() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Network");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " /*+ permissionsNeeded.get(0)*/;
                for (int i = 0; i < permissionsNeeded.size(); i++)
                    message = message + "" + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }

            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        // initVideo();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    public void connectWiFi(String SSID, String password, String Security) {
        try {

            Log.d(TAG, "Item clicked, SSID " + SSID + " Security : " + Security);

            String networkSSID = SSID;
            String networkPass = password;

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;

            if (Security.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                }

                conf.wepTxKeyIndex = 0;

            } else if (Security.toUpperCase().contains("WPA")) {
                Log.v(TAG, "Configuring WPA");

                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                conf.preSharedKey = "\"" + networkPass + "\"";

            } else {
                Log.v(TAG, "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }

            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = wifiManager.addNetwork(conf);

            Log.v(TAG, "Add result " + networkId);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    Log.v(TAG, "WifiConfiguration SSID " + i.SSID);

                    boolean isDisconnected = wifiManager.disconnect();
                    Log.v(TAG, "isDisconnected : " + isDisconnected);

                    boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                    Log.v(TAG, "isEnabled : " + isEnabled);

                    boolean isReconnected = wifiManager.reconnect();
                    Log.v(TAG, "isReconnected : " + isReconnected);

                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getActivity()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getActivity().getTaskId(), 0);
    }

    @Override
    public void onResume() {

        super.onResume();
    }


}
