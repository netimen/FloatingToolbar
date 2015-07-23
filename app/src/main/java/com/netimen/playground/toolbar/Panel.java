/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.06.15
 */
package com.netimen.playground.toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the action views and layouts them so they fit the maximum width and distributes them to several containers navigable via more/back buttons if needed
 */
@SuppressLint("ViewConstructor")
public class Panel extends FrameLayout {
    private int currentContainerId;
    private final Adapter adapter;
    private int visibleActionPosition;
    private Map<Object, View> item2views = new HashMap<>();
    private OnClickListener backButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showContainer(currentContainerId - 1);
        }
    };
    private OnClickListener moreButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showContainer(currentContainerId + 1);
        }
    };

    public Panel(Context context, Adapter adapter) {
        super(context);
        this.adapter = adapter;
        setBackgroundColor(Color.TRANSPARENT);
    }

    public void relayout(int containerWidth) {
        removeAllViews();
        currentContainerId = 0;
        int currentContainerWidth = 0, containerToShow = 0;
        for (int position = 0; position < adapter.getCount(); position++) {
            View actionView = createActionView(position);
            actionView.setTag(position); // needed to easily get view position in adapter later

            if (currentContainerWidth + actionView.getMeasuredWidth() > containerWidth) {
                final View moreButton = createButton(getToolbar().moreButtonLayout, moreButtonClickListener);
                getCurrentContainer().addView(moreButton);
                if (currentContainerWidth + moreButton.getMeasuredWidth() > containerWidth) { // if even 'more' button doesn't fit, we need to remove last action view and add it to the next container
                    actionView = getCurrentContainer().getChildAt(getCurrentContainer().getChildCount() - 2); // last added action view
                    getCurrentContainer().removeView(actionView);
                    position--;
                }
                currentContainerId++;
            }

            if (getCurrentContainer() == null) {
                final Container container = new Container(getContext(), currentContainerId);
                addView(container, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                container.setPadding(getToolbar().paddingLeft, getToolbar().paddingTop, getToolbar().paddingRight, getToolbar().paddingBottom);

                if (container.hasBackButton()) {
                    final View backButton = createButton(getToolbar().backButtonLayout, backButtonClickListener);
                    getCurrentContainer().addView(backButton);
                    currentContainerWidth = backButton.getMeasuredWidth();
                } else
                    currentContainerWidth = 0;

                if (visibleActionPosition >= position)
                    containerToShow = currentContainerId;
            }
            getCurrentContainer().addView(actionView);
            currentContainerWidth += actionView.getVisibility() == GONE ? 0 : actionView.getMeasuredWidth();
        }

        showContainer(containerToShow); // order is important: we first measure, then show, because background animation relies on size
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), heightMeasureSpec); // no horizontal crop
    }

    void showContainer(int containerId) {
        final View showing = getChildAt(containerId), hiding = getChildAt(currentContainerId);
        if (showing.getMeasuredWidth() == 0)
            showing.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)); // need for background size adjusting

        if (getVisibility() == VISIBLE) {
            getToolbar().changePanels(showing, hiding); // animate, adjust background etc
        } else {
            hiding.setVisibility(INVISIBLE);
            showing.setVisibility(VISIBLE);
        }
        currentContainerId = containerId;
        visibleActionPosition = getViewPositionInAdapter(getCurrentContainer().getFirstActionView());
    }

    private Container getCurrentContainer() {
        return (currentContainerId == -1 || currentContainerId >= getChildCount()) ? null : (Container) getChildAt(currentContainerId);
    }


    public View getActionView(Object o) {
        return item2views.get(o);
    }

    private View createActionView(int position) {
        final View view = adapter.getView(position, null, null);
        measureView(view);
        item2views.put(adapter.getItem(position), view);
        return view;
    }

    /**
     * creates more/back button, measures and sets ClickListener
     */
    private View createButton(@LayoutRes int layoutRes, OnClickListener onClickListener) {
        final View view = inflate(getContext(), layoutRes, null);
        measureView(view);
        view.setOnClickListener(onClickListener);
        return view;
    }

    private void measureView(View view) {
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)); // on < 4.4 on Samsungs measure() crashes without setting LP first http://stackoverflow.com/a/15750783/190148
        view.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    private int getViewPositionInAdapter(View v) {
        return (int) v.getTag();
    }

    private FloatingToolbar getToolbar() {
        return (FloatingToolbar) getParent();
    }
}
