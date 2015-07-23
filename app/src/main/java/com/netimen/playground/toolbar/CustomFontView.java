/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   02.07.15
 */
package com.netimen.playground.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontView extends TextView {
    public CustomFontView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(CustomFontHelper.getCustomFontText(getContext(), text), type);
    }
}
