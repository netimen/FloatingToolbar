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
    private final Paint paint, strokePaint;
    private final TextCenterRenderer textRenderer;

    public SelectionColorButton(Context context, int size, float innerRadiusRatio, float outerRadiusRatio, float gapRatio, int color) {
        super(context);
        this.size = size;
        this.innerRadiusRatio = innerRadiusRatio;
        this.outerRadiusRatio = outerRadiusRatio;
        this.gapRatio = gapRatio;
        this.color = color;
        setBackground(null);

        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        strokePaint = new Paint();
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        setupColors(Color.WHITE); // CUR
        textRenderer = new TextCenterRenderer(getContext().getString(R.string.selection_quote));
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
        final int radius = calcRadius();
        final int cx = getWidth() / 2;
        canvas.drawCircle(cx, radius, radius * innerRadiusRatio, paint);
        if (isChecked()) {
            canvas.drawCircle(cx, radius, radius * outerRadiusRatio, strokePaint);
            textRenderer.draw(canvas, cx, radius);
        }
    }

    private int calcRadius() {
        return getMeasuredHeight() / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
        strokePaint.setStrokeWidth(getMeasuredHeight() / 2 * (outerRadiusRatio - gapRatio - innerRadiusRatio));
        textRenderer.onMeasure(calcRadius() * innerRadiusRatio);
    }

}
