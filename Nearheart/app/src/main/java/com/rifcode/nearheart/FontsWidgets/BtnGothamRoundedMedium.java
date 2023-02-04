package com.rifcode.nearheart.FontsWidgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.rifcode.nearheart.Utils.FontCache;


@SuppressLint("AppCompatCustomView")
public class BtnGothamRoundedMedium extends Button {


    public BtnGothamRoundedMedium(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public BtnGothamRoundedMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public BtnGothamRoundedMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/GothamRoundedMedium.ttf", context);
        setTypeface(customFont);
    }

}
