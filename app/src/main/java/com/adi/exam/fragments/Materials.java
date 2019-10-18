package com.adi.exam.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.ViewMaterial;
import com.adi.exam.adapters.MaterialsAdapter;
import com.adi.exam.callbacks.IDialogCallbacks;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.controls.ProgressControl;
import com.adi.exam.controls.progress.CircleProgressView;
import com.adi.exam.database.App_Table;
import com.adi.exam.database.PhoneComponent;
import com.adi.exam.services.DownloadService;
import com.adi.exam.tasks.HTTPPostTask;
import com.adi.exam.utils.TraceUtils;
import com.adi.exam.utils.Utils;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class
Materials extends ParentFragment implements IItemHandler, View.OnClickListener, IDialogCallbacks {

    private View layout;

    private RecyclerView mRecyclerView;

    private ProgressBar progressBar;

    private TextView tv_content_txt;

    private OnFragmentInteractionListener mListener;

    private MaterialsAdapter adapter;

    private SriVishwa mActivity;

    private JSONObject mJsonObject;

    private App_Table app_table;

    private JSONObject selectedJSON;

    private Messenger messenger = null;

    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    private boolean mIsBound;

    private String url = "";

    private int downloadID;

    private String path = Environment.getExternalStorageDirectory().getPath() + "/Materials/";


    public Materials() {
        // Required empty public constructor
    }

    public static Materials newInstance(String data) {

        Materials fragment = new Materials();
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        fragment.setArguments(bundle);
        return fragment;

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

        try {

            if (getArguments() != null) {

                String data = getArguments().getString("data");

                mJsonObject = new JSONObject(data);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_examlist, container, false);

        progressBar = layout.findViewById(R.id.pb_content_bar);

        tv_content_txt = layout.findViewById(R.id.tv_content_txt);

        tv_content_txt.setText(R.string.nomaterial);

        if (!mIsBound) {

            doBindService(new Intent(mActivity, DownloadService.class));

        }

        return layout;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onFragmentInteraction(R.string.material, false);

        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);

        mRecyclerView = layout.findViewById(R.id.rv_content_list);

        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new MaterialsAdapter("material_original_name");

        adapter.setOnClickListener(this);

        mRecyclerView.setAdapter(adapter);

        getData();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    private void getData() {

        try {

            progressBar.setVisibility(View.VISIBLE);

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("course", mActivity.getStudentDetails().optString("program_name"));

            jsonObject.put("subject", mJsonObject.optString("subject"));

            jsonObject.put("lesson", mJsonObject.optString("lesson_name"));

            jsonObject.put("topic", mJsonObject.optString("topic_name"));

            jsonObject.put("material_data_ids", "");

            HTTPPostTask post = new HTTPPostTask(mActivity, this);

            post.disableProgress();

            post.userRequest(getString(R.string.plwait), 1, "getmaterial", jsonObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
    }

    @Override
    public void onFinish(Object results, int requestId) {

        progressBar.setVisibility(View.GONE);

        try {

            if (requestId == 1) {

                parseData(results);

                getTracksListFromTable();

            } else if (requestId == 2) {

                updateUI(results);

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onError(String errorCode, int requestType) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

    }

    @Override
    public void onClick(View view) {

        try {

            //material_data_id INTEGER, material_original_name TEXT, material_unique_name TEXT, downloadurl TEXT, downloadstatus TEXT, savedpath TEXT, mimetype TEXT, errorMsg TEXT

            int itemPosition = mRecyclerView.getChildLayoutPosition(view);

            JSONObject jsonObject = adapter.getItems().getJSONObject(itemPosition);

            selectedJSON = jsonObject;

            if (mActivity.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", 200) != 1) {

                return;

            }
            url = selectedJSON.optString("downloadurl");

            Intent in = new Intent(getActivity(), ViewMaterial.class);
            in.putExtra("url", url);
            startActivity(in);

            /*File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            String full_path = path + jsonObject.optString("material_unique_name");
            File filer = new File(full_path);
            String type = Utils.getMimeType(jsonObject.optString("material_unique_name"));
            if (filer.exists()) {
                Uri path = Uri.fromFile(new File(full_path));
                Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                launchIntent.setDataAndType(path, type);
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(launchIntent);
            } else {
                Intent in = new Intent(getActivity(), ViewMaterial.class);
                in.putExtra("url", url);
                startActivity(in);

                long folder_size = getFolderSize(file);
                if (folder_size < 5120000000L) {
                    filePDF(selectedJSON);
                } else {
                    //delete file here
                    File yourDir = new File(path);
                    for (File f : yourDir.listFiles()) {
                        int file_size = Integer.parseInt(String.valueOf(f.length() / 1024));
                        if (file_size > 25) {
                            f.delete();
                        }
                    }
                }
            }*/

            // initDownload(selectedJSON);

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void filePDF(JSONObject jsonObject) {

        if (Status.RUNNING == PRDownloader.getStatus(downloadID)) {
            PRDownloader.pause(downloadID);
            return;
        }

        if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
            PRDownloader.resume(downloadID);
            return;
        }
        downloadID = PRDownloader.download(jsonObject.optString("downloadurl"), path, jsonObject.optString("material_unique_name"))
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        downloadID = 0;

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Toast.makeText(mActivity, "Download completed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Error error) {

                        Toast.makeText(mActivity, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void parseData(Object results) throws Exception {

        if (results != null) {

            JSONObject jsonObject = new JSONObject(results.toString());

            if (jsonObject.optString("statuscode").equalsIgnoreCase("200")) {

                if (jsonObject.has("material_details")) {

                    JSONArray jsonArray = jsonObject.getJSONArray("material_details");

                    if (jsonArray.length() > 0) {

                        String downloadlink = jsonObject.optString("downloadlink");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            jsonObject1.put("downloadurl", downloadlink + jsonObject1.optString("material_unique_name"));

                            url = downloadlink + jsonObject1.optString("material_unique_name");

                            jsonObject1.put("downloadstatus", "");

                            jsonObject1.put("savedpath", "");

                            jsonObject1.put("mimetype", "");

                            jsonObject1.put("errorMsg", "");

                        }

                        adapter.setItems(jsonArray);

                        adapter.notifyDataSetChanged();

                        return;

                    }

                }

            }

        }

        tv_content_txt.setVisibility(View.VISIBLE);

    }

    private void updateUI(Object object) throws Exception {

        JSONArray jsonArray = (JSONArray) (object);

        if (jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                for (int j = 0; j < adapter.getCount(); j++) {

                    JSONObject jsonObject1 = adapter.getItems().getJSONObject(j);

                    if (jsonObject1.optString("material_data_id").equalsIgnoreCase(jsonObject.optString("material_data_id"))) {

                        jsonObject1.put("downloadurl", jsonObject.optString("jsonObject"));

                        jsonObject1.put("downloadstatus", jsonObject.optString("downloadstatus"));

                        jsonObject1.put("savedpath", jsonObject.optString("savedpath"));

                        jsonObject1.put("mimetype", jsonObject.optString("mimetype"));

                        jsonObject1.put("errorMsg", jsonObject.optString("errorMsg"));

                        adapter.notifyDataSetChanged();

                        break;

                    }

                }

            }

        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden)
            mListener.onFragmentInteraction(R.string.material, false);

    }

    private App_Table getDataBaseObject() {

        if (app_table == null)
            app_table = new App_Table(mActivity);

        return app_table;
    }

    private void getTracksListFromTable() {

        try {

            progressBar.setVisibility(View.GONE);

            PhoneComponent phncomp = new PhoneComponent(this, mActivity, 2);

            phncomp.executeLocalDBInBackground("DOWNLOADQUEUE");

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void startDownloading(JSONObject jsonObject) {

        app_table = getDataBaseObject();

        app_table.updateDownloadStatus(String.valueOf(jsonObject.optString("material_data_id")), "Q");

        Intent intent = new Intent(mActivity, DownloadService.class);

        intent.setAction("com.ooredoo.music.action.addcontent");

        intent.putExtra("data", jsonObject.toString());

        intent.putExtra("requestId", Integer.parseInt(jsonObject.optString("material_data_id")));

        mActivity.startService(intent);

    }

    @Override
    public void onOK(int requestId) {

        try {

            if (requestId == 1) {

                if (selectedJSON != null) {

                    app_table = getDataBaseObject();

                    //app_table.updateDownloadStatus(selectedJSON.optString("material_data_id"), "F");

                    app_table.updateOnFailSavedPath(selectedJSON.optString("material_data_id"));

                    app_table.updateDownloadStatus(String.valueOf(selectedJSON.optString("material_data_id")), "Q");

                    selectedJSON.put("downloadstatus", "F");

                    //startDownloading(selectedJSON);

                }

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onCancel(int requestId) {

    }

    private void doBindService(Intent service) {

        mActivity.bindService(service, mConnection, Context.BIND_AUTO_CREATE);

        mIsBound = true;

    }


    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {

            messenger = new Messenger(service); // using this messenger your can
            // send data to service
            try {

                Message msg = Message.obtain(null, DownloadService.MSG_REGISTER_CLIENT);

                msg.replyTo = mMessenger;

                messenger.send(msg);

            } catch (Exception ex) {

                TraceUtils.logException(ex);

            }

        }

        public void onServiceDisconnected(ComponentName className) {

            messenger = null;

        }

    };

    private void doUnbindService() {

        if (mIsBound) {

            if (messenger != null) {
                try {
                    Message msg = Message.obtain(null, DownloadService.MSG_UNREGISTER_CLIENT);

                    msg.replyTo = mMessenger;

                    messenger.send(msg);
                } catch (Exception ex) {
                    TraceUtils.logException(ex);
                }
            }

            mActivity.unbindService(mConnection);

            mIsBound = false;
        }

    }

    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case DownloadService.DWL_START:
                    break;

                case DownloadService.DWL_STOP:
                    break;

                case DownloadService.DWL_PROGS:

                    setItemUpdating(msg.arg1, true, (Long[]) msg.obj);

                    Long[] values = (Long[]) msg.obj;

                    if (values != null && values.length > 4) {

                        Long progressVal = values[0];

                        TraceUtils.logE("Download Player", "Download Player progress: " + progressVal + ", msg.arg1: " + msg.arg1);

                        int pos = adapter.getProgressPosition(String.valueOf(msg.arg1));

                        RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(pos);

                        if (null != holder) {

                            if (progressVal == 100) {

                                ((ImageView) holder.itemView.findViewById(R.id.downloadStatusIV)).setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_downloaded));

                                holder.itemView.findViewById(R.id.circleProgressView).setVisibility(View.GONE);

                            } else {

                                ((ImageView) holder.itemView.findViewById(R.id.downloadStatusIV)).setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_download));

                                holder.itemView.findViewById(R.id.circleProgressView).setVisibility(View.VISIBLE);

                                ((CircleProgressView) holder.itemView.findViewById(R.id.circleProgressView)).setValue(values[0]);

                            }

                        }

                    }

                    break;

                case DownloadService.DWL_COMPLT:
                    removeItemUpdating(msg.arg1);
                    getTracksListFromTable();
                    break;

                case DownloadService.DWL_INFO:
                    break;

                case DownloadService.DWL_FAILED:
                    removeItemUpdating(msg.arg1);
                    break;

                case DownloadService.DWL_CANCEL:
                    removeItemUpdating(msg.arg1);
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        doUnbindService();

    }


    private void removeItemUpdating(int position) {
        try {

            int pos = adapter.getProgressPosition(String.valueOf(position));

            adapter.removeItemUpdating(pos);

            adapter.notifyItemChanged(pos);

            adapter.notifyDataSetChanged();

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public void setItemUpdating(int position, boolean isUpdating, Long... values) {

        int pos = adapter.getProgressPosition(String.valueOf(position));

        if (pos == -1) {

            return;

        }

        RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(pos);

        if (null != holder) {

            holder.itemView.findViewById(R.id.pc_dprg).setVisibility(View.VISIBLE); //showProgressBar();

            ((ProgressControl) holder.itemView.findViewById(R.id.pc_dprg)).updateProgressState(values);

            adapter.setItemUpdating(pos, isUpdating, values);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, boolean permissionState) {
        super.onRequestPermissionsResult(requestCode, permissionState);

        initDownload(selectedJSON);

    }

    private void initDownload(JSONObject jsonObject) {

        if (jsonObject.optString("downloadstatus").equalsIgnoreCase("P")) {

            mActivity.showToast(R.string.dip);

            return;

        }

        if (jsonObject.optString("downloadstatus").equalsIgnoreCase("Q")) {

            mActivity.showToast(R.string.maatdq);

            return;

        }

        if (jsonObject.optString("downloadstatus").equalsIgnoreCase("C")) {

            String savedpath = jsonObject.optString("savedpath");

            if (savedpath.length() > 0) {

                File file = new File(savedpath);

                if (file.exists()) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    Uri apkURI = FileProvider.getUriForFile(mActivity, mActivity.getApplicationContext().getPackageName() + ".provider", file);

                    intent.setDataAndType(apkURI, jsonObject.optString("mimetype"));

                    //intent.setData(apkURI);

                    intent.addFlags(Intent.
                            FLAG_GRANT_READ_URI_PERMISSION);

                    //intent.setDataAndType(Uri.fromFile(file), jsonObject.optString("mimetype"));

                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    startActivity(intent);

                    return;

                } else {

                    mActivity.showokPopUp(R.drawable.pop_ic_failed, "", mActivity.getString(R.string.fhbddywtdia), 1, this, true);

                    return;

                }

            }

        }

        long val = getDataBaseObject().insertSingleRecords(jsonObject, "DOWNLOADQUEUE");

        if (val > 0) {

            startDownloading(jsonObject);

        }

        getTracksListFromTable();

    }

    public long getFolderSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                size += file.length();
            } else
                size += getFolderSize(file);
        }
        return size;
    }
    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getActivity()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getActivity().getTaskId(), 0);
    }
}
