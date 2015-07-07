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

    private final int width;
    private final int color;
    private final Paint paint, strokePaint;
    private final TextCenterRenderer textRenderer;
    private final int innerRadius, outerRadius;

    public SelectionColorButton(Context context, int innerRadius, int color) {
        super(context);
        this.innerRadius = innerRadius;
        outerRadius = (int) (innerRadius * 1.2f);
        width = (int) (outerRadius * 2.4f);
        this.color = color;
        setBackground(null);
        setPadding(0, 0, 0, 0); // overrides default toggle button's paddings

        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        strokePaint = new Paint();
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth((outerRadius - innerRadius) * 2 / 3);

        textRenderer = new TextCenterRenderer(getContext().getString(R.string.selection_quote));
        textRenderer.onMeasure(innerRadius);

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
        final int cx = getWidth() / 2, cy = canvas.getHeight() / 2;
        canvas.drawCircle(cx, cy, innerRadius, paint);
        if (isChecked()) {
            canvas.drawCircle(cx, cy, outerRadius, strokePaint);
            textRenderer.draw(canvas, cx, cy);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

}
