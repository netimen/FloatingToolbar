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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class FloatingToolbar<T> extends FrameLayout {
    private static final String LOG_TAG = FloatingToolbar.class.getSimpleName();
    private int currentContainerWidth;
    private Listener<T> listener;

    @LayoutRes
    int moreButtonLayout, backButtonLayout;
    private int animationDuration = 800;
    private View backgoundView;


    public FloatingToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingToolbar(Context context) {
        super(context);
        setBackgroundColor(Color.TRANSPARENT);
        backgoundView = new View(context);
        addView(backgoundView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
//        setBackgroundColor(Color.MAGENTA);
        backgoundView.setBackgroundColor(Color.LTGRAY);
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
                    for (int i = 1; i < getChildCount(); i++) { // skipping background child
                        final Panel panel = (Panel) getChildAt(i);
                        panel.relayout(containerWidth);
                        if (panel.getMeasuredHeight() > maxHeight)
                            maxHeight = panel.getMeasuredHeight();
                    }

                    final ViewGroup.LayoutParams layoutParams = backgoundView.getLayoutParams();
                    layoutParams.height = maxHeight;
                    backgoundView.setLayoutParams(layoutParams);
                }
            }
        });
    }

    public void setListener(Listener<T> listener) {
        this.listener = listener;
    }

    @Nullable
    public Listener<T> getListener() {
        return listener;
    }

    public void addPanel(Adapter adapter) {
        addView(new Panel<>(getContext(), adapter), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
    }

    public void addPanel(T[] actions) {
        addPanel(new SimpleAdapter(getContext(), android.R.layout.simple_list_item_1, actions));
    }

    public void show(Point position) {
        if (getMeasuredWidth() == 0)
            measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        final MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        layoutParams.leftMargin = Math.min(position.x, currentContainerWidth - getMeasuredWidth()); // fitting to screen by x axis
        layoutParams.topMargin = position.y;
        setLayoutParams(layoutParams);
        changeVisibility(true);
    }

    public void hide() {
        changeVisibility(false);
    }

    protected void changeVisibility(boolean visible) { // CUR animation
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
        if (animate) {
            final int duration = animationDuration * 2 / 5;
            hiding.animate().alpha(0).setDuration(duration).withEndAction(new Runnable() { // CUR make min API 16
                @Override
                public void run() {
                    hiding.setVisibility(INVISIBLE);
                    showing.setVisibility(VISIBLE);
                    showing.animate().alpha(1).setDuration(duration).setStartDelay(animationDuration - 2 * duration);
                }
            });
            animateWidthTo(backgoundView, showing.getMeasuredWidth(), animationDuration);
        } else {
            hiding.setVisibility(GONE);
            hiding.setAlpha(0);
            showing.setVisibility(VISIBLE);
            showing.setAlpha(1);
            final ViewGroup.LayoutParams layoutParams = backgoundView.getLayoutParams();
            layoutParams.width = showing.getMeasuredWidth();
            backgoundView.setLayoutParams(layoutParams);
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

    public class SimpleAdapter extends ArrayAdapter<T> {

        public SimpleAdapter(Context context, int resource, T[] actions) {
            super(context, resource);
            addAll(actions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);
            ((TextView) view).setText("action " + position);
            view.setBackgroundColor(Color.BLUE);
            ((TextView) view).setSingleLine();
            return view;
        }
    }

    public interface Listener<T> {
        void actionSelected(T action);
    }
} // CUR height regulation
