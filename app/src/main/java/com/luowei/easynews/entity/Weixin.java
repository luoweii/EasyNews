package com.luowei.easynews.entity;

import java.util.List;

/**
 * Created by 骆巍 on 2016/3/4.
 */
public class Weixin extends BaseEntity {
    public String title;
    public String description;
    public String picUrl;
    public String url;
    public String hottime;

    public boolean isRead;

    public class WeixinResponse extends BaseEntity {
        public String code;
        public String msg;
        public List<Weixin> newslist;
    }
}
