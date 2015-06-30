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
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Panel extends FrameLayout {
    private final ArrayAdapter<Integer> adapter;
    private final int moreButtonRes;
    private final int backButtonRes;

    private int currentPanelId;

    public Panel(Context context, ArrayAdapter<Integer> adapter, @DrawableRes int moreButtonRes, @DrawableRes int backButtonRes) { // CUR change to @LayoutRes
        super(context);
        this.adapter = adapter;
        this.moreButtonRes = moreButtonRes;
        this.backButtonRes = backButtonRes;
        setBackgroundColor(Color.GRAY);
    }

    public void relayout(int containerWidth) {
        removeAllViews();
        currentPanelId = 0;
        int currentPanelWidth = 0;
        for (int itemId = 0; itemId < adapter.getCount(); itemId++) {
            final View view = adapter.getView(itemId, null, null);
            view.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            if (currentPanelWidth + view.getMeasuredWidth() > containerWidth) {
                addViewToPanel(createMoreButton());
                currentPanelId++; // CUR check if more button also doesn't fit
            }
            if (getCurrentPanel() == null) {
                final LinearLayout innerPanel = new LinearLayout(getContext());
                innerPanel.setOrientation(LinearLayout.HORIZONTAL);
                innerPanel.setVisibility(GONE);
                addView(innerPanel, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (currentPanelId > 0) {
                    final View backButton = createBackButton();
                    addViewToPanel(backButton);
                    currentPanelWidth = backButton.getMeasuredWidth();
                } else
                    currentPanelWidth = 0;
            }
            addViewToPanel(view);
            currentPanelWidth += view.getMeasuredWidth();
        }
        showPanel(0);
    }

    private void addViewToPanel(View view) {
        getCurrentPanel().addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private View createBackButton() {
        final View view = createSpecialButton(backButtonRes);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPanel(currentPanelId - 1);
            }
        });
        return view;
    }

    private void showPanel(int panelId) { // CUR rename
        getCurrentPanel().setVisibility(GONE);
        currentPanelId = panelId;
        getCurrentPanel().setVisibility(VISIBLE);
    }

    private View createMoreButton() {
        final View view = createSpecialButton(moreButtonRes);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPanel(currentPanelId + 1);
            }
        });
        return view;
    }

    private View createSpecialButton(@DrawableRes int drawableRes) {
        final ImageView view = new ImageView(getContext());
        view.setImageResource(drawableRes);
        view.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return view;
    }

    ViewGroup getCurrentPanel() {
        return (currentPanelId == -1 || currentPanelId >= getChildCount()) ? null : (ViewGroup) getChildAt(currentPanelId);
    }
}
