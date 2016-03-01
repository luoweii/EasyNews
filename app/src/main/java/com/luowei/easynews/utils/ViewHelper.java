package com.luowei.easynews.utils;

import android.app.Activity;
import android.app.Dialog;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;

/**
 * Created by luowei on 2015/7/25.
 */
public class ViewHelper {
    /**
     * Simpler version of {@link View#findViewById(int)} which infers the target type.
     */
    @SuppressWarnings({"unchecked", "UnusedDeclaration"}) // Checked by runtime cast. Public API.
    public static <T extends View> T findById(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * Simpler version of {@link Activity#findViewById(int)} which infers the target type.
     */
    @SuppressWarnings({"unchecked", "UnusedDeclaration"}) // Checked by runtime cast. Public API.
    public static <T extends View> T findById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    /**
     * Simpler version of {@link Dialog#findViewById(int)} which infers the target type.
     */
    @SuppressWarnings({"unchecked", "UnusedDeclaration"}) // Checked by runtime cast. Public API.
    public static <T extends View> T findById(Dialog dialog, int id) {
        return (T) dialog.findViewById(id);
    }

    /**
     * @param view
     * @param id   资源ID
     * @return View对象
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

    /**
     * 显示或隐藏动画
     *
     * @param v
     */
    public static void toggleViewAnim(final View v) {
        boolean isShow = true;
        v.setClickable(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        if (v.getVisibility() == View.VISIBLE) {
            alphaAnimation = new AlphaAnimation(1, 0);
            isShow = false;
        }
        v.setVisibility(View.VISIBLE);
        alphaAnimation.setDuration(300);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        v.startAnimation(alphaAnimation);
        final Boolean finalIsShow = isShow;
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!finalIsShow) {
                    v.setVisibility(View.GONE);
                }
                v.setClickable(true);
            }
        }, 300);
    }
}
