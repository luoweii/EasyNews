package com.luowei.easynews.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.luowei.easynews.utils.CommonUtil;
import com.luowei.easynews.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Method;

import butterknife.ButterKnife;

/**
 * Created by 骆巍 on 2016/2/26.
 */
public class BaseActivity extends AppCompatActivity {
    public EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i("--创建界面-->> " + getClass().getSimpleName());
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("--进入界面-->> " + getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i("--离开界面-->> " + getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventBus.isRegistered(this))
            eventBus.unregister(this);
        LogUtil.i("--销毁界面-->> " + getClass().getSimpleName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Subscribe
    public void onEvent(String msg){
        CommonUtil.showToast(msg);
    }
}
