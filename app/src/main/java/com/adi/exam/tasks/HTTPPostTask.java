package com.adi.exam.tasks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.adi.exam.R;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.common.AppSettings;
import com.adi.exam.common.CacheManager;
import com.adi.exam.common.Item;
import com.adi.exam.common.MixUpValue;
import com.adi.exam.utils.LogUtils;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class HTTPPostTask {

    private Context mContext;

    private IItemHandler callback;

    private int requestId = -1;

    private boolean progressFlag = true;

    private boolean noRetries = false;

    private Item mHeaders = null;

    private Object obj = null;

    private Dialog dialog = null;

    private GetConnection getConn = null;

    private int cacheType = 0;

    private CacheManager mCacheManager = null;

    private long uid = 0;

    private boolean isRequestCancelled = false;

    private long startTime;

    public HTTPPostTask(Context aContext, IItemHandler callback) {
        this.mContext = aContext;
        this.callback = callback;
    }

    public void disableProgress() {
        progressFlag = false;
    }

    public void enableProgress() {
        progressFlag = true;
    }

    public void setNoRetries(boolean aNoRetries) {
        this.noRetries = aNoRetries;
    }

    public void setHeaders(Item aItem) {

        mHeaders = aItem;

    }

    public void setCacheType(int aCacheType) {
        cacheType = aCacheType;
    }

    private String networkType = "mobile";

    public void userRequest(String progressMsg, int requestId, final String urlKey, final String postData) {

        this.requestId = requestId;

        if (progressFlag)
            showProgress(mContext);

        if (cacheType == 0)
            if (!isNetworkAvailable()) {
                showUserActionResult(-1, mContext.getString(R.string.nipcyns));
                return;
            }

        new Thread(new Runnable() {

            @Override
            public void run() {

                HttpURLConnection conn = null;

                DataOutputStream outputStream = null;

                InputStream inputStream = null;

                try {

                    uid = System.currentTimeMillis();

                    String requestUrl = urlEncode(AppSettings.getInstance().getPropertyValue(urlKey));

                    if (cacheType != 0) {

                        mCacheManager = CacheManager.getInstance();

                        obj = mCacheManager.getCache(mContext, requestUrl, cacheType);

                        if (obj != null) {

                            postUserAction(0, "");

                            return;

                        }

                    }

                    if (!isNetworkAvailable()) {

                        showUserActionResult(-1, mContext.getString(R.string.nipcyns));

                        return;

                    }

                    startTime = System.currentTimeMillis();

                    getConn = new GetConnection(mContext);

                    getConn.setRequestMethod("POST");

                    getConn.setRequestHeaders(mHeaders);

                    getConn.setNoRetries(noRetries);

                    getConn.setNetworkType(networkType);

                    getConn.setHashingData(postData);

                    getConn.setUniqueId(uid);

                    conn = getConn.getHTTPConnection(requestUrl, AppPreferences.getInstance(mContext).getFromStore("oauth").trim());

                    if (isRequestCancelled)
                        return;

                    if (conn == null) {

                        TraceUtils.logCrashlytics(new Exception("Connection Object is Null"), urlKey, System.currentTimeMillis() - startTime,-1);

                        postUserAction(-1, mContext.getString(R.string.isr));

                        return;

                    }

                    inputStream = new ByteArrayInputStream(postData.getBytes());

                    outputStream = new DataOutputStream(conn.getOutputStream());

                    byte[] data = new byte[1024];

                    int bytesRead;

                    while ((bytesRead = inputStream.read(data)) != -1) {

                        outputStream.write(data, 0, bytesRead);

                    }

                    if (isRequestCancelled)
                        return;

                    int serverResponseCode = conn.getResponseCode();

                    TraceUtils.logCrashlytics(new Exception("API Time Capture"), urlKey, System.currentTimeMillis() - startTime,serverResponseCode);

                    if (serverResponseCode == 200) {

                        inputStream = conn.getInputStream();

                        byte[] bytebuf = new byte[0x1000];
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        for (; ; ) {
                            int len = inputStream.read(bytebuf);
                            if (len < 0)
                                break;
                            baos.write(bytebuf, 0, len);
                        }

                        bytebuf = baos.toByteArray();

                        obj = new String(bytebuf, "UTF-8");

                        if (cacheType != 0) {

                            String tempURL = requestUrl;

                            if (AppPreferences.getInstance(mContext).getFromStore("language").equalsIgnoreCase("ar")) {
                                tempURL = tempURL + "/";
                            }

                            mCacheManager.setCache(mContext, tempURL, cacheType, obj);
                        }

                        TraceUtils.logE("Request URL", requestUrl);
                        TraceUtils.logE("Request body", postData);
                        TraceUtils.logE("Response", obj.toString());

                        if (obj != null) {

                            /*JSONObject jsonObject = new JSONObject(obj.toString());

                            String tid = jsonObject.optString("transid");
                            String temp = jsonObject.optString("status") + "#" + tid;

                            MixUpValue mixUpValue = new MixUpValue();

                            temp = temp + "&SALT=" + mixUpValue.getValues(uid + "");

                            temp = mixUpValue.encryption(temp);

                            if (!temp.equalsIgnoreCase(conn.getHeaderField("X-IMI-HASH"))) {

                                postUserAction(-1, "");

                                return;

                            }*/

                        }

                        logRequestLocally(mContext, AppSettings.getInstance().getPropertyValue(urlKey), postData, "", obj, "", "", 0);

                        postUserAction(0, "");

                        return;

                    }

                    String serverResponseMessage = conn.getResponseMessage();

                    postUserAction(-1, serverResponseMessage);

                } catch (MalformedURLException me) {

                    logRequestLocally(mContext, AppSettings.getInstance().getPropertyValue(urlKey), postData, "", "", "ConnectException", me.getLocalizedMessage()+"", 0);

                    TraceUtils.logCrashlytics(me, urlKey, System.currentTimeMillis() - startTime,-1);

                    postUserAction(-1, mContext.getString(R.string.iurl));

                } catch (ConnectException e) {

                    logRequestLocally(mContext, AppSettings.getInstance().getPropertyValue(urlKey), postData, "", "", "ConnectException", e.getLocalizedMessage()+"", 0);

                    TraceUtils.logCrashlytics(e, urlKey, System.currentTimeMillis() - startTime,-1);

                    postUserAction(-1, mContext.getString(R.string.snr1));

                } catch (SocketException se) {

                    logRequestLocally(mContext, AppSettings.getInstance().getPropertyValue(urlKey), postData, "", "", "SocketException", se.getLocalizedMessage()+"", 0);

                    TraceUtils.logCrashlytics(se, urlKey, System.currentTimeMillis() - startTime,-1);

                    postUserAction(-1, mContext.getString(R.string.snr2));

                } catch (SocketTimeoutException stex) {

                    logRequestLocally(mContext, AppSettings.getInstance().getPropertyValue(urlKey), postData, "", "", "SocketTimeoutException", stex.getLocalizedMessage()+"", 0);

                    TraceUtils.logCrashlytics(stex, urlKey, System.currentTimeMillis() - startTime,-1);

                    postUserAction(-1, mContext.getString(R.string.sct));

                } catch (Exception ex) {

                    logRequestLocally(mContext, AppSettings.getInstance().getPropertyValue(urlKey), postData, "", "", "Exception", ex.getLocalizedMessage()+"", 0);

                    TraceUtils.logCrashlytics(ex, urlKey, System.currentTimeMillis() - startTime,-1);

                    postUserAction(-1, mContext.getString(R.string.snr3));

                } finally {

                    if (inputStream != null)

                        try {

                            inputStream.close();

                            inputStream = null;

                        } catch (IOException e) {

                            TraceUtils.logException(e);

                        }

                    if (outputStream != null)

                        try {

                            outputStream.close();

                            outputStream = null;

                        } catch (IOException e) {

                            TraceUtils.logException(e);

                        }

                    if (conn != null)
                        conn.disconnect();

                    conn = null;

                    if (getConn != null)
                        getConn.clearConn();

                    getConn = null;

                }
            }
        }).start();
    }

    private void postUserAction(final int status, final String response) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (isRequestCancelled)
                    return;

                showUserActionResult(status, response);

            }
        });
    }

    private void showUserActionResult(int status, String response) {

        dismissProgress();

        if (isRequestCancelled)
            return;

        switch (status) {
            case 0:
                callback.onFinish(obj, requestId);
                break;

            case -1:
                callback.onError(response, requestId);
                break;

            default:
                break;
        }

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager == null) {

            return false;

        }

        NetworkInfo net = manager.getActiveNetworkInfo();

        if (net != null) {

            networkType = net.getTypeName();

            return net.isConnected();

        }

        return false;

    }

    private String urlEncode(String sUrl) {

        int i = 0;

        String urlOK = "";

        while (i < sUrl.length()) {

            if (sUrl.charAt(i) == ' ') {

                urlOK = urlOK + "%20";

            } else {

                urlOK = urlOK + sUrl.charAt(i);

            }

            i++;

        }

        return (urlOK);

    }

    private void showProgress(Context context) {

        try {

            dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            if (dialog.getWindow() != null) {

                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));

            }

            dialog.setCancelable(false);

            View view = View.inflate(context, R.layout.processing, null);

            dialog.setContentView(view);

            dialog.show();

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }

    private void dismissProgress() {

        try {

            if (dialog != null)
                dialog.dismiss();

            dialog = null;

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void logRequestLocally(final Context context, final String reqAPI, final String reqBody, final String reqHeaders, final Object response, final String excepTitle, final String excepMsg, final long resTime) {
        LogUtils.logRequestLocally(context, reqAPI, reqBody, reqHeaders, response, excepTitle, excepMsg, resTime, "");
    }

}
