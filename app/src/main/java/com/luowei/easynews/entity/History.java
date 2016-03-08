package com.luowei.easynews.entity;

import java.util.List;

/**
 * Created by 骆巍 on 2016/3/8.
 */
public class History extends BaseEntity {
    public String day;
    public String des;
    public String id;
    public String lunar;
    public String month;
    public String pic;
    public String title;
    public String year;

    public boolean isRead;

    public class HistoryResponse extends BaseEntity {
        public String error_code;
        public String reason;
        public List<History> result;
    }
}
