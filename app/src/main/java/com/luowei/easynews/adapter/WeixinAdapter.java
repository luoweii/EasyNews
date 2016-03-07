package com.luowei.easynews.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luowei.easynews.R;
import com.luowei.easynews.activity.MainActivity;
import com.luowei.easynews.entity.Weixin;
import com.luowei.easynews.utils.CommonUtil;
import com.luowei.easynews.utils.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by 骆巍 on 2016/3/4.
 */
public class WeixinAdapter extends BaseAdapter<Weixin> {
    private LayoutInflater inflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_weixin, parent, false);
        }
        Weixin wx = data.get(position);
        ImageView ivImage = ViewHelper.get(convertView, R.id.ivImage);
        if (!wx.picUrl.equals(ivImage.getTag())) {
            ImageLoader.getInstance().displayImage(wx.picUrl, ivImage);
            ivImage.setTag(wx.picUrl);
        }
        TextView tvTitle = ViewHelper.get(convertView, R.id.tvTitle);
        if (wx.isRead) tvTitle.setTextColor(0xff888888);
        else tvTitle.setTextColor(0xff333333);
        if (TextUtils.isEmpty(MainActivity.query)) tvTitle.setText(wx.title);
        else CommonUtil.highlightString(tvTitle, wx.title, MainActivity.query);

        TextView tvDesc = ViewHelper.get(convertView, R.id.tvDescription);
        tvDesc.setText(wx.description);

        return convertView;
    }

}
