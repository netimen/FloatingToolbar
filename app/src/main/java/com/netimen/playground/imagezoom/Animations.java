/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   24.07.15
 */
package com.netimen.playground.imagezoom;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.netimen.playground.R;

public class Animations {
    public static void showAndMove(final View view, final Rect startBounds, final Rect endBounds, boolean show) {
        showAndMove(view, startBounds, endBounds, show, null);
    }

    public static void showAndMove(final View view, final Rect startBounds, final Rect endBounds, boolean show, final Runnable endAction) {
        if (show)
            view.setVisibility(View.VISIBLE);
        animateViewBounds(view, startBounds, endBounds, show ? endAction : new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
                if (endAction != null)
                    endAction.run();
            }
        });
    }

    public static void animateViewBounds(final View view, final Rect startBounds, final Rect endBounds, final Runnable endAction) {
        final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = startBounds.left;
        layoutParams.topMargin = startBounds.top;
        layoutParams.width = startBounds.width();
        layoutParams.height = startBounds.height();
        view.requestLayout();

        view.animate().start();
        ValueAnimator anim = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofInt("l", startBounds.left, endBounds.left),
                PropertyValuesHolder.ofInt("t", startBounds.top, endBounds.top),
                PropertyValuesHolder.ofInt("w", startBounds.width(), endBounds.width()),
                PropertyValuesHolder.ofInt("h", startBounds.height(), endBounds.height()))
                .setDuration(view.getResources().getInteger(R.integer.animation_normal));

        if (endAction != null)
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    endAction.run();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.leftMargin = (int) valueAnimator.getAnimatedValue("l");
                layoutParams.topMargin = (int) valueAnimator.getAnimatedValue("t");
                layoutParams.width = (int) valueAnimator.getAnimatedValue("w");
                layoutParams.height = (int) valueAnimator.getAnimatedValue("h");
                view.requestLayout();
//                view.getBackground().setAlpha((int) (valueAnimator.getAnimatedFraction() * 255));
            }
        });
        anim.start();
    }
}
