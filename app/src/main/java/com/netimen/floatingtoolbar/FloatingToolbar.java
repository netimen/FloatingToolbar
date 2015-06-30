/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.06.15
 */
package com.netimen.floatingtoolbar;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class FloatingToolbar<T> extends FrameLayout {
    private static final String LOG_TAG = FloatingToolbar.class.getSimpleName();
    private int previousContainerWidth;
    private Listener<T> listener;

    @LayoutRes
    int moreButtonLayout, backButtonLayout;


    public FloatingToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingToolbar(Context context) {
        super(context);
        setBackgroundColor(Color.RED);
        moreButtonLayout = R.layout.more_button;
        backButtonLayout = R.layout.back_button; // CUR builder, attr etc
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int containerWidth = ((View) getParent()).getWidth() - ((View) getParent()).getPaddingLeft() - ((View) getParent()).getPaddingRight(); // CUR paddings/margins
                Log.d(LOG_TAG, "onGlobalLayout: pW: " + previousContainerWidth + " w: " + containerWidth);
                if (previousContainerWidth != containerWidth && containerWidth > 0) { // containerWidth can be < 0 when getWidth is 0, and paddings are > 0
                    previousContainerWidth = containerWidth;
                    for (int i = 0; i < getChildCount(); i++)
                        ((Panel) getChildAt(i)).relayout(containerWidth);
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

    public void addPanel(T[] actions) {
        addView(new Panel<>(getContext(), new Adapter(getContext(), android.R.layout.simple_list_item_1, actions)), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void show(Point position) {
        final MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        layoutParams.leftMargin = position.x;
        layoutParams.topMargin = position.y;
        setLayoutParams(layoutParams);
        changeVisibility(true);
    }

    public void hide() {
        changeVisibility(false);
    }

    private void changeVisibility(boolean visible) { // CUR animation
        setVisibility(visible ? VISIBLE : GONE);
    }

    public class Adapter extends ArrayAdapter<T> {

        public Adapter(Context context, int resource, T[] actions) {
            super(context, resource);
//            for (int action : actions) add(action); // can't use addAll on primitive array
            addAll(actions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);
            ((TextView) view).setText("Toolbar action " + position);
            view.setBackgroundColor(Color.BLUE);
            ((TextView) view).setSingleLine();
            return view;
        }
    }

    public interface Listener<T> {
        void actionSelected(T action);
    }
}
