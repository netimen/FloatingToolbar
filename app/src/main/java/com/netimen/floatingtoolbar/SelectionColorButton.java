/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.07.15
 */
package com.netimen.floatingtoolbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.widget.ToggleButton;

public class SelectionColorButton extends ToggleButton {

    private final int size;
    private final float innerRadiusRatio;
    private final float outerRadiusRatio;
    private final float gapRatio;
    private final int color;
    private Paint paint, strokePaint;

    public SelectionColorButton(Context context, int size, float innerRadiusRatio, float outerRadiusRatio, float gapRatio, int color) {
        super(context);
        this.size = size;
        this.innerRadiusRatio = innerRadiusRatio;
        this.outerRadiusRatio = outerRadiusRatio;
        this.gapRatio = gapRatio;
        this.color = color;
        setBackground(null);

        paint = new Paint();
        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        setupColors(Color.WHITE); // CUR
    }

    /**
     * this button displays color of marker blended with background color
     */
    public void setupColors(int bgColor) {
        final int color = Utils.blendColors(this.color, bgColor);
        strokePaint.setColor(color);
        paint.setColor(color);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        final int cx = getWidth() / 2;
        final int cy = getHeight() / 2;
        final int radius = getHeight() / 2;
        canvas.drawCircle(cx, cy, radius * innerRadiusRatio, paint);
        if (isChecked())
            canvas.drawCircle(cx, cy, radius * outerRadiusRatio, strokePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
        strokePaint.setStrokeWidth(getMeasuredHeight() / 2 * (outerRadiusRatio - gapRatio - innerRadiusRatio));
    }

}
