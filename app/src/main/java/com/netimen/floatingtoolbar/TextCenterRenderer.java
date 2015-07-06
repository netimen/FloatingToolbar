/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   06.07.15
 */
package com.netimen.floatingtoolbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * renders text, so it's center is at specified point
 */
public class TextCenterRenderer {
    private final Paint paint = new Paint();
    private final String text;
    private final Rect textRect = new Rect();

    public TextCenterRenderer(String text) {
        this.text = text;
    }

    public void onMeasure(float circleRadius) {
        final float textSize = circleRadius * 3 / 2;
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), textRect);
    }

    public void draw(Canvas canvas, float x, float y) {
        canvas.drawText(text, x - textRect.width() / 2, y + textRect.height() / 2, paint);
    }
}
