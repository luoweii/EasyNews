package com.luowei.easynews.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.luowei.easynews.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 骆巍
 * @date 2016-3-9
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<BaseFragment> fragments = new ArrayList<>();

	public MainFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public void addFragment(BaseFragment fragment) {
		fragments.add(fragment);
	}

	@Override
	public BaseFragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return fragments.get(position).getTitle();
	}
}
