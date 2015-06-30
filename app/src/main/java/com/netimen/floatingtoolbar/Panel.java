/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.06.15
 */
package com.netimen.floatingtoolbar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Contains the action views and layouts them so they fit the maximum width and distributes them to several containers navigable via more/back buttons if needed
 * CUR animation & stretching
 */
public class Panel extends FrameLayout {
    private final ArrayAdapter<Integer> adapter;
    @LayoutRes
    private final int moreButtonLayout, backButtonLayout;

    private int currentContainerId;

    public Panel(Context context, ArrayAdapter<Integer> adapter, @LayoutRes int moreButtonLayout, @LayoutRes int backButtonLayout) {
        super(context);
        this.adapter = adapter;
        this.moreButtonLayout = moreButtonLayout;
        this.backButtonLayout = backButtonLayout;
        setBackgroundColor(Color.GRAY); // CUR
    }

    public void relayout(int containerWidth) {
        removeAllViews();
        currentContainerId = 0;
        int currentContainerWidth = 0;
        for (int itemId = 0; itemId < adapter.getCount(); itemId++) {
            View actionView = adapter.getView(itemId, null, null);
            actionView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            if (currentContainerWidth + actionView.getMeasuredWidth() > containerWidth) {
                final View moreButton = createMoreButton();
                addViewToContainer(moreButton);
                if (currentContainerWidth + moreButton.getMeasuredWidth() > containerWidth) { // if even 'more' button doesn't fit, we need to remove last action view and add it to the next container
                    actionView = getCurrentContainer().getChildAt(getCurrentContainer().getChildCount() - 2); // last added action view
                    getCurrentContainer().removeView(actionView);
                    itemId--;
                }
                currentContainerId++;
            }

            if (getCurrentContainer() == null) {
                final LinearLayout container = new LinearLayout(getContext());
                container.setOrientation(LinearLayout.HORIZONTAL);
                container.setVisibility(GONE);
                addView(container, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (currentContainerId > 0) {
                    final View backButton = createBackButton();
                    addViewToContainer(backButton);
                    currentContainerWidth = backButton.getMeasuredWidth();
                } else
                    currentContainerWidth = 0;
            }
            addViewToContainer(actionView);
            currentContainerWidth += actionView.getMeasuredWidth();
        }
        showContainer(0);
    }

    private void addViewToContainer(View view) {
        getCurrentContainer().addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void showContainer(int containerId) {
        getCurrentContainer().setVisibility(GONE);
        currentContainerId = containerId;
        getCurrentContainer().setVisibility(VISIBLE);
    }

    ViewGroup getCurrentContainer() {
        return (currentContainerId == -1 || currentContainerId >= getChildCount()) ? null : (ViewGroup) getChildAt(currentContainerId);
    }

    /// more/back buttons

    private View createBackButton() {
        final View view = createSpecialButton(backButtonLayout);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showContainer(currentContainerId - 1);
            }
        });
        return view;
    }

    private View createMoreButton() {
        final View view = createSpecialButton(moreButtonLayout);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showContainer(currentContainerId + 1);
            }
        });
        return view;
    }

    private View createSpecialButton(@LayoutRes int layoutRes) {
        final View view = inflate(getContext(), layoutRes, null);
        view.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return view;
    }
}
