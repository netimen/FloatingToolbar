/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   01.07.15
 */
package com.netimen.floatingtoolbar;


import android.view.View;

/**
 * implements fade in/fade out animation and supports checking against to frequent restarting.
 */
public class FadeAnimator {
    private int lastDesiredVisibility = -1;
    private int animationDuration;

    public FadeAnimator() {
    }

    public FadeAnimator(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setDuration(int duration) {
        animationDuration = duration;
    }

    public void animateVisibility(final View view, boolean visible) {
        animateVisibility(view, visible, false);
    }

    /**
     * @param preventRestarting if true, doesn't restart if desired visibility is the same as last time
     */
    public void animateVisibility(final View view, final boolean visible, boolean preventRestarting) {
        int desiredVisility = visible ? View.VISIBLE : View.INVISIBLE;
        if (preventRestarting && desiredVisility == lastDesiredVisibility)
            return;

        lastDesiredVisibility = desiredVisility;
        if (visible) view.setVisibility(View.VISIBLE);
        view.setAlpha(visible ? 0 : 1);

        view.animate().alpha(visible ? 1 : 0).setDuration(animationDuration).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (!visible) {
                    view.setVisibility(View.INVISIBLE);
                    view.setAlpha(1); // without this we get strange results if we call view.setVisibility(VISIBLE) somewhere in code after animateVisibility
                }
                lastDesiredVisibility = -1;
            }
        });
    }

}
