/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   06.07.15
 */
package com.netimen.playground;

import android.graphics.Color;

public class Utils {

    public static int blendColors(int color, int bgColor) {
        float ratio = Color.alpha(color) / 255f;
        final float inverseRatio = 1f - ratio;
        float r = (Color.red(color) * ratio) + (Color.red(bgColor) * inverseRatio);
        float g = (Color.green(color) * ratio) + (Color.green(bgColor) * inverseRatio);
        float b = (Color.blue(color) * ratio) + (Color.blue(bgColor) * inverseRatio);
        return Color.rgb((int) r, (int) g, (int) b);
    }
}
