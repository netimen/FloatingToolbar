/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   23.07.15
 */
package com.netimen.playground.imagezoom;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.netimen.playground.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_image_zoom)
public class ImageZoomActivity extends AppCompatActivity {
    private static final String LOG_TAG = ImageZoomActivity.class.getSimpleName();

    @ViewById
    ImageView smallPicture, bigPicture;

    @ViewById
    MovableImageView zoomImage;

    @ViewById
    ViewGroup mainContainer;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Rect initialImageBounds;

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

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.e(LOG_TAG, "AAAA onScroll " + distanceX + " " + distanceY);
                if (zoomImage.getVisibility() == View.VISIBLE)
                    zoomImage.move(-distanceX, -distanceY); // when I move finger right, distanceX is < 0 for some reason; same for Y
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                hideZoomImage();
                return super.onSingleTapConfirmed(e);
            }
        });
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            /**
             * https://developer.android.com/reference/android/view/ScaleGestureDetector.html#getFocusX() will return nonsense in onScaleEnd, so we need to store values ourselves
             */
            private float focusX, focusY;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                focusX = detector.getFocusX();
                focusY = detector.getFocusY();
                if (zoomImage.getVisibility() == View.VISIBLE)
                    zoomImage.scale(detector.getScaleFactor());
                return false;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                if (zoomImage.getVisibility() != View.VISIBLE && detector.getScaleFactor() > 1)
                    zoomImageAtPoint(focusX, focusY);
                else if (zoomImage.getVisibility() == View.VISIBLE && detector.getScaleFactor() < 1)
                    hideZoomImage();
            }

        });
    }

    private void hideZoomImage() {
        Animations.showAndMove(zoomImage, new Rect(0, 0, mainContainer.getWidth(), mainContainer.getHeight()), initialImageBounds, false);
    }

    private void zoomImageAtPoint(float x, float y) {
        final ImageView imageToZoom = isPointInsideView(x, y, bigPicture) ? bigPicture : (isPointInsideView(x, y, smallPicture) ? smallPicture : null);
        if (imageToZoom != null)
            zoomImage(imageToZoom.getDrawable(), new Rect(imageToZoom.getLeft(), imageToZoom.getTop(), imageToZoom.getRight(), imageToZoom.getBottom()));
    }

    private void zoomImage(Drawable drawable, Rect imageBounds) {
        zoomImage.setImageDrawable(drawable);
        zoomImage.reset();
        zoomImage.setVisibility(View.VISIBLE);
        initialImageBounds = imageBounds;
        Animations.showAndMove(zoomImage, imageBounds, new Rect(0, 0, mainContainer.getWidth(), mainContainer.getHeight()), true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = scaleGestureDetector.onTouchEvent(event);
        result |= gestureDetector.onTouchEvent(event);
        return result;
    }

    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        return (x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()));
    }
}
