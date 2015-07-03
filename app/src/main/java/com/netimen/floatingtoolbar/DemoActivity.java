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

    public enum Action {
        QUOTE, NOTE, SHARE_FACEBOOK, SHARE_TWITTER, SHARE_VKONTAKTE, SHARE_INSTAGRAM, COPY, TRANSLATE, PROBLEM, DELETE, COLOR_1, COLOR_2, COLOR_3, COLOR_4
    }

    @ViewById
    FrameLayout mainContainer;

    @ViewById
    FloatingToolbar<Action> floatingToolbar;

    @AfterViews
    void ready() {
        mainContainer.setClipChildren(false);
        floatingToolbar.addPanel(new TestAdapter());
        floatingToolbar.addPanel(new TestAdapter2());
        floatingToolbar.setListener(new FloatingToolbar.Listener<Action>() {
            @Override
            public void actionSelected(Action action) {
                if (action == Action.SHARE_FACEBOOK)
                    floatingToolbar.showPanel(1);
                Toast.makeText(DemoActivity.this, "aaaa " + action, Toast.LENGTH_LONG).show();
            }
        });
        floatingToolbar.setVisibility(View.GONE);
    }

    @Touch
    void mainContainerTouched(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP)
            if (floatingToolbar.getVisibility() == View.VISIBLE)
                floatingToolbar.hide();
            else
                floatingToolbar.show(new Point((int) e.getX() - mainContainer.getPaddingLeft(), (int) e.getY() - mainContainer.getPaddingTop()));
    }

    private class TestAdapter2 extends BaseAdapter {

        CharSequence[] characters = {"", "", "r", "h", "c", "!"};

        @Override
        public int getCount() {
//            return Action.values().length;
            return characters.length;
        }

        @Override
        public Object getItem(int position) {
            return Action.values()[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) { // rhc!…bt
            final ActionView view = ActionView_.build(DemoActivity.this);
            view.bind(characters[position], "Action 2 " + position);
            return view;
        }
    }

    private class TestAdapter extends BaseAdapter {

        CharSequence[] characters = {"", "", "r", "h", "c", "!"};

        @Override
        public int getCount() {
//            return Action.values().length;
            return characters.length;
        }

        @Override
        public Object getItem(int position) {
            return Action.values()[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) { // rhc!…bt
            final ActionView view = ActionView_.build(DemoActivity.this);
            view.bind(characters[position], "Action " + position);
            return view;
        }
    }

}
