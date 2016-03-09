package com.luowei.easynews.entity;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 骆巍 on 2016/3/9.
 */
public class JokeNew extends BaseEntity {
    public String content;
    public String hashId;
    public String unixtime;
    public String updatetime;
    public String url;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public boolean isRead;

    public class JokeNewResponse extends BaseEntity {
        public String error_code;
        public String reason;
        public JokeNewResult result;
    }

    public class JokeNewResult extends BaseEntity {
        public List<JokeNew> data;
    }


    public String getFormatDate() {
        if (TextUtils.isEmpty(updatetime)) return "";
        try {
            Date date = sdf.parse(updatetime);
            long time = System.currentTimeMillis() - date.getTime();
            long minute = time / 1000 / 60;//分钟
            if (minute < 60) {
                return minute + "分钟前";
            } else if (minute < 1440) {
                return minute / 60 + "小时前";
            } else if (minute < 2880){
                return "昨天";
            } else {
                return updatetime.substring(0, 10);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
