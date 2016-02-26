package com.luowei.easynews.adapter;

import java.util.List;

/**
 * Created by 骆巍 on 2016/2/14.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    protected List<T> data;

    public BaseAdapter(List<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
