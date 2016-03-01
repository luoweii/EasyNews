package com.luowei.easynews.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 骆巍 on 2016/2/14.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    protected List<T> data = new ArrayList<>();

    public BaseAdapter() {

    }

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

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addData(int location,List<T> data) {
        this.data.addAll(location,data);
        notifyDataSetChanged();
    }

    public void addData(T t) {
        this.data.add(t);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void remove(T t) {
        data.remove(t);
        notifyDataSetChanged();
    }
}
