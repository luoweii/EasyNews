package com.luowei.easynews.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.luowei.easynews.Constant;
import com.luowei.easynews.R;
import com.luowei.easynews.activity.MainActivity;
import com.luowei.easynews.activity.WebActivity;
import com.luowei.easynews.adapter.NewsAdapter;
import com.luowei.easynews.entity.News;
import com.luowei.easynews.net.AHttp;
import com.luowei.easynews.net.JsonHttpHandler;
import com.luowei.easynews.utils.CommonUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by 骆巍 on 2016/3/4.
 */
public class NewsFragment extends BaseFragment {
    @Bind(R.id.listView)
    ListView listView;
    private NewsAdapter newsAdapter;
    @Bind(R.id.ptrFrameLayout)
    PtrClassicFrameLayout ptrFrameLayout;
    private int currentPage = 0;
    private View vLoadmore;
    private boolean isLoading;
    private MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        newsAdapter = new NewsAdapter();
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id < 0) return;
                WebActivity.startActivity(getContext(), newsAdapter.getItem((int) id).link);
                newsAdapter.getItem((int) id).isRead = true;
                newsAdapter.notifyDataSetChanged();
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
        vLoadmore = getLayoutInflater(savedInstanceState).inflate(R.layout.list_footer_load_more, null);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 如果滑动到最后一项
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && view.getLastVisiblePosition() == view.getCount() - 1 && listView.getFooterViewsCount() > 0) {
                    if (!isLoading) {
                        getData(false);
                    }
                }
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && !activity.getSearchView().isIconified()) {
                    CommonUtil.hideInput(getActivity());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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
        isLoading = true;
        if (isStart) currentPage = 0;
        RequestParams rp = new RequestParams();
        rp.add("page", (++currentPage) + "");
        if (!TextUtils.isEmpty(MainActivity.query)) rp.add("title", MainActivity.query);
        AHttp.get(Constant.HTTP_CHANNEL_NEWS, rp, new JsonHttpHandler<News.NewsResponse>() {
            @Override
            public void onSuccess(News.NewsResponse data) {
                if (isStart) newsAdapter.setData(data.showapi_res_body.pagebean.contentlist);
                else newsAdapter.addData(data.showapi_res_body.pagebean.contentlist);
                ptrFrameLayout.refreshComplete();
                isLoading = false;
                if (data.showapi_res_body.pagebean.currentPage >= data.showapi_res_body.pagebean.allPages) {
                    listView.removeFooterView(vLoadmore);
                } else if (listView.getFooterViewsCount() == 0) {
                    listView.addFooterView(vLoadmore);
                }
            }

            @Override
            public void onFailure(int errCode, String msg) {
                CommonUtil.showToast(errCode + ": " + msg);
                ptrFrameLayout.refreshComplete();
                isLoading = false;
            }
        });
    }

    @Override
    public String getTitle() {
        return "头条";
    }

    @Override
    public int getLayout() {
        return R.layout.news_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(NewsFragment fragment) {
        ptrFrameLayout.autoRefresh();
    }
}
