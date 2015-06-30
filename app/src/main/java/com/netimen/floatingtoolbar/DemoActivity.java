package com.netimen.floatingtoolbar;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.activity_demo)
public class DemoActivity extends AppCompatActivity {

    @ViewById
    FrameLayout mainContainer;

    private FloatingToolbar floatingToolbar;

    @AfterViews
    void ready() {
        floatingToolbar = new FloatingToolbar(this);
        floatingToolbar.addPanel(new int[] {1, 2, 3, 4, 5, 6});
        mainContainer.addView(floatingToolbar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        floatingToolbar.setVisibility(View.GONE);
    }

    @Click
    void mainContainerClicked() {
        floatingToolbar.setVisibility(floatingToolbar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}
