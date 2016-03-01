package com.luowei.easynews.entity;

import android.provider.Settings;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 骆巍 on 2016/3/1.
 */
public class News extends BaseEntity {
    public String channelId;
    public String channelName;
    public String desc;
    public List<NewsImage> imageurls;
    public String link;
    public String nid;
    public String pubDate;
    public String source;
    public String title;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String getFormatDate() {
        if (TextUtils.isEmpty(pubDate)) return "";
        try {
            Date date = sdf.parse(pubDate);
            long time = System.currentTimeMillis() - date.getTime();
            long minute = time / 1000 / 60;//分钟
            if (minute < 60) {
                return minute + "分钟前";
            } else if (minute < 1440) {
                return minute / 60 + "小时前";
            } else if (minute < 2880){
                return "昨天";
            } else {
                return pubDate.substring(0, 10);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public class NewsImage extends BaseEntity {
        public String url;
        public int height;
        public int width;
    }

    public class NewsResponse extends BaseEntity {
        public int showapi_res_code;
        public String showapi_res_error;
        public NewsBody showapi_res_body;
    }

    public class NewsBody extends BaseEntity {
        public NewsPage pagebean;
        public int ret_code;
    }

    public class NewsPage extends BaseEntity {
        public int allNum;
        public int allPages;
        public List<News> contentlist;
        public int currentPage;
        public int maxResult;
    }
}
