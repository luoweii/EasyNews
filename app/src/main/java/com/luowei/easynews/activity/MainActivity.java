package com.luowei.easynews.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.luowei.easynews.Constant;
import com.luowei.easynews.R;
import com.luowei.easynews.adapter.NewsAdapter;
import com.luowei.easynews.entity.News;
import com.luowei.easynews.net.AHttp;
import com.luowei.easynews.net.JsonHttpHandler;
import com.luowei.easynews.utils.Blur;
import com.luowei.easynews.utils.CommonUtil;
import com.luowei.easynews.utils.PreferenceUtil;
import com.luowei.easynews.utils.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 主导航界面
 * Created by 骆巍 on 2016/2/26.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private UMShareAPI umShareAPI;
    ImageView ivHead;
    LinearLayout llLogin;
    TextView tvName;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.listView)
    ListView listView;
    private NewsAdapter newsAdapter;
    @Bind(R.id.ptrFrameLayout)
    PtrClassicFrameLayout ptrFrameLayout;
    private int currentPage = 0;
    private View vLoadmore;
    private boolean isLoading;
    private SearchView searchView;
    private String query;//查询字段

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        /*
        点击Navigation图标菜单从右边滑出有两种方法:
        1. 拷贝ActionBarDrawerToggle和ActionBarDrawerToggleHoneycomb然后修改toggle方法
        2. 设置 toolbar.setNavigationOnClickListener()写自己的逻辑
         */
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ivHead = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivHead);
        llLogin = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.llLogin);
        tvName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvName);

        newsAdapter = new NewsAdapter();
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id < 0) return;
                WebActivity.startActivity(MainActivity.this, newsAdapter.getItem((int) id).link);
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
        vLoadmore = getLayoutInflater().inflate(R.layout.list_footer_load_more, null);

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
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && !searchView.isIconified()) {
                    CommonUtil.hideInput(MainActivity.this);
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
        if (!TextUtils.isEmpty(query)) rp.add("title", query);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else if (!searchView.isIconified()) {
            searchView.setIconified(true);//最小化
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setQuery(query);
                ptrFrameLayout.autoRefresh();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setQuery(null);
                ptrFrameLayout.autoRefresh();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    public void loginOnClick(View view) {
        umShareAPI = UMShareAPI.get(this);
        SHARE_MEDIA platform = null;
        switch (view.getId()) {
            case R.id.ivSina:
                platform = SHARE_MEDIA.SINA;
                break;
            case R.id.ivWeixin:
                platform = SHARE_MEDIA.WEIXIN;
                break;
            case R.id.ivQQ:
                platform = SHARE_MEDIA.QQ;
                break;
        }
        final SHARE_MEDIA finalPlatform = platform;
        umShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                umShareAPI.getPlatformInfo(MainActivity.this, finalPlatform, new UMAuthListener() {
                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        String headUrl = map.get("profile_image_url");
                        String name = map.get("screen_name");
                        ImageLoader.getInstance().displayImage(headUrl, ivHead, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                Bitmap bm = Blur.fastblur(loadedImage, CommonUtil.dp2px(10), 0.1f);
                                View vBackground = navigationView.getHeaderView(0).findViewById(R.id.vBackground);
                                vBackground.setBackground(new BitmapDrawable(bm));
                                ViewHelper.toggleViewAnim(vBackground);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
                        tvName.setText(name);
                        ViewHelper.toggleViewAnim(ivHead);
                        ViewHelper.toggleViewAnim(llLogin);
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {

                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        umShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    public void setQuery(String q) {
        query = q;
        newsAdapter.setQuery(q);
    }
}
