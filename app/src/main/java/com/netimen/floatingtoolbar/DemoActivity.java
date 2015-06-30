package com.netimen.floatingtoolbar;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.activity_demo)
public class DemoActivity extends AppCompatActivity {

    @ViewById
    FrameLayout mainContainer;

    private FloatingToolbar<Integer> floatingToolbar;

    @AfterViews
    void ready() {
        floatingToolbar = new FloatingToolbar<>(this);
        floatingToolbar.addPanel(new Integer[]{1, 2, 3, 4, 5, 6});
        floatingToolbar.setListener(new FloatingToolbar.Listener<Integer>() {
            @Override
            public void actionSelected(Integer action) {
                Toast.makeText(DemoActivity.this, "aaaa " + action, Toast.LENGTH_LONG).show();
            }
        });
        mainContainer.addView(floatingToolbar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        floatingToolbar.setVisibility(View.GONE);
    }

    @Touch
    void mainContainerTouched(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP)
            if (floatingToolbar.getVisibility() == View.VISIBLE)
                floatingToolbar.hide();
            else
                floatingToolbar.show(new Point((int) e.getRawX(), (int) e.getRawY()));
    }
}
