package com.adi.exam.tasks;

import android.content.Context;

import com.adi.exam.common.Item;
import com.adi.exam.common.MixUpValue;
import com.adi.exam.common.ProjectHeaders;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Enumeration;

class GetConnection {

    private Context context;

    private Item headers = null;

    private String requestMethod = "GET";

    private String mData;

    private String networkType = "mobile";

    private long uid = 0;

    private boolean noRetries = false;

    GetConnection(Context context) {
        this.context = context;
    }

    void setRequestHeaders(Item item) {
        this.headers = item;
    }

    void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    void setNoRetries(boolean aNoRetries) {
        this.noRetries = aNoRetries;
    }

    void setHashingData(String aData) {
        this.mData = aData;
    }

    void setUniqueId(long aUid) {
        this.uid = aUid;
    }

    void setNetworkType(String aNetworkType) {
        networkType = aNetworkType;
    }

    HttpURLConnection getHTTPConnection(String url, String val) throws Exception {

        HttpURLConnection _conn;
        URL serverAddress;
        int socketExepCt = 0;
        int ExepCt = 0;
        int numRedirect = 0;
        int initval = 0;

        if (noRetries) {
            socketExepCt = 1;
            ExepCt = 1;
            initval = 1;
        }

        serverAddress = new URL(url);
        String host = "http://" + serverAddress.getHost() + "/";

        Item defaultItem = (Item) ProjectHeaders.getInstance().getHeaders(context).clone();

        if (val.length() == 0)
            val = defaultItem.getAttribute("X-IMI-TOKENID").trim();

        MixUpValue mixUpValue = new MixUpValue();

        if (mData != null) {

            mData = mData + "&SALT=" + mixUpValue.getValues(val);

        }

        for (int i = initval; i <= 1; i++) {

            try {

                _conn = (HttpURLConnection) serverAddress.openConnection();
                if (_conn != null) {
                    _conn.setRequestMethod(requestMethod);
                    _conn.setReadTimeout(60000);
                    _conn.setConnectTimeout(2500);
                    _conn.setInstanceFollowRedirects(false);
                    _conn.setDoOutput(false);

                    if (requestMethod.equalsIgnoreCase("POST")) {
                        _conn.setRequestMethod("POST");
                        _conn.setDoInput(true);
                        _conn.setDoOutput(true);
                        _conn.setUseCaches(false);
                        _conn.setRequestProperty("Connection", "close");
                        _conn.setRequestProperty("Connection", "Keep-Alive");
                        _conn.setReadTimeout(60000);
                        _conn.setConnectTimeout(2500);
                        _conn.setRequestProperty("Content-Type", "application/json");
                    }

                    if (mData != null) {
                        _conn.setRequestProperty("X-IMI-SIGNATURE", mixUpValue.encryption(mData));
                    }

                    if (defaultItem != null) {
                        Enumeration keys = defaultItem.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement().toString();
                            String value = defaultItem.get(key).toString();
                            _conn.setRequestProperty(key, value);
                        }
                    }

                    if (headers != null) {
                        Enumeration keys = headers.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement().toString();
                            String value = headers.get(key).toString();
                            _conn.setRequestProperty(key, value);
                        }
                    }

                    _conn.setRequestProperty("X-IMI-UID", uid + "");

                    _conn.setRequestProperty("X-IMI-NETWORK", networkType + "");

                    int RESCODE;
                    _conn.connect();

                    if (requestMethod.equalsIgnoreCase("POST"))
                        return _conn;

                    RESCODE = _conn.getResponseCode();
                    if (RESCODE == HttpURLConnection.HTTP_OK || RESCODE == HttpURLConnection.HTTP_PARTIAL) {
                        return _conn;
                    } else if (RESCODE == HttpURLConnection.HTTP_MOVED_TEMP
                            || RESCODE == HttpURLConnection.HTTP_MOVED_PERM) {
                        if (numRedirect > 15) {
                            _conn.disconnect();
                            _conn = null;
                            break;
                        }

                        numRedirect++;
                        i = 0;
                        url = _conn.getHeaderField("Location");
                        if (!url.startsWith("http")) {
                            url = host + url;
                        }

                        _conn.disconnect();
                        _conn = null;

                    } else {
                        _conn.disconnect();
                        _conn = null;
                    }
                }
            } catch (MalformedURLException me) {
                throw me;
            } catch (SocketTimeoutException se) {
                _conn = null;
                if (i >= 1)
                    throw se;
            } catch (SocketException s) {
                if (socketExepCt > 1) {
                    _conn = null;
                    throw s;
                }
                socketExepCt++;
                i = 0;
            } catch (Exception e) {
                if (ExepCt > 1) {
                    _conn = null;
                    throw e;
                }
                ExepCt++;
                i = 0;
            }
        }
        return null;
    }

    void clearConn() {

        if (headers != null)
            headers.clear();
        headers = null;

        requestMethod = null;

    }

}
