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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class FloatingToolbar extends FrameLayout {
    public FloatingToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingToolbar(Context context) {
        super(context);
        setBackgroundColor(Color.RED);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for (int i = 0; i < getChildCount(); i++)
                    ((Panel)getChildAt(i)).relayout(((View) getParent()).getWidth());
                getViewTreeObserver().removeOnGlobalLayoutListener(this); // CUR ; check parent size changed
            }
        });
    }

    public void addPanel(int[] actions) {
        addView(new Panel(getContext(), new Adapter(getContext(), android.R.layout.simple_list_item_1, actions), R.drawable.abc_ab_share_pack_mtrl_alpha, R.drawable.abc_ab_share_pack_mtrl_alpha), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void show(Point position) { // CUR
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.leftMargin = position.x;
        layoutParams.topMargin = position.y;
        setLayoutParams(layoutParams);
    }

    public class Adapter extends ArrayAdapter<Integer> {

        public Adapter(Context context, int resource, int[] actions) {
            super(context, resource);
            for (int action : actions) add(action); // can't use addAll on primitive array
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
}
