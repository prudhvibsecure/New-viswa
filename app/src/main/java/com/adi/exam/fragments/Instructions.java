package com.adi.exam.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONObject;

public class Instructions extends ParentFragment implements View.OnClickListener {

    private OnFragmentInteractionListener mFragListener;

    private View layout;

    private SriVishwa activity;

    private ProgressBar pb_instructions;

    private WebView wv_instructions;

    private JSONObject data;

    private boolean mShowProceed;

    public Instructions() {
        // Required empty public constructor
    }

    public static Instructions newInstance(String data, boolean showProceed) {

        Instructions fragment = new Instructions();

        Bundle args = new Bundle();

        args.putString("data", data);

        args.putBoolean("showProceed", showProceed);

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       try {

           if (getArguments() != null) {

               data = new JSONObject(getArguments().getString("data"));

               mShowProceed = getArguments().getBoolean("showProceed");

           }

       } catch (Exception e) {

           TraceUtils.logException(e);

       }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_instructions, container, false);

        layout.findViewById(R.id.tv_proceed).setOnClickListener(this);

        pb_instructions = layout.findViewById(R.id.pb_instructions);

        wv_instructions = layout.findViewById(R.id.wv_instructions);

        wv_instructions.setVerticalScrollBarEnabled(true);

        wv_instructions.getSettings().setJavaScriptEnabled(true);

        wv_instructions.getSettings().setSupportZoom(false);

        wv_instructions.setWebViewClient(new MyWebViewClient());

        wv_instructions.setWebChromeClient(new WebChromeClient());

        wv_instructions.getSettings().setLoadWithOverviewMode(true);

        wv_instructions.getSettings().setUseWideViewPort(false);

        wv_instructions.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        wv_instructions.getSettings().setSupportZoom(true);

        wv_instructions.getSettings().setBuiltInZoomControls(true);

        wv_instructions.getSettings().setDisplayZoomControls(false);

        wv_instructions.loadUrl("about:blank");

        wv_instructions.clearFormData();

        wv_instructions.clearCache(true);

        wv_instructions.clearHistory();

        clearCookies(activity);

        wv_instructions.clearMatches();

        wv_instructions.clearSslPreferences();

        wv_instructions.requestFocus(View.FOCUS_DOWN);

        String localPath = "";

        if(data.optString("course").equalsIgnoreCase("1") || data.optString("course").equalsIgnoreCase("2")) {

            localPath = "file:///android_asset/JEE/jee.html";

        } else if(data.optString("course").equalsIgnoreCase("3")) {

            localPath = "file:///android_asset/BITSAT/bitsat0.html";

        } else if(data.optString("course").equalsIgnoreCase("4") || data.optString("course").equalsIgnoreCase("8")) {

            localPath = "file:///android_asset/EAMCET/eamcet.html";

        } else if(data.optString("course").equalsIgnoreCase("5")) {

            localPath = "file:///android_asset/NEET/neet.html";

        }  else if(data.optString("course").equalsIgnoreCase("6")) {

            localPath = "file:///android_asset/AIIMS/aiims.html";

        }  else if(data.optString("course").equalsIgnoreCase("7")) {

            localPath = "file:///android_asset/JIPMER/jipmer.html";

        } else if(data.optString("course").equalsIgnoreCase("9")) {

            localPath = "file:///android_asset/KVPY/kvpy.html";

        }

        wv_instructions.loadUrl(localPath);

        if(!mShowProceed) {

            layout.findViewById(R.id.cb_instructions).setVisibility(View.GONE);

            layout.findViewById(R.id.tv_proceed).setVisibility(View.GONE);

        }

        return layout;

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mFragListener = (SriVishwa) context;

        activity = (SriVishwa) context;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        mFragListener.onFragmentInteraction(R.string.gi, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragListener.onFragmentInteraction(R.string.gi, false);

    }

