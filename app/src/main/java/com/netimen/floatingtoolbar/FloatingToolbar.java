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

    public FloatingToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingToolbar(Context context) {
        super(context);
        setBackgroundColor(Color.RED);
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
        addView(new Panel<>(getContext(), new Adapter(getContext(), android.R.layout.simple_list_item_1, actions), R.layout.more_button, R.layout.back_button), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void show(Point position) { // CUR
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.leftMargin = position.x;
        layoutParams.topMargin = position.y;
        setLayoutParams(layoutParams);
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
