package com.rifcode.nearheart.FontsWidgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.rifcode.nearheart.Utils.FontCache;


@SuppressLint("AppCompatCustomView")
public class CbGothamRoundedMedium extends CheckBox {


    public CbGothamRoundedMedium(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CbGothamRoundedMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CbGothamRoundedMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/GothamRoundedMedium.ttf", context);
        setTypeface(customFont);
    }

}
