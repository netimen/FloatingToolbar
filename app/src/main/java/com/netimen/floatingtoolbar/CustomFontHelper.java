/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   02.07.15
 */
package com.netimen.floatingtoolbar;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomFontHelper { // http://bluejamesbond.github.io/CharacterMap/

    @SuppressWarnings("SameParameterValue")
    public static Spannable getCustomFontText(Context context, CharSequence text) {
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "selection_icons.ttf");
        spannable.setSpan(new CustomTypefaceSpan("", tf), 0, spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static class CustomTypefaceSpan extends TypefaceSpan { // see http://stackoverflow.com/a/4826885/190148
        private final Typeface newType;

        @SuppressWarnings("SameParameterValue")
        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(@NonNull TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private static void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            oldStyle = old == null ? 0 : old.getStyle();

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(tf);
        }
    }
}
