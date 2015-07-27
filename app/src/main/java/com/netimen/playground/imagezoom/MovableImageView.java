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
    private float currentScale, minScale;
    /**
     * image center coordinates
     */
    private float currentX, currentY;

    public MovableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultScaleType = getScaleType();
        matrix = new Matrix();
    }

    public void reset() {
        setScaleType(defaultScaleType);
    }

    /**
     * new scale = currentScale * scaleFactor
     */
    public void scale(float scaleFactor) {
        currentScale = Math.max(minScale, currentScale * scaleFactor);
        updateScaleAndPosition();
    }

    /**
     * new x = x + deltaX
     */
    public void move(float deltaX, float deltaY) {
        currentX += deltaX;
        currentY += deltaY;
        updateScaleAndPosition();
    }

    /**
     * for images lesser than view, simply means that currentScale is > 1, otherwise means that one of image's dimensions is bigger than view's
     */
    public boolean isUpScaled() {
        return getScaleType() == ScaleType.MATRIX && (currentScale > 1 || getCurrentDrawableWidth() > getWidth() || getCurrentDrawableHeight() > getHeight());
    }

    private float fitViewBoundaries(float coord, int viewDimension, float drawableDimension) {
        if (viewDimension > drawableDimension)
            return viewDimension / 2;

        if (coord > drawableDimension / 2) // left/top
            return drawableDimension / 2;

        if (coord + drawableDimension / 2 < viewDimension) // right/bottom
            return viewDimension - drawableDimension / 2;

        return coord;
    }

    private void updateScaleAndPosition() {
        setupImageMatrixIfNeeded();

        currentX = fitViewBoundaries(currentX, getWidth(), getCurrentDrawableWidth()); // we do it here, because we need to update position when downscaling
        currentY = fitViewBoundaries(currentY, getHeight(), getCurrentDrawableHeight());

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
            currentScale = Math.min(Math.min(w / dw, h / dh), 1);
            minScale = Math.min(currentScale, 1);
            currentX = getWidth() / 2;
            currentY = getHeight() / 2;
            setScaleType(ScaleType.MATRIX);
        }
    }

} // CUR rotate screen
