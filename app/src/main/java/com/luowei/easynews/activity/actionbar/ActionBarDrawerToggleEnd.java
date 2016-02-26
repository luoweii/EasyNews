package com.luowei.easynews.activity.actionbar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by 骆巍 on 2016/2/26.
 */
@SuppressLint("NewApi")
public class ActionBarDrawerToggleEnd implements DrawerLayout.DrawerListener {

    public interface DelegateProvider {

        @Nullable
        Delegate getDrawerToggleDelegate();
    }

    public interface Delegate {

        void setActionBarUpIndicator(Drawable upDrawable, @StringRes int contentDescRes);

        void setActionBarDescription(@StringRes int contentDescRes);

        Drawable getThemeUpIndicator();

        Context getActionBarThemedContext();

        boolean isNavigationVisible();
    }

    private final Delegate mActivityImpl;
    private final DrawerLayout mDrawerLayout;

    private DrawerToggle mSlider;
    private Drawable mHomeAsUpIndicator;
    private boolean mDrawerIndicatorEnabled = true;
    private boolean mHasCustomUpIndicator;
    private final int mOpenDrawerContentDescRes;
    private final int mCloseDrawerContentDescRes;
    private View.OnClickListener mToolbarNavigationClickListener;
    private boolean mWarnedForDisplayHomeAsUp = false;

    public ActionBarDrawerToggleEnd(Activity activity, DrawerLayout drawerLayout,
                                 @StringRes int openDrawerContentDescRes,
                                 @StringRes int closeDrawerContentDescRes) {
        this(activity, null, drawerLayout, null, openDrawerContentDescRes,
                closeDrawerContentDescRes);
    }

    public ActionBarDrawerToggleEnd(Activity activity, DrawerLayout drawerLayout,
                                 Toolbar toolbar, @StringRes int openDrawerContentDescRes,
                                 @StringRes int closeDrawerContentDescRes) {
        this(activity, toolbar, drawerLayout, null, openDrawerContentDescRes,
                closeDrawerContentDescRes);
    }

