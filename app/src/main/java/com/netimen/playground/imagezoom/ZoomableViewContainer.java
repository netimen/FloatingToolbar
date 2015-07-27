/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   27.07.15
 */
package com.netimen.playground.imagezoom;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class ZoomableViewContainer extends FrameLayout {
    private Rect initialImageBounds;
    /**
     * zoom image is visible and animation has finished
     */
    private boolean zoomImageActive;
    public ZoomableViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void zoomImage(Drawable drawable, final Rect imageBounds) {
        zoomImage.setVisibility(View.VISIBLE);
        initialImageBounds = imageBounds;
        Animations.showAndMove(zoomImage, imageBounds, new Rect(0, 0, mainContainer.getWidth(), mainContainer.getHeight()), true, new Runnable() {
            @Override
            public void run() {
                zoomImageActive = true;
            }
        });
    }

    public interface ViewAnimation {

    }
}
