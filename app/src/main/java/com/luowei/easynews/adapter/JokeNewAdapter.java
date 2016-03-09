package com.luowei.easynews.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.luowei.easynews.R;
import com.luowei.easynews.entity.Joke;
import com.luowei.easynews.entity.JokeNew;
import com.luowei.easynews.net.AHttp;
import com.luowei.easynews.utils.CommonUtil;
import com.luowei.easynews.utils.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;

import cz.msebera.android.httpclient.Header;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.GifTextureView;
import pl.droidsonroids.gif.InputSource;

/**
 * Created by 骆巍 on 2016/3/4.
 */
public class JokeNewAdapter extends BaseAdapter<JokeNew> {
    private LayoutInflater inflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_jokenew, parent, false);
        }
        final JokeNew j = data.get(position);
        final GifTextureView gifImage = ViewHelper.get(convertView, R.id.gifImage);
        final ImageView ivImage = ViewHelper.get(convertView, R.id.ivImage);
        final FrameLayout flImage = ViewHelper.get(convertView, R.id.flImage);
        if (TextUtils.isEmpty(j.url)) {
            flImage.setVisibility(View.GONE);
        } else {
            flImage.setVisibility(View.VISIBLE);
            if (!j.url.equals(flImage.getTag())) {
                if (j.url.endsWith(".gif")) {
                    gifImage.setVisibility(View.VISIBLE);
                    ivImage.setVisibility(View.GONE);
                    byte[] bytes = AHttp.httpCache.getByte(CommonUtil.getMD5(j.url));
                    if (bytes == null) {
                        ivImage.setVisibility(View.VISIBLE);
                        ivImage.setImageDrawable(new ColorDrawable(0xffe9e9e9));
                        AHttp.get(j.url, null, new BinaryHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                                if (binaryData == null) return;
                                AHttp.httpCache.put(CommonUtil.getMD5(j.url), binaryData);
                                if (j.url.equals(flImage.getTag())) {
                                    ivImage.setVisibility(View.GONE);
                                    gifImage.setInputSource(new InputSource.ByteArraySource(binaryData));
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {

                            }
                        });
                    } else {
                        gifImage.setInputSource(new InputSource.ByteArraySource(bytes));
                    }
                } else {
                    gifImage.setVisibility(View.GONE);
                    ivImage.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(j.url, ivImage);
                }
                flImage.setTag(j.url);
            }
        }

        TextView tvTitle = ViewHelper.get(convertView, R.id.tvTitle);
        if (j.isRead) tvTitle.setTextColor(0xff888888);
        else tvTitle.setTextColor(0xff333333);
        tvTitle.setText(j.content);
        TextView tvTime = ViewHelper.get(convertView, R.id.tvTime);
        tvTime.setText(j.getFormatDate());
        return convertView;
    }

}
