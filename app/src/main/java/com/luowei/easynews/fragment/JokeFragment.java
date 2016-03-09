package com.luowei.easynews.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.luowei.easynews.Constant;
import com.luowei.easynews.R;
import com.luowei.easynews.activity.MainActivity;
import com.luowei.easynews.adapter.MainFragmentPagerAdapter;

import butterknife.Bind;

/**
 * Created by 骆巍 on 2016/3/8.
 */
public class JokeFragment extends BaseFragment {
    private MainActivity activity;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    private MainFragmentPagerAdapter adapter;

    private static JokeFragment fragment;

    public static JokeFragment getInstance() {
        if (fragment == null) fragment = new JokeFragment();
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

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        adapter = new MainFragmentPagerAdapter(getChildFragmentManager());
        adapter.addFragment(JokeJuheFragment.newInstance(Constant.HTTP_JOKE_IMG_JUHE));
        adapter.addFragment(JokeJuheFragment.newInstance(Constant.HTTP_JOKE_TEXT_JUHE));
        adapter.addFragment(JokeBaiduFragment.newInstance(Constant.HTTP_JOKE_IMG));
        adapter.addFragment(JokeBaiduFragment.newInstance(Constant.HTTP_JOKE_TEXT));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public String getTitle() {
        return "笑话";
    }

    @Override
    public int getLayout() {
        return R.layout.joke_main;
    }
}
