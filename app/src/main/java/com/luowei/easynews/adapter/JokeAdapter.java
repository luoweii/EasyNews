package com.luowei.easynews.adapter;

import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luowei.easynews.R;
import com.luowei.easynews.entity.History;
import com.luowei.easynews.entity.Joke;
import com.luowei.easynews.utils.CommonUtil;
import com.luowei.easynews.utils.PreferenceUtil;
import com.luowei.easynews.utils.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by 骆巍 on 2016/3/4.
 */
public class JokeAdapter extends BaseAdapter<Joke> {
    private LayoutInflater inflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_historynow, parent, false);
        }
        Joke j = data.get(position);
        ImageView ivImage = ViewHelper.get(convertView, R.id.ivImage);
        if (TextUtils.isEmpty(j.img)) {
            ivImage.setVisibility(View.GONE);
        } else {
            ivImage.setVisibility(View.VISIBLE);
            if (!j.img.equals(ivImage.getTag())) {
                ImageLoader.getInstance().displayImage(j.img, ivImage, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (loadedImage == null) return;
                        int w = view.getWidth() > 0 ? view.getWidth() : CommonUtil.dp2px(300);
                        view.getLayoutParams().height = loadedImage.getHeight() * w / loadedImage.getWidth();

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
                ivImage.setTag(j.img);
            }
        }

        TextView tvTitle = ViewHelper.get(convertView, R.id.tvTitle);
        if (j.isRead) tvTitle.setTextColor(0xff888888);
        else tvTitle.setTextColor(0xff333333);
        tvTitle.setText(j.title);
        TextView tvTime = ViewHelper.get(convertView, R.id.tvTime);
        tvTime.setText(j.getFormatDate());
        TextView tvDesc = ViewHelper.get(convertView, R.id.tvDescription);
        if (TextUtils.isEmpty(j.text)) {
            tvDesc.setVisibility(View.GONE);
        } else {
            tvDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(Html.fromHtml(j.text));
        }
        return convertView;
    }

}
