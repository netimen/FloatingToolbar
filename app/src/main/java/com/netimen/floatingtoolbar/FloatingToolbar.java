/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.06.15
 */
package com.netimen.floatingtoolbar;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.FrameLayout;

public class FloatingToolbar extends FrameLayout {
    private static final String LOG_TAG = FloatingToolbar.class.getSimpleName();
    public static final int TECHNICAL_CHILDREN_COUNT = 1;
    private final FadeAnimator fadeAnimator;
    private int currentContainerWidth;

    @LayoutRes
    int moreButtonLayout, backButtonLayout;
    private int animationDuration = 300;
    private View backgroundView;
    private int currentPanelId;
    /**
     * show panel 0, don't remember last showing panel
     */
    private boolean resetStateBeforeShow;


    public FloatingToolbar(Context context) {
        this(context, null);
    }

    public FloatingToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        fadeAnimator = new FadeAnimator(animationDuration);

        initBackground(context);

        moreButtonLayout = R.layout.more_button;
        backButtonLayout = R.layout.back_button; // CUR builder, attr etc
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int containerWidth = ((View) getParent()).getWidth() - ((View) getParent()).getPaddingLeft() - ((View) getParent()).getPaddingRight(); // CUR paddings/margins
                Log.d(LOG_TAG, "onGlobalLayout: pW: " + currentContainerWidth + " w: " + containerWidth);
                if (currentContainerWidth != containerWidth && containerWidth > 0) { // containerWidth can be < 0 when getWidth is 0, and paddings are > 0
                    currentContainerWidth = containerWidth;
                    int maxHeight = 0;
                    for (int i = 0; i < getPanelCount(); i++) { // skipping background child
                        final Panel panel = getPanel(i);
                        panel.relayout(containerWidth);
                        if (panel.getMeasuredHeight() > maxHeight)
                            maxHeight = panel.getMeasuredHeight();
                    }

                    final ViewGroup.LayoutParams layoutParams = backgroundView.getLayoutParams();
                    layoutParams.height = maxHeight;
                    backgroundView.setLayoutParams(layoutParams);
                }
            }
        });
    }

    private int getPanelCount() {
        return getChildCount() - TECHNICAL_CHILDREN_COUNT;
    }

    /**
     * we need to store background in a different view, so we can animate it easily
     */
    private void initBackground(Context context) {
        backgroundView = new View(context);
        backgroundView.setBackground(getBackground());
        setBackground(null); // without this they share common drawable with Toolbar
        setBackgroundColor(Color.TRANSPARENT);
        addView(backgroundView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
    }

    public int addPanel(Adapter adapter) {
        final Panel panel = new Panel(getContext(), adapter);
        if (getPanelCount() > 0) // initially only first panel is visible
            panel.setVisibility(INVISIBLE);
        addView(panel, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        return getPanelCount() - 1; // index of this panel
    }

    public void show(Point position) {
        if (getMeasuredWidth() == 0)
            measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        resetStateIfNeeded();

        final MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        layoutParams.leftMargin = Math.min(position.x, currentContainerWidth - getMeasuredWidth()); // fitting to screen by x axis
        layoutParams.topMargin = position.y;
        setLayoutParams(layoutParams);
        changeVisibility(true);
    }

    /**
     * displays panel 0, and scrolls all the panels to beginning
     */
    private void resetStateIfNeeded() {
        if (resetStateBeforeShow) {
            showPanel(0);
            for (int i = 0; i < getPanelCount(); i++)
                getPanel(i).showContainer(0);
        }
    }

    public void showPanel(int panelId) {
        changePanels(getPanel(panelId), getPanel(currentPanelId));
        currentPanelId = panelId;
    }

    private Panel getPanel(int panelId) {
        return (Panel) getChildAt(panelId + TECHNICAL_CHILDREN_COUNT);
    }

    /**
     * @param rememberPosition if true, after calling again to {@link #show}, toolbar will display again current panel in current position
     */
    public void hide(boolean rememberPosition) {
        changeVisibility(false);
        resetStateBeforeShow = !rememberPosition;
    }

    protected void changeVisibility(boolean visible) { // CUR animation
        if (true)
            fadeAnimator.animateVisibility(this, visible, true);
        else
            setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), heightMeasureSpec); // no horizontal crop
    }

    protected void changePanels(final View showing, final View hiding) { // CUR move to another class
        changePanels(showing, hiding, true);
    }

    protected void changePanels(final View showing, final View hiding, boolean animate) { // CUR move to another class
        if (animate && hiding.getVisibility() == VISIBLE && getVisibility() == VISIBLE) { // no need to animate if nothing is visible, so checking
            final int duration = animationDuration * 2 / 5;
            hiding.animate().alpha(0).setDuration(duration).withEndAction(new Runnable() {
                @Override
                public void run() {
                    hiding.setVisibility(INVISIBLE);
                    showing.setVisibility(VISIBLE);
                    showing.animate().alpha(1).setDuration(duration).setStartDelay(animationDuration - 2 * duration);
                }
            });
            animateWidthTo(backgroundView, showing.getMeasuredWidth(), animationDuration);
        } else {
            hiding.setVisibility(INVISIBLE);
            hiding.setAlpha(0);
            showing.setVisibility(VISIBLE);
            showing.setAlpha(1);
            final ViewGroup.LayoutParams layoutParams = backgroundView.getLayoutParams();
            layoutParams.width = showing.getMeasuredWidth();
            backgroundView.setLayoutParams(layoutParams);
        }
    }

    public static void animateWidthTo(final View view, int newWidth, int animationDuration) {
        ValueAnimator anim = ValueAnimator.ofInt(view.getWidth(), newWidth).setDuration(animationDuration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = val;
                view.setLayoutParams(layoutParams);
            }
        });
        anim.start();
    }

    /**
     * tries to find view corresponding to specified object in all the panels
     */
    public View getActionView(Object o) {
        for (int i = 0; i < getPanelCount(); i++) { // skipping background child
            final View actionView = getPanel(i).getActionView(o);
            if (actionView != null)
                return actionView;
        }
        return null;
    }

//    public class SimpleAdapter extends ArrayAdapter<T> {
//
//        public SimpleAdapter(Context context, int resource, T[] actions) {
//            super(context, resource);
//            addAll(actions);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            final View view = super.getView(position, convertView, parent);
//            ((TextView) view).setText("action " + position);
//            view.setBackgroundColor(Color.BLUE);
//            ((TextView) view).setSingleLine();
//            return view;
//        }
//    }

}
