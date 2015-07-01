package com.netimen.floatingtoolbar;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
        mainContainer.setClipChildren(false);
        floatingToolbar = new FloatingToolbar<>(this);
        floatingToolbar.addPanel(new TestAdapter());
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
                floatingToolbar.show(new Point((int) e.getX() + mainContainer.getPaddingLeft(), (int) e.getY() + mainContainer.getPaddingTop()));
    }

    private class TestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ActionView view = ActionView_.build(DemoActivity.this);
//            view.setBackgroundColor(Color.RED);
            view.bind(String.valueOf(position), "Action " + position);
            return view;
        }
    }
}
