/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   01.07.15
 */
package com.netimen.floatingtoolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

@SuppressLint("ViewConstructor")
public class Container extends LinearLayout {
    /**
     * container position in this panel. If 0 — has no "back" button, but possibly has "more" etc
     */
    private final int positionInPanel;
    private int maxChildHeight;

    public Container(Context context, int positionInPanel) {
        super(context);
        this.positionInPanel = positionInPanel;
        setOrientation(LinearLayout.HORIZONTAL);
        setVisibility(GONE);
    }

    public boolean hasBackButton() {
        return positionInPanel > 0;
    }

    private boolean hasMoreButton() {
        return ((Panel) getParent()).getChildCount() - 1 > positionInPanel; // checking this is not the last container in this panel
    }

    public View getFirstActionView() {
        return getChildAt(positionInPanel == 0 ? 0 : 1);
    }

    public int getMaxChildHeight() {
        return maxChildHeight;
    }

    @Override
    public void addView(@NonNull View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        if (child.getMeasuredHeight() > maxChildHeight)
            maxChildHeight = child.getMeasuredHeight();
    }

    public void adjustBackMoreButtonsHeight() {
        if (hasBackButton())
            adjustViewHeight(getChildAt(0));
        if (hasMoreButton())
            adjustViewHeight(getChildAt(getChildCount() - 1));
        measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)); // needed for backgound size calculation
    }

    private void adjustViewHeight(View view) {
        if (view.getMeasuredHeight() < maxChildHeight) { // making it's height to fill the toolbar
            final int extraPadding = (maxChildHeight - view.getMeasuredHeight()) / 2;
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + extraPadding, view.getPaddingRight(), view.getPaddingBottom() + extraPadding);
        }
    }
}
