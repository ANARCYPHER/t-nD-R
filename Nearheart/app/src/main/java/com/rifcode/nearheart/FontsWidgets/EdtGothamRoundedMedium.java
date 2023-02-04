package com.rifcode.nearheart.FontsWidgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.rifcode.nearheart.Utils.FontCache;


@SuppressLint("AppCompatCustomView")
public class EdtGothamRoundedMedium extends EditText {


    public EdtGothamRoundedMedium(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public EdtGothamRoundedMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public EdtGothamRoundedMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/GothamRoundedBook.ttf", context);
        setTypeface(customFont);
    }

}
