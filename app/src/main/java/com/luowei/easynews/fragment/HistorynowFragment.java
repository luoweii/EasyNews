package com.luowei.easynews.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.luowei.easynews.Constant;
import com.luowei.easynews.R;
import com.luowei.easynews.activity.MainActivity;
import com.luowei.easynews.adapter.HistorynowAdapter;
import com.luowei.easynews.entity.History;
import com.luowei.easynews.net.AHttp;
import com.luowei.easynews.net.JsonHttpHandler;
import com.luowei.easynews.utils.CommonUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by 骆巍 on 2016/3/8.
 */
public class HistorynowFragment extends BaseFragment {
    @Bind(R.id.listView)
    ListView listView;
    private HistorynowAdapter adapter;
    @Bind(R.id.ptrFrameLayout)
    PtrClassicFrameLayout ptrFrameLayout;
    private MainActivity activity;

    private static HistorynowFragment fragment;

    public static HistorynowFragment getInstance() {
        if (fragment == null) fragment = new HistorynowFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new HistorynowAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id < 0) return;
            }
        });

        ptrFrameLayout.setDurationToCloseHeader(500);
        ptrFrameLayout.setLastUpdateTimeRelateObject(this);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getData(true);
            }
        });

        ptrFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                ptrFrameLayout.autoRefresh();
            }
        });
    }


    private void getData(final boolean isStart) {
        RequestParams rp = new RequestParams();
        rp.add("key", Constant.API_KEY_VALUE_JUHE);
        rp.add("v", "1.0");
        rp.add("month", (Calendar.getInstance().get(Calendar.MONTH)+1)+"");
        rp.add("day", Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"");
        if (!TextUtils.isEmpty(MainActivity.query)) rp.add("word", MainActivity.query);
        AHttp.get(Constant.HTTP_HISTORY_NOW, rp, new JsonHttpHandler<History.HistoryResponse>() {
            @Override
            public void onSuccess(History.HistoryResponse data) {
                if (isStart) adapter.setData(data.result);
                else adapter.addData(data.result);
                ptrFrameLayout.refreshComplete();
            }

            @Override
            public void onFailure(int errCode, String msg) {
                CommonUtil.showToast(errCode + ": " + msg);
                ptrFrameLayout.refreshComplete();
            }
        });
    }

    @Override
    public String getTitle() {
        return "历史上的今天";
    }

    @Override
    public int getLayout() {
        return R.layout.historynow_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(HistorynowFragment fragment) {
        ptrFrameLayout.autoRefresh();
    }
}