    @Override
    public void onClick(View v) {

        try {

            if (((CheckBox) layout.findViewById(R.id.cb_instructions)).isChecked()) {

                activity.onKeyDown(4, null);

                activity.showExamTemplate(data.toString());

                return;

            }

            activity.showokPopUp(R.drawable.pop_ic_info, activity.getString(R.string.nonettitle), activity.getString(R.string.patacbp));

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    private void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

            CookieManager.getInstance().removeAllCookies(null);

            CookieManager.getInstance().flush();

        } else {

            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);

            cookieSyncMngr.startSync();

            CookieManager cookieManager = CookieManager.getInstance();

            cookieManager.removeAllCookie();

            cookieManager.removeSessionCookie();

            cookieSyncMngr.stopSync();

            cookieSyncMngr.sync();

        }

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

            pb_instructions.setVisibility(View.GONE);

            try {

                showError(errorCode);

                view.loadUrl("about:blank");

                view.loadUrl("javascript:document.open();document.close();");

            } catch (Exception e) {
                TraceUtils.logException(e);
            }

        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

            pb_instructions.setVisibility(View.GONE);

            try {
                showError(error.getErrorCode());
            } catch (Exception e) {
                TraceUtils.logException(e);
            }

        }

    }

    private class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress >= 1)
                pb_instructions
                        .setVisibility(View.VISIBLE);

            if (newProgress >= 95) {
                pb_instructions
                        .setVisibility(View.GONE);
            }

            super.onProgressChanged(view, newProgress);
        }
    }


    private void showError(int errorCode) {

        try {

            String message = null;
            String title = null;
            if (errorCode == WebViewClient.ERROR_AUTHENTICATION) {
                message = "User authentication failed on server";
                title = "Auth Error";
            } else if (errorCode == WebViewClient.ERROR_TIMEOUT) {
                message = "The server is taking too much time to communicate. Try again later.";
                title = "Connection Timeout";
            } else if (errorCode == WebViewClient.ERROR_TOO_MANY_REQUESTS) {
                message = "Too many requests during this load";
                title = "Too Many Requests";
            } else if (errorCode == WebViewClient.ERROR_UNKNOWN) {
                //message = "Generic error";
                //title = "Unknown Error";
                return;
            } else if (errorCode == WebViewClient.ERROR_BAD_URL) {
                message = "Check entered URL..";
                title = "Malformed URL";
            } else if (errorCode == WebViewClient.ERROR_CONNECT) {
                message = "Failed to connect to the server";
                title = "Connection";
            } else if (errorCode == WebViewClient.ERROR_FAILED_SSL_HANDSHAKE) {
                message = "Failed to perform SSL handshake";
                title = "SSL Handshake Failed";
            } else if (errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
                message = "Server or proxy hostname lookup failed";
                title = "Host Lookup Error";
            } else if (errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION) {
                message = "User authentication failed on proxy";
                title = "Proxy Auth Error";
            } else if (errorCode == WebViewClient.ERROR_REDIRECT_LOOP) {
                message = "Too many redirects";
                title = "Redirect Loop Error";
            } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME) {
                message = "Unsupported authentication scheme (not basic or digest)";
                title = "Auth Scheme Error";
            } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME) {
                message = "Unsupported URI scheme";
                title = "URI Scheme Error";
            } else if (errorCode == WebViewClient.ERROR_FILE) {
                message = "Generic file error";
                title = "File";
            } else if (errorCode == WebViewClient.ERROR_FILE_NOT_FOUND) {
                message = "File not found";
                title = "File";
            } else if (errorCode == WebViewClient.ERROR_IO) {
                message = "The server failed to communicate. Try again later.";
                title = "IO Error";
            }

            if (message != null) {

                activity.showokPopUp(R.drawable.pop_ic_failed, title, message);

            }

        } catch (Exception e) {
            TraceUtils.logException(e);
        }
    }

    @Override
    public void onDetach() {
        try {

            mFragListener = null;

            wv_instructions.loadUrl("about:blank");

            wv_instructions.clearFormData();

            wv_instructions.clearCache(true);

            wv_instructions.clearHistory();

            clearCookies(activity);

           // activity.serRefresh();

            wv_instructions.clearMatches();

            wv_instructions.clearSslPreferences();

            wv_instructions.removeJavascriptInterface("jsInterface");

        } catch (Exception e) {

            TraceUtils.logException(e);

        }
        super.onDetach();
    }

}
