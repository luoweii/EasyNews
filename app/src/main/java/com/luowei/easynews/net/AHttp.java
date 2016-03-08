package com.luowei.easynews.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.luowei.easynews.Constant;
import com.luowei.easynews.utils.CommonUtil;
import com.luowei.easynews.utils.LogUtil;

/**
 * Created by 骆巍 on 2016/2/14.
 */
public class AHttp {
    public static HttpCache httpCache = new HttpCache();
    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.setTimeout(60000);
        client.addHeader(Constant.API_KEY, Constant.API_KEY_VALUE);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LogUtil.d("-------------AHttp(get url)--------------- \n" + url
                + "\n-----    " + params);
        responseHandler.setTag(CommonUtil.getMD5(url + params));
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LogUtil.d("-------------AHttp(post url)--------------- \n" + url
                + "\n-----    " + params);
        responseHandler.setTag(CommonUtil.getMD5(url + params));
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return Constant.HTTP_SERVER + relativeUrl;
    }
}
