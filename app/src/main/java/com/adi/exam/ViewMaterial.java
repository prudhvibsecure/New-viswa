package com.adi.exam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ViewMaterial extends AppCompatActivity {

    private String student_id, url;

    private WebView wv_content = null;

    private WebSettings webSettings = null;

    //PDFView view ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_material);

       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        Intent in = getIntent();
        if(in!= null)
        {
           url = in.getStringExtra("url");
        }
        /*view = findViewById(R.id.pdfView);
        Uri uri = Uri.parse(url);
        view.fromUri(uri);
        PDFView.Configurator.onRender(new OnRenderListener() {
            @Override
            public void onInitiallyRendered(int pages, float pageWidth, float pageHeight) {
                view.fitToWidth(); // optionally pass page number
            }
        });*/

        wv_content = findViewById(R.id.webview);

     //   wv_content.getSettings().setAllowFileAccess(true);
        wv_content.getSettings().setSupportZoom(true);

       // wv_content.getSettings().setUseWideViewPort(true);
        wv_content.getSettings().setJavaScriptEnabled(true);

        wv_content.setWebViewClient(new ViewMaterial.MyWebViewClient());
        wv_content.setWebChromeClient(new ViewMaterial.MyWebChromeClient());
//        try {
//            url= URLEncoder.encode(url,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        wv_content.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);
        //wv_content.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+url);
        //url = "https://decapoda.nhm.org/pdfs/11686/11686.pdf";
        //String doc="<iframe src='https://decapoda.nhm.org/pdfs/11686/11686.pdf' embedded=true width='100%' height='100%' style='border: none;'></iframe>";
        //wv_content.loadUrl(doc);
        //wv_content.loadData( doc , "text/html",  "UTF-8");
    }


    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            // Log.e("-=-=-=-=-=-", newProgress + "");

            if (newProgress == 5)
                findViewById(R.id.pb_allpg).setVisibility(View.VISIBLE);

            if (newProgress >= 95) {
                findViewById(R.id.pb_allpg).setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            findViewById(R.id.pb_allpg).setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
