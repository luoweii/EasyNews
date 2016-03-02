package com.luowei.easynews.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.luowei.easynews.R;
import com.luowei.easynews.entity.News;
import com.luowei.easynews.utils.CommonUtil;
import com.luowei.easynews.utils.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by 骆巍 on 2016/3/1.
 */
public class NewsAdapter extends BaseAdapter<News> {
    private LayoutInflater inflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_news, parent, false);
        }
        News news = data.get(position);
        ImageView ivImage = ViewHelper.get(convertView, R.id.ivImage);
        TextView tvTitle = ViewHelper.get(convertView, R.id.tvTitle);
        TextView tvSource = ViewHelper.get(convertView, R.id.tvSource);
        LinearLayout llImages = ViewHelper.get(convertView, R.id.llImages);
        if (news.imageurls.size() > 1) {
            ivImage.setVisibility(View.GONE);
            llImages.setVisibility(View.VISIBLE);
            if (!news.imageurls.equals(llImages.getTag())) {
                llImages.setTag(news.imageurls);
                llImages.removeAllViews();
                for (int i = 0; i < news.imageurls.size(); i++) {
                    ImageView iv = new ImageView(parent.getContext());
                    iv.setBackgroundColor(0xffe8e8e8);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, CommonUtil.dp2px(80), 1);
                    int margin = CommonUtil.dp2px(1);
                    lp.setMargins(margin, margin, margin, margin);
                    llImages.addView(iv, lp);
                    loadImage(news.imageurls.get(i).url, iv);
                    if (i == 4) break;
                }
            }
        } else if (news.imageurls.size() == 1) {
            ivImage.setVisibility(View.VISIBLE);
            llImages.setVisibility(View.GONE);
            loadImage(news.imageurls.get(0).url, ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
            llImages.setVisibility(View.GONE);
        }
        tvTitle.setText(news.title);
        tvSource.setText(news.source + "  " + news.getFormatDate());
        return convertView;
    }

    private void loadImage(String url, ImageView iv) {
        if (!url.equals(iv.getTag()))
            ImageLoader.getInstance().displayImage(url, iv);
        iv.setTag(url);
    }
}