    <T extends Drawable & DrawerToggle> ActionBarDrawerToggleEnd(Activity activity, Toolbar toolbar,
                                                              DrawerLayout drawerLayout, T slider,
                                                              @StringRes int openDrawerContentDescRes,
                                                              @StringRes int closeDrawerContentDescRes) {
        if (toolbar != null) {
            mActivityImpl = new ToolbarCompatDelegate(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDrawerIndicatorEnabled) {
                        toggle();
                    } else if (mToolbarNavigationClickListener != null) {
                        mToolbarNavigationClickListener.onClick(v);
                    }
                }
            });
        } else if (activity instanceof DelegateProvider) { // Allow the Activity to provide an impl
            mActivityImpl = ((DelegateProvider) activity).getDrawerToggleDelegate();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mActivityImpl = new JellybeanMr2Delegate(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mActivityImpl = new HoneycombDelegate(activity);
        } else {
            mActivityImpl = new DummyDelegate(activity);
        }

        mDrawerLayout = drawerLayout;
        mOpenDrawerContentDescRes = openDrawerContentDescRes;
        mCloseDrawerContentDescRes = closeDrawerContentDescRes;
        if (slider == null) {
            mSlider = new DrawerArrowDrawableToggle(activity,
                    mActivityImpl.getActionBarThemedContext());
        } else {
            mSlider = slider;
        }

        mHomeAsUpIndicator = getThemeUpIndicator();
    }

    public void syncState() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mSlider.setPosition(1);
        } else {
            mSlider.setPosition(0);
        }
        if (mDrawerIndicatorEnabled) {
            setActionBarUpIndicator((Drawable) mSlider,
                    mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
                            mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // Reload drawables that can change with configuration
        if (!mHasCustomUpIndicator) {
            mHomeAsUpIndicator = getThemeUpIndicator();
        }
        syncState();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home && mDrawerIndicatorEnabled) {
            toggle();
            return true;
        }
        return false;
    }

    private void toggle() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
    }

    public void setHomeAsUpIndicator(Drawable indicator) {
        if (indicator == null) {
            mHomeAsUpIndicator = getThemeUpIndicator();
            mHasCustomUpIndicator = false;
        } else {
            mHomeAsUpIndicator = indicator;
            mHasCustomUpIndicator = true;
        }

        if (!mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(mHomeAsUpIndicator, 0);
        }
    }

    public void setHomeAsUpIndicator(int resId) {
        Drawable indicator = null;
        if (resId != 0) {
            indicator = mDrawerLayout.getResources().getDrawable(resId);
        }
        setHomeAsUpIndicator(indicator);
    }

    public boolean isDrawerIndicatorEnabled() {
        return mDrawerIndicatorEnabled;
    }

    public void setDrawerIndicatorEnabled(boolean enable) {
        if (enable != mDrawerIndicatorEnabled) {
            if (enable) {
                setActionBarUpIndicator((Drawable) mSlider,
                        mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
                                mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
            } else {
                setActionBarUpIndicator(mHomeAsUpIndicator, 0);
            }
            mDrawerIndicatorEnabled = enable;
        }
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        mSlider.setPosition(Math.min(1f, Math.max(0, slideOffset)));
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        mSlider.setPosition(1);
        if (mDrawerIndicatorEnabled) {
            setActionBarDescription(mCloseDrawerContentDescRes);
        }
    }

    public void onDrawerClosed(View drawerView) {
        mSlider.setPosition(0);
        if (mDrawerIndicatorEnabled) {
            setActionBarDescription(mOpenDrawerContentDescRes);
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    public View.OnClickListener getToolbarNavigationClickListener() {
        return mToolbarNavigationClickListener;
    }

    public void setToolbarNavigationClickListener(
            View.OnClickListener onToolbarNavigationClickListener) {
        mToolbarNavigationClickListener = onToolbarNavigationClickListener;
    }

    void setActionBarUpIndicator(Drawable upDrawable, int contentDescRes) {
        if (!mWarnedForDisplayHomeAsUp && !mActivityImpl.isNavigationVisible()) {
            Log.w("ActionBarDrawerToggle", "DrawerToggle may not show up because NavigationIcon"
                    + " is not visible. You may need to call "
                    + "actionbar.setDisplayHomeAsUpEnabled(true);");
            mWarnedForDisplayHomeAsUp = true;
        }
        mActivityImpl.setActionBarUpIndicator(upDrawable, contentDescRes);
    }

    void setActionBarDescription(int contentDescRes) {
        mActivityImpl.setActionBarDescription(contentDescRes);
    }

    Drawable getThemeUpIndicator() {
        return mActivityImpl.getThemeUpIndicator();
    }

    static class DrawerArrowDrawableToggle extends DrawerArrowDrawable implements DrawerToggle {
        private final Activity mActivity;

        public DrawerArrowDrawableToggle(Activity activity, Context themedContext) {
            super(themedContext);
            mActivity = activity;
        }

        public void setPosition(float position) {
            if (position == 1f) {
                setVerticalMirror(true);
            } else if (position == 0f) {
                setVerticalMirror(false);
            }
            setProgress(position);
        }

        public float getPosition() {
            return getProgress();
        }
    }

    static interface DrawerToggle {

        public void setPosition(float position);

        public float getPosition();
    }

    private static class HoneycombDelegate implements Delegate {

        final Activity mActivity;
        ActionBarDrawerToggleHoneycomb.SetIndicatorInfo mSetIndicatorInfo;

        private HoneycombDelegate(Activity activity) {
            mActivity = activity;
        }

        @Override
        public Drawable getThemeUpIndicator() {
            return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(mActivity);
        }

        @Override
        public Context getActionBarThemedContext() {
            final ActionBar actionBar = mActivity.getActionBar();
            final Context context;
            if (actionBar != null) {
                context = actionBar.getThemedContext();
            } else {
                context = mActivity;
            }
            return context;
        }

        @Override
        public boolean isNavigationVisible() {
            final ActionBar actionBar = mActivity.getActionBar();
            return actionBar != null
                    && (actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0;
        }

        @Override
        public void setActionBarUpIndicator(Drawable themeImage, int contentDescRes) {
            mActivity.getActionBar().setDisplayShowHomeEnabled(true);
            mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(
                    mSetIndicatorInfo, mActivity, themeImage, contentDescRes);
            mActivity.getActionBar().setDisplayShowHomeEnabled(false);
        }

        @Override
        public void setActionBarDescription(int contentDescRes) {
            mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarDescription(
                    mSetIndicatorInfo, mActivity, contentDescRes);
        }
    }

    private static class JellybeanMr2Delegate implements Delegate {

        final Activity mActivity;

        private JellybeanMr2Delegate(Activity activity) {
            mActivity = activity;
        }

        @Override
        public Drawable getThemeUpIndicator() {
            final TypedArray a = getActionBarThemedContext().obtainStyledAttributes(null,
                    new int[]{android.R.attr.homeAsUpIndicator}, android.R.attr.actionBarStyle, 0);
            final Drawable result = a.getDrawable(0);
            a.recycle();
            return result;
        }

        @Override
        public Context getActionBarThemedContext() {
            final ActionBar actionBar = mActivity.getActionBar();
            final Context context;
            if (actionBar != null) {
                context = actionBar.getThemedContext();
            } else {
                context = mActivity;
            }
            return context;
        }

        @Override
        public boolean isNavigationVisible() {
            final ActionBar actionBar = mActivity.getActionBar();
            return actionBar != null &&
                    (actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0;
        }

        @Override
        public void setActionBarUpIndicator(Drawable drawable, int contentDescRes) {
            final ActionBar actionBar = mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(drawable);
                actionBar.setHomeActionContentDescription(contentDescRes);
            }
        }

        @Override
        public void setActionBarDescription(int contentDescRes) {
            final ActionBar actionBar = mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeActionContentDescription(contentDescRes);
            }
        }
    }

    static class ToolbarCompatDelegate implements Delegate {

        final Toolbar mToolbar;
        final Drawable mDefaultUpIndicator;
        final CharSequence mDefaultContentDescription;

        ToolbarCompatDelegate(Toolbar toolbar) {
            mToolbar = toolbar;
            mDefaultUpIndicator = toolbar.getNavigationIcon();
            mDefaultContentDescription = toolbar.getNavigationContentDescription();
        }

        @Override
        public void setActionBarUpIndicator(Drawable upDrawable, @StringRes int contentDescRes) {
            mToolbar.setNavigationIcon(upDrawable);
            setActionBarDescription(contentDescRes);
        }

        @Override
        public void setActionBarDescription(@StringRes int contentDescRes) {
            if (contentDescRes == 0) {
                mToolbar.setNavigationContentDescription(mDefaultContentDescription);
            } else {
                mToolbar.setNavigationContentDescription(contentDescRes);
            }
        }

        @Override
        public Drawable getThemeUpIndicator() {
            return mDefaultUpIndicator;
        }

        @Override
        public Context getActionBarThemedContext() {
            return mToolbar.getContext();
        }

        @Override
        public boolean isNavigationVisible() {
            return true;
        }
    }

    static class DummyDelegate implements Delegate {
        final Activity mActivity;

        DummyDelegate(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void setActionBarUpIndicator(Drawable upDrawable, @StringRes int contentDescRes) {

        }

        @Override
        public void setActionBarDescription(@StringRes int contentDescRes) {

        }

        @Override
        public Drawable getThemeUpIndicator() {
            return null;
        }

        @Override
        public Context getActionBarThemedContext() {
            return mActivity;
        }

        @Override
        public boolean isNavigationVisible() {
            return true;
        }
    }
}

