/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   24.07.15
 */
package com.netimen.playground.imagezoom;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScalableImageView extends ImageView {
    private final ScaleType defaultScaleType;
    private final Matrix matrix;
    private float initialScale;

    public ScalableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultScaleType = getScaleType();
        matrix = new Matrix();
    }

    public void resetScale() {
        setScaleType(defaultScaleType);
    }

    public void scale(float scaleFactor) {
        if (scaleFactor <= 1)
            return;

        if (getScaleType() != ScaleType.MATRIX) {
            float w = getWidth(), h = getHeight(), dw = getDrawable().getIntrinsicWidth(), dh = getDrawable().getIntrinsicHeight();
            initialScale = Math.min(Math.min(w / dw, h / dh), 1);
            setScaleType(ScaleType.MATRIX);
        }
        final float scale = initialScale * scaleFactor;
        matrix.setScale(scale, scale);
        matrix.postTranslate((getWidth() - getDrawable().getIntrinsicHeight()) / 2, (getHeight() - getDrawable().getIntrinsicHeight()) / 2);
        setImageMatrix(matrix);
    }
}
