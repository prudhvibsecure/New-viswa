package com.adi.exam.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.Gravity;
import android.widget.Toast;

import com.adi.exam.R;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.common.Item;
import com.adi.exam.common.NetworkInfoAPI;
import com.adi.exam.database.App_Table;
import com.adi.exam.tasks.HTTPTaskDownload;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONObject;

import java.util.Hashtable;

public class DownloadService extends Service implements IItemHandler {

    private Messenger mMessenger = new Messenger(new IncomingHandler());

    private Messenger client = null;

    public static final int MSG_REGISTER_CLIENT = 1;

    public static final int MSG_UNREGISTER_CLIENT = 2;

    public static final int DWL_START = 3;

    public static final int DWL_PROGS = 4;

    public static final int DWL_CANCEL = 5;

    public static final int DWL_FAILED = 6;

    public static final int DWL_COMPLT = 7;

    public static final int DWL_INFO = 8;

    public static final int MSG_INFO_CLIENT = 9;

    public static final int DWL_STOP = -2;

    private HTTPTaskDownload task = null;

    private App_Table table = null;

    private NetworkInfoAPI networkInfoAPI = null;

    private MediaScannerConnection mediaScannerConnection;

    private JSONObject jsonObject = null;

    private Hashtable<Integer, HTTPTaskDownload> requestItems = new Hashtable<>();

    private Hashtable<Integer, JSONObject> requestData = new Hashtable<>();

