/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   06.07.15
 */
package com.netimen.floatingtoolbar;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.dynamic_icon_action_view)
public class DynamicIconActionView extends RelativeLayout {
    @ViewById
    TextView caption;

    @ViewById
    DynamicIconView icon;

    public DynamicIconActionView(Context context) {
        super(context);
    }

    public View bind(DynamicIconView.IconRenderer iconRenderer, int iconWidth, String caption) {
        icon.setRenderer(iconRenderer);
        icon.getLayoutParams().width = iconWidth;
        icon.requestLayout();
        this.caption.setText(caption);
        return this;
    }
}

