package com.rifcode.nearheart.FontsWidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import com.rifcode.nearheart.R;

/**
 * Created by ibra_ on 22/02/2018.
 */

public class TvGothamRoundedBold extends androidx.appcompat.widget.AppCompatTextView  {


    AttributeSet attr;

    public TvGothamRoundedBold(Context context) {
        super(context);
        setCustomFont(context, attr);
    }

    public TvGothamRoundedBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public TvGothamRoundedBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        String customFont = null;
        TypedArray a = null;
        if (attrs != null) {
            a = ctx.obtainStyledAttributes(attrs, R.styleable.TvGothamRoundedBold);
            customFont = a.getString(R.styleable.TvGothamRoundedBold_customFontGothamRoundedBold);
        }
        if (customFont == null)
            customFont = "fonts/GothamRoundedBold.ttf";
        setCustomFont(ctx, customFont);
        if (a != null) {
            a.recycle();
        }
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e("textView", "Could not get typeface", e);
            return false;
        }
        setTypeface(tf);
        return true;
    }
}
