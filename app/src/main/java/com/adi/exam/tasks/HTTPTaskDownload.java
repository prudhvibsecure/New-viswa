package com.adi.exam.tasks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;

import com.adi.exam.R;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.Item;
import com.adi.exam.utils.TraceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class HTTPTaskDownload {

    private Context context;

    private IItemHandler callback;

    private int requestId = -1;

    private boolean progressFlag = true;

    private Dialog dialog = null;

    private boolean mSaveAbleFile = false;

    private long length = 0;

    private long mDownloadStartTime = 0;

    private boolean isRequestCancelled = false;

    private GetConnection getConn = null;

    private Item mHeaders;

    private String PATH = Environment.getExternalStorageDirectory().toString();

    private final String DWLPATH = PATH + "/SriVishwa/downloads/";

    private String fileName = "";

    public HTTPTaskDownload(Context context, IItemHandler callback) {

        this.context = context;

        this.callback = callback;

    }

    public void saveableFile() {

        mSaveAbleFile = true;
    }

    public void setFileName(String aFileName) {

        fileName = aFileName;

    }

    public void disableProgress() {

        progressFlag = false;
    }

    public void userRequest(String progressMsg, int requestId, final String url) {

        this.requestId = requestId;

        TraceUtils.logE("request URL: ", url + "");

        if (progressFlag)
            showProgress(progressMsg, context);

        if (!isNetworkAvailable()) {
            showUserActionResult(-1, context.getString(R.string.nipcyns));
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {

                HttpURLConnection conn = null;
                InputStream inputStream = null;
                ByteArrayOutputStream baos = null;

                try {

                    String link = url.replace("http://", "https://");

                    link = urlEncode(link);

                    getConn = new GetConnection(context);

                    getConn.setRequestMethod("GET");

                    getConn.setRequestHeaders(mHeaders);

                    conn = getConn.getHTTPConnection(link, "");

                    if (conn == null) {
                        postUserAction(-1, context.getString(R.string.isr));
                        return;
                    }

                    if (isRequestCancelled) {
                        throw new ConnectionClosed("connection closed");
                    }

                    mDownloadStartTime = System.currentTimeMillis();

                    inputStream = conn.getInputStream();

                    if (mSaveAbleFile) {

                        length = conn.getContentLength();

                        if (length == -1) {
                            String leng = conn.getHeaderField("content-length");// content-filesize
                            if (leng != null)
                                length = Long.parseLong(leng);

                            if (length == -1)
                                length = inputStream.available();
                        }

                        String mimeType = conn.getContentType();

                        if (mimeType.contains("pdf") || mimeType.contains("image") || mimeType.contains("msword") || mimeType.contains("officedocument")) {

                            TraceUtils.logE("mimeType", mimeType + "");

                            String contDisp = conn.getHeaderField("Content-Disposition");

                            String depoSplit[] = contDisp.split("filename=");

                            String fileName = depoSplit[1].replace("filename=", "").replace("\"", "").trim();

                            saveFile(fileName, inputStream);

                            postUserAction(0, DWLPATH + fileName + "##" + fileName + "##" + mimeType);

                            return;
                        }
                    }

                    byte[] bytebuf = new byte[0x1000];

                    baos = new ByteArrayOutputStream();
                    for (; ; ) {
                        int len = inputStream.read(bytebuf);
                        if (len < 0)
                            break;
                        baos.write(bytebuf, 0, len);
                    }

                    bytebuf = baos.toByteArray();

                    String response = new String(bytebuf, "UTF-8");

                    TraceUtils.logE("link-=-=--=-=-", link + "");
                    TraceUtils.logE("response-=-=--=-=-", response + "");

                    postUserAction(-1, response);

                } catch (ConnectionClosed cc) {
                    postUserAction(-2, context.getString(R.string.yhctd));
                } catch (MalformedURLException me) {
                    TraceUtils.logException(me);
                    postUserAction(-1, context.getString(R.string.iurl));
                } catch (ConnectException e) {
                    TraceUtils.logException(e);
                    postUserAction(-1, context.getString(R.string.snr1));
                } catch (SocketException se) {
                    TraceUtils.logException(se);
                    postUserAction(-1, context.getString(R.string.snr2));
                } catch (SocketTimeoutException stex) {
                    TraceUtils.logException(stex);
                    postUserAction(-1, context.getString(R.string.sct));
                } catch (Exception ex) {
                    TraceUtils.logException(ex);
                    postUserAction(-1, context.getString(R.string.snr3));
                } finally {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                            inputStream = null;
                        } catch (IOException e) {
                            TraceUtils.logException(e);
                        }

                    try {

                        if (baos != null) {
                            baos.close();
                            baos.flush();
                        }
                        baos = null;

                    } catch (Exception e) {
                        TraceUtils.logException(e);
                    }

                    if (conn != null)
                        conn.disconnect();
                    conn = null;
                }
            }
        }).start();
    }

    private void postUserAction(final int status, final String response) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                showUserActionResult(status, response);
            }
        });
    }

    private void showUserActionResult(int status, String response) {

        dismissProgress();

        switch (status) {
            case 0:
                callback.onFinish(response, requestId);
                break;

            case -2:
            case -1:
                callback.onError(response, requestId);
                break;

            default:
                break;
        }
    }

    private void publishProgress(final Long... values) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                callback.onProgressChange(requestId, values);
            }
        });
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo net = manager.getActiveNetworkInfo();
        return net != null && net.isConnected();
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

    private void showProgress(String title, Context context) {

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

    private void saveFile(String fileName, InputStream inputStream) throws Exception {

        int bufferSize = 4096;

        byte[] buffer = new byte[bufferSize];
        byte[] buffer1 = new byte[bufferSize];
        int bytesRead;
        long totalRead = 0;

        File f = new File(DWLPATH);
        if (!f.exists()) {
            f.mkdirs();
        }

        FileOutputStream outStream = null;

        try {

           // outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outStream = new FileOutputStream(DWLPATH + fileName);

            while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) >= 0) {

                if (isRequestCancelled) {

                    throw new ConnectionClosed("connection closed");

                }

                outStream.write(buffer, 0, bytesRead);

                totalRead += bytesRead;

                if (length <= 0)
                    length = inputStream.available();

                if (this.length > 0) {
                    Long[] progress = new Long[5];
                    progress[0] = (long) ((double) totalRead / (double) this.length * 100.0);
                    progress[1] = totalRead;
                    progress[2] = this.length;

                    double elapsedTimeSeconds = (System.currentTimeMillis() - mDownloadStartTime) / 1000.0;

                    // Compute the avg speed up to now
                    double bytesPerSecond = totalRead / elapsedTimeSeconds;

                    // How many bytes left?
                    long bytesRemaining = this.length - totalRead;

                    double timeRemainingSeconds;

                    if (bytesPerSecond > 0) {
                        timeRemainingSeconds = bytesRemaining / bytesPerSecond;
                    } else {
                        // Infinity so set to -1
                        timeRemainingSeconds = -1.0;
                    }

                    progress[3] = (long) (elapsedTimeSeconds * 1000);

                    progress[4] = (long) (timeRemainingSeconds * 1000);

                    publishProgress(progress);
                }

                if (isRequestCancelled) {
                    throw new ConnectionClosed("connection closed");
                }
            }

          /*  outStream = new FileOutputStream(DWLPATH + fileName);

            InputStream is = context.openFileInput(fileName);
            while ((bytesRead = is.read(buffer, 0, bufferSize)) >= 0) {
                for (int i = 0; i < buffer.length; i++) {
                    buffer1[buffer.length - (i + 1)] = buffer[i];
                }
                outStream.write(buffer1, 0, bytesRead);
            }*/

            buffer = null;

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (Exception e) {
                TraceUtils.logException(e);
            }
        }
    }

    private class ConnectionClosed extends Exception {

        ConnectionClosed(String message) {
            super(message);
        }
    }

    public void cancelRequest() {

        disableProgress();

        isRequestCancelled = true;

    }

}
