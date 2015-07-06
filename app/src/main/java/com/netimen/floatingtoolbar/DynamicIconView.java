/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   06.07.15
 */
package com.netimen.floatingtoolbar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class DynamicIconView extends View {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = DynamicIconView.class.getSimpleName();

    IconRenderer renderer;

    public DynamicIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        renderer.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        renderer.onMeasure(getMeasuredWidth(), getMeasuredHeight());
    }

    public void setRenderer(IconRenderer renderer) {
        this.renderer = renderer;
    }

    public interface IconRenderer {
        void draw(Canvas canvas);

        void onMeasure(int measuredWidth, int measuredHeight);
    }
}
