package com.luowei.easynews.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luowei.easynews.R;
import com.luowei.easynews.activity.MainActivity;
import com.luowei.easynews.entity.History;
import com.luowei.easynews.entity.Weixin;
import com.luowei.easynews.utils.CommonUtil;
import com.luowei.easynews.utils.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by 骆巍 on 2016/3/4.
 */
public class HistorynowAdapter extends BaseAdapter<History> {
    private LayoutInflater inflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_historynow, parent, false);
        }
        History h = data.get(position);
        ImageView ivImage = ViewHelper.get(convertView, R.id.ivImage);
        if (!h.pic.equals(ivImage.getTag())) {
            ImageLoader.getInstance().displayImage(h.pic, ivImage);
            ivImage.setTag(h.pic);
        }
        TextView tvTitle = ViewHelper.get(convertView, R.id.tvTitle);
        if (h.isRead) tvTitle.setTextColor(0xff888888);
        else tvTitle.setTextColor(0xff333333);
        tvTitle.setText(h.title);
        TextView tvTime = ViewHelper.get(convertView, R.id.tvTime);
        tvTime.setText(h.year + "年" + h.month + "月" + h.day+"日"+"  "+h.lunar);
        TextView tvDesc = ViewHelper.get(convertView, R.id.tvDescription);
        tvDesc.setText(h.des);

        return convertView;
    }

}
