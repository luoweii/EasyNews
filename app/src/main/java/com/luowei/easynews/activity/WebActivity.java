package com.luowei.easynews.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;

import com.luowei.easynews.R;

import butterknife.Bind;

/**
 * Created by luowei on 2015/8/20.
 */
public class WebActivity extends BaseActivity {
    @Bind(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 显示返回按钮

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) webView.loadUrl(url);
    }

    public static void startActivity(Context context,String url) {
        Intent it = new Intent(context, WebActivity.class);
        it.putExtra("url", url);
        context.startActivity(it);
    }
}
