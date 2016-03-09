package com.luowei.easynews.entity;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 骆巍 on 2016/3/1.
 */
public class Joke extends BaseEntity {
    public String ct;
    public String text;
    public String title;
    public String type;
    public String img;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public boolean isRead;

    public String getFormatDate() {
        if (TextUtils.isEmpty(ct)) return "";
        try {
            Date date = sdf.parse(ct);
            long time = System.currentTimeMillis() - date.getTime();
            long minute = time / 1000 / 60;//分钟
            if (minute < 60) {
                return minute + "分钟前";
            } else if (minute < 1440) {
                return minute / 60 + "小时前";
            } else if (minute < 2880){
                return "昨天";
            } else {
                return ct.substring(0, 10);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public class JokeResponse extends BaseEntity {
        public int showapi_res_code;
        public String showapi_res_error;
        public JokeBody showapi_res_body;
    }

    public class JokeBody extends BaseEntity {
        public int allNum;
        public int allPages;
        public List<Joke> contentlist;
        public int currentPage;
        public int maxResult;
    }

}
