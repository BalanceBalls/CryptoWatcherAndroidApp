package com.example.admin.cryptowatcher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Alex on 20.01.2018.
 */

public class NewsActivity extends AppCompatActivity {

    /*This activity displays news from the forklog. Just a web site inside a WebView */
    WebView webView;
    String appWebViewTempUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_screen);
        final Toolbar toolbarNews = (Toolbar) findViewById(R.id.toolbarNews);

        //Toolbar init block
        if(toolbarNews != null){
            toolbarNews.setTitle("Новости");
            setSupportActionBar(toolbarNews);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        webView = (WebView) findViewById(R.id.pageView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Android");
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://forklog.com/news/");

    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(NewsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
           webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class LoadWebPage extends AsyncTask<String, Void, String> {
        ProgressDialog newsProgressDialog;
        String webPage;
        public LoadWebPage(String siteURL) {
            super();
            webPage = siteURL;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


                newsProgressDialog = new ProgressDialog(
                    NewsActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);                    //set style for progressDialog
                newsProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);                        //set spinner
                newsProgressDialog.setMessage("Загружаю. Подождите...");//H--C
                newsProgressDialog.setCanceledOnTouchOutside(false);
                newsProgressDialog.show();

        }

        @Override
        protected String doInBackground(String...urls) {


            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            if (newsProgressDialog.isShowing()){
                newsProgressDialog.dismiss();

            }
        }
    }
    @Override
    protected void onResume(){
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        releaseWebView();
    }
    public void onDestroy() {
        super.onDestroy();
        Log.d("NewsAc", "On DESTROY called");


    }
    private void releaseWebView() {



        if(webView != null){


            webView.clearHistory();

            webView.clearCache(true);

            webView.loadUrl("about:blank");

            webView.onPause();

            webView.removeAllViews();

            webView.destroyDrawingCache();

            webView.pauseTimers();

            webView.destroy();

            webView = null;
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
