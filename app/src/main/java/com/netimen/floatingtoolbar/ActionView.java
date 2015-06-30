/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   30.06.15
 */
package com.netimen.floatingtoolbar;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.action_view)
public class ActionView extends LinearLayout {
    @ViewById
    TextView icon, caption;

    public ActionView(Context context) {
        super(context);
    }

    public void bind(String icon, String caption) {
        this.icon.setText(icon);
        this.caption.setText(caption);
    }
}
