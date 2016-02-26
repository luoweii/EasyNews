package com.luowei.easynews.net;

import com.loopj.android.http.TextHttpResponseHandler;
import com.luowei.easynews.utils.JsonUtil;
import com.luowei.easynews.utils.LogUtil;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by luowei on 2015/10/25.
 */
public abstract class JsonHttpHandler<T> extends TextHttpResponseHandler {
    private static int CACHE_CODE = 304;//缓存代码
    private boolean enableCache;

    public JsonHttpHandler() {
    }

    public JsonHttpHandler(boolean enableCache) {
        this.enableCache = enableCache;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        LogUtil.d("----------MHttp response " + (statusCode == CACHE_CODE ? "cache" : "")
                + "(" + getRequestURI() + ")-------------\n" + responseString);
        try {
            if (enableCache && getTag() != null) {
                String tag = getTag().toString();
                AHttp.httpCache.put(tag, responseString);
            }
            T data = processResult(responseString);
            onSuccess(data);
        } catch (Exception e) {
            LogUtil.w(e);
            onFailure(-1, "数据格式错误");
        }
    }

    private T processResult(String dataStr) {
        Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        //检测泛型类型是否为String
        if (type instanceof Class && String.class.getName().equals(((Class) type).getName())) {
            return (T) dataStr;
        }
        return JsonUtil.fromJson(dataStr, type);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        LogUtil.w(statusCode + " " + responseString, throwable);
        if (enableCache && getTag() != null) {
            String result = AHttp.httpCache.get(getTag().toString());
            if (result != null) onSuccess(CACHE_CODE, null, result);
            else onFailure(statusCode, responseString);
        } else {
            onFailure(statusCode, responseString);
        }
    }

    public abstract void onSuccess(T data);

    public abstract void onFailure(int errCode, String msg);

}
