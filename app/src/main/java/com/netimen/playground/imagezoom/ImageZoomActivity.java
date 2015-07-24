/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   23.07.15
 */
package com.netimen.playground.imagezoom;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.netimen.playground.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_image_zoom)
public class ImageZoomActivity extends AppCompatActivity {
    private static final String LOG_TAG = ImageZoomActivity.class.getSimpleName();

    @ViewById
    ImageView smallPicture, bigPicture, zoomImage;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    @AfterViews
    void ready() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                zoomImageAtPoint(e.getRawX(), e.getRawY());
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }
        });
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            /**
             * https://developer.android.com/reference/android/view/ScaleGestureDetector.html#getFocusX() will return nonsense in onScaleEnd, so we need to store values ourselves
             */
            private float focusX, focusY;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.e(LOG_TAG, "AAAA onScale " + detector.getCurrentSpan() + " " + detector.getPreviousSpan() + " " + detector.getFocusX());
                focusX = detector.getFocusX();
                focusY = detector.getFocusY();
                return false;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                zoomImageAtPoint(focusX, focusY);
            }

        });
    }

    private void zoomImageAtPoint(float x, float y) {
        final ImageView imageToZoom = isPointInsideView(x, y, bigPicture) ? bigPicture : (isPointInsideView(x, y, smallPicture) ? smallPicture : null);
        if (imageToZoom != null)
            zoomImage(imageToZoom.getDrawable());
    }
    private void zoomImage(Drawable drawable) {
        zoomImage.setImageDrawable(drawable);
        zoomImage.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = scaleGestureDetector.onTouchEvent(event);
        result |= gestureDetector.onTouchEvent(event);
        return  result;
    }

    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }
}
