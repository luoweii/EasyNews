package com.luowei.easynews.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.luowei.easynews.R;
import com.luowei.easynews.activity.MainActivity;
import com.luowei.easynews.adapter.JokeAdapter;
import com.luowei.easynews.entity.Joke;
import com.luowei.easynews.net.AHttp;
import com.luowei.easynews.net.JsonHttpHandler;
import com.luowei.easynews.utils.CommonUtil;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by 骆巍 on 2016/3/9.
 */
public class JokeBaiduFragment extends BaseFragment {
    private MainActivity activity;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.ptrFrameLayout)
    PtrClassicFrameLayout ptrFrameLayout;
    private JokeAdapter adapter;
    private int currentPage = 0;
    private View vLoadmore;
    private boolean isLoading;
    private String title = "笑话";

    public static JokeBaiduFragment newInstance(String url) {
        JokeBaiduFragment fragment = new JokeBaiduFragment();
        Bundle b = new Bundle();
        b.putString("url", url);
        fragment.setArguments(b);
        if (url.contains("joke_pic")) fragment.setTitle("趣图");
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

        adapter = new JokeAdapter();
        listView.setAdapter(adapter);

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
        AHttp.get(getArguments().getString("url"), rp, new JsonHttpHandler<Joke.JokeResponse>() {
            @Override
            public void onSuccess(Joke.JokeResponse data) {
                if (isStart) adapter.setData(data.showapi_res_body.contentlist);
                else adapter.addData(data.showapi_res_body.contentlist);
                ptrFrameLayout.refreshComplete();
                isLoading = false;
                if (data.showapi_res_body.currentPage >= data.showapi_res_body.allPages) {
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_joke;
    }
}
