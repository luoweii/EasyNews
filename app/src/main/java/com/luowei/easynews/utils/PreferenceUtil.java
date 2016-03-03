package com.luowei.easynews.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.luowei.easynews.App;

import java.lang.reflect.Type;

/**
 * Created by 骆巍 on 2015/9/16.
 */
public class PreferenceUtil {
    public static void putString(String key, String value) {
        SharedPreferences spf = App.context.getSharedPreferences(App.context.getPackageName(), 0);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key, String defValue) {
        SharedPreferences spf = App.context.getSharedPreferences(App.context.getPackageName(), 0);
        return spf.getString(key, defValue);
    }

    public static void putObject(String key, Object obj) {
        App.context.getSharedPreferences(App.context.getPackageName(), 0).edit()
                .putString(key, JsonUtil.objectToJson(obj)).commit();
    }

    public static <T> T getObject( String key, Type type) {
        String str = App.context.getSharedPreferences(App.context.getPackageName(), 0).getString(key, "");
        return JsonUtil.fromJson(str, type);
    }
}
