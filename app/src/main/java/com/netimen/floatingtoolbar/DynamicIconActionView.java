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

    public View bind(DynamicIconView.IconRenderer iconRenderer, String caption) {
        icon.setRenderer(iconRenderer);
        this.caption.setText(caption);
        return this;
    }

    DynamicIconView.IconRenderer getIconRenderer() { // CUR remove?
        return icon.renderer;
    }
}
