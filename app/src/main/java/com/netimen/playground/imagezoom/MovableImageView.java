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

public class MovableImageView extends ImageView {
    private final ScaleType defaultScaleType;
    private final Matrix matrix;
    private float initialScale, currentScale;
    /**
     * image center coordinates
     */
    private int currentX, currentY;

    public MovableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultScaleType = getScaleType();
        matrix = new Matrix();
    }

    public void reset() {
        setScaleType(defaultScaleType);
    }

    public void scale(float scaleFactor) {
        if (scaleFactor <= 1) // CUR
            return;

        currentScale = initialScale * scaleFactor;
        updateScaleAndPosition();
    }

    public void move(float deltaX, float deltaY) {
        currentX += fitDeltaViewBoundaries(currentX, deltaX, getWidth(), getCurrentDrawableWidth());
        currentY += fitDeltaViewBoundaries(currentY, deltaY, getHeight(), getCurrentDrawableHeight());
        updateScaleAndPosition();
    }

    private int fitDeltaViewBoundaries(int coord, float deltaCoord, int viewDimension, float drawableDimension) {
        return (int) (deltaCoord > 0 ? Math.min(deltaCoord, Math.max(0, drawableDimension / 2 - coord)) :
                Math.max(deltaCoord, Math.min(0, viewDimension - coord - drawableDimension / 2)));
    }

    private void updateScaleAndPosition() {
        setupImageMatrixIfNeeded();

        matrix.setScale(currentScale, currentScale);
        matrix.postTranslate(currentX - getCurrentDrawableWidth() / 2, currentY - getCurrentDrawableHeight() / 2);
        setImageMatrix(matrix);
    }

    private float getCurrentDrawableWidth() {
        return getDrawable().getIntrinsicWidth() * currentScale;
    }

    private float getCurrentDrawableHeight() {
        return getDrawable().getIntrinsicHeight() * currentScale;
    }

    /**
     * sets the scale type to MATRIX and calculates initial scale and position so that current matrix will correspond to CENTER_INSIDE scale type
     */
    private void setupImageMatrixIfNeeded() {
        if (getScaleType() != ScaleType.MATRIX) {
            float w = getWidth(), h = getHeight(), dw = getDrawable().getIntrinsicWidth(), dh = getDrawable().getIntrinsicHeight();
            currentScale = initialScale = Math.min(Math.min(w / dw, h / dh), 1);
            currentX = getWidth() / 2;
            currentY = getHeight() / 2;
            setScaleType(ScaleType.MATRIX);
        }
    }

}
