/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   23.07.15
 */
package com.netimen.playground.imagezoom;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.netimen.playground.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.layout_text)
public class ImageZoomActivity extends AppCompatActivity {
    private static final String LOG_TAG = ImageZoomActivity.class.getSimpleName();
    private ScaleGestureDetector scaleGestureDetector;

    @AfterViews
    void ready() {
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.e(LOG_TAG, "AAAA onScale " + detector.getCurrentSpan() + " " + detector.getPreviousSpan());
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event);
    }
}
