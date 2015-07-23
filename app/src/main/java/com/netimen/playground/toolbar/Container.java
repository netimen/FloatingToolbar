/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   01.07.15
 */
package com.netimen.playground.toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

@SuppressLint("ViewConstructor")
public class Container extends LinearLayout {
    /**
     * container position in this panel. If 0 — has no "back" button, but possibly has "more" etc
     */
    private final int positionInPanel;

    public Container(Context context, int positionInPanel) {
        super(context);
        this.positionInPanel = positionInPanel;
        setOrientation(LinearLayout.HORIZONTAL);
        setVisibility(GONE);
    }

    public boolean hasBackButton() {
        return positionInPanel > 0;
    }

    public View getFirstActionView() {
        return getChildAt(positionInPanel == 0 ? 0 : 1);
    }

}