    @Override
    public void onCreate() {

        super.onCreate();
        networkInfoAPI = new NetworkInfoAPI();
        networkInfoAPI.initialize(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (intent != null) {

                if (intent.getAction().equals("com.ooredoo.music.action.addcontent")) {

                    checkDBNInitDownload(intent.getStringExtra("data"), intent.getIntExtra("requestId", -1));

                } else if (intent.getAction().equals("com.ooredoo.music.action.removecontent")) {

                    removeDownloadingContent(intent.getIntExtra("requestId", -1));

                }

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

        return START_STICKY; // run until explicitly stopped.

    }

    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        mMessenger = null;

        client = null;

        if (networkInfoAPI != null)
            networkInfoAPI.onDestroy();

        networkInfoAPI = null;

        if (task != null) {

            task.cancelRequest();

            task = null;

        }

    }

    private void stopIt() {

        if (client == null)
            stopSelf();

        if (client != null) {
            sendMessageToUI(DWL_STOP, 0, 0, null);
            stopSelf();
            return;
        }

        sendMessageToUI(DWL_STOP, 0, 0, null);

    }


    class IncomingHandler extends Handler { // Handler of incoming messages from

        // clients.
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MSG_REGISTER_CLIENT:
                    client = msg.replyTo;

                    if (jsonObject != null) {
                        int count = 0;

                        //sendMessageToUI(DownloadService.DWL_START, currentCount, totalCount, jsonObject.optString("title"));
                        //sendMessageToUI(DownloadService.DWL_START, 1, count, currentItem.getAttribute("TITLE"));
                    } else {
                        //sendMessageToUI(DownloadService.DWL_START, 1, 1, "");
                    }


                    break;

                case MSG_UNREGISTER_CLIENT:
                    client = null;
                    break;

                case MSG_INFO_CLIENT:

                    int count = msg.arg1;

                    //totalCount = count + (currentCount - 1);
                    //sendMessageToUI(DownloadService.DWL_INFO, currentCount, totalCount, "");

                    break;

                default:
                    super.handleMessage(msg);
            }
        }

    }

    private void sendMessageToUI(int state, int value, int value1, Object object) {

        try {
            if (client == null) {
                return;
            }

            client.send(Message.obtain(null, state, value, value1, object));
        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }

    public void showToast(int value) {

        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String value) {

        Toast toast = Toast.makeText(this, value, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    private App_Table getDataBaseObject() {

        if (table == null)
            table = new App_Table(this);

        return table;
    }

    class ScanContent implements MediaScannerConnectionClient {

        private String filePath = "";

        ScanContent(Item item, String filePath) {

            this.filePath = filePath;
        }

        @Override
        public void onMediaScannerConnected() {

            try {
                mediaScannerConnection.scanFile(filePath, null);
            } catch (Exception e) {
                TraceUtils.logException(e);
            }

        }

        @Override
        public void onScanCompleted(String path, Uri uri) {

            mediaScannerConnection.disconnect();
        }

        private void scanFile() {

            if (mediaScannerConnection != null)
                mediaScannerConnection.disconnect();

            mediaScannerConnection = new MediaScannerConnection(DownloadService.this, this);
            mediaScannerConnection.connect();
        }

    }

    private void removeDownloadingContent(int requestId) {

        //Starting next item download.
        table = getDataBaseObject();

        JSONObject nextObj = table.getTrack(AppPreferences.getInstance(this).getFromStore("userid"));

        if (nextObj != null && nextObj.optString("id") != null && Integer.parseInt(nextObj.optString("id")) != requestId) {

            checkDBNInitDownload(nextObj.toString(), Integer.parseInt(nextObj.optString("id")));

        }

        HTTPTaskDownload httptask = requestItems.get(requestId);

        if (httptask != null) {

            httptask.cancelRequest();

            requestItems.remove(requestId);

            httptask = null;

        }

    }

    private void checkDBNInitDownload(String data, int requestId) {

        try {

            if (requestItems.size() > 4) {
                return;
            }

            jsonObject = new JSONObject(data);

            table = getDataBaseObject();

            table.updateDownloadStatus(String.valueOf(requestId), "P");

            table.updateErrMsgOnFail(String.valueOf(requestId), "");

            HTTPTaskDownload task = new HTTPTaskDownload(this.getApplicationContext(), this);
            task.disableProgress();
            task.setFileName(jsonObject.optString("material_original_name"));
            task.saveableFile();

            String fileLink = "https://bsecuresoftechsolutions.com/viswa/material/material_download/1561440728293_VedicReport220201992246AM.pdf";

            task.userRequest("Please wait...", requestId, fileLink/*jsonObject.optString("downloadurl").trim()*/);
            sendMessageToUI(DWL_START, requestId, 0, null);

            requestItems.put(requestId, task);
            requestData.put(requestId, jsonObject);

        } catch (Exception e) {
            TraceUtils.logException(e);
        }
    }

    @Override
    public void onFinish(Object results, int requestId) {

        if (requestId == -1) {
            return;
        }

        try {

            if (results != null) {

                String data = results.toString();

                String[] temp = data.split("##");

                requestItems.remove(requestId);

                JSONObject jsonObject = requestData.get(requestId);

                jsonObject.put("savedpath", temp[0]);

                jsonObject.put("mimetype", temp[2]);

                jsonObject.put("downloadstatus", "C");

                table = getDataBaseObject();

                table.update(String.valueOf(requestId), temp[0], temp[2], "C");

                table.updateErrMsgOnFail(String.valueOf(requestId), "");

                requestData.remove(requestId);

                showToast(jsonObject.optString("title") + " " + getString(R.string.downloaded_successfully));

                sendMessageToUI(DWL_COMPLT, requestId, 0, jsonObject);

                JSONObject nextObj = table.getTrack(AppPreferences.getInstance(this).getFromStore("userid"));

                if (nextObj != null) {
                    checkDBNInitDownload(nextObj.toString(), Integer.parseInt(nextObj.optString("id")));
                    return;
                }

                if (requestItems.size() == 0) {
                    this.stopSelf();
                }
            }

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }

    @Override
    public void onError(String errorData, int requestId) {

        requestData.remove(requestId);

        requestItems.remove(requestId);

        table = getDataBaseObject();

        table.updateDownloadStatus(String.valueOf(requestId), "F");

        if (errorData.equalsIgnoreCase(getString(R.string.yhctd))) {

            sendMessageToUI(DWL_CANCEL, requestId, 0, errorData);

            showToast(getString(R.string.yhctd));

            table.updateErrMsgOnFail(String.valueOf(requestId), getString(R.string.yhctd));

            return;

        }

        table.updateErrMsgOnFail(String.valueOf(requestId), getString(R.string.dfptal));

        sendMessageToUI(DWL_FAILED, requestId, 0, errorData);

        showToast(errorData);

        JSONObject nextObj = table.getTrack(AppPreferences.getInstance(this).getFromStore("userid"));

        if (nextObj != null) {

            checkDBNInitDownload(nextObj.toString(), Integer.parseInt(nextObj.optString("id")));

            return;

        }

        if (requestItems.size() == 0) {

            this.stopSelf();

        }

    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

        sendMessageToUI(DWL_PROGS, requestId, 0, values);
    }



}
