/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   30.06.15
 */
package com.netimen.playground.toolbar;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netimen.playground.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.selection_toolbar_action_view)
public class ActionView extends RelativeLayout {
    @ViewById
    TextView icon, caption;

    public ActionView(Context context) {
        super(context);
    }

    public View bind(CharSequence icon, String caption) {
        this.icon.setText(icon);
        this.caption.setText(caption);
        return this;
    }
}
