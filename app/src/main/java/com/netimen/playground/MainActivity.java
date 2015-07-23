/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   23.07.15
 */
package com.netimen.playground;

import android.support.v7.app.AppCompatActivity;

import com.netimen.playground.imagezoom.ImageZoomActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.layout_text)
public class MainActivity extends AppCompatActivity {
    @AfterViews
    void ready() {
        ImageZoomActivity_.intent(this).start();
    }
}
