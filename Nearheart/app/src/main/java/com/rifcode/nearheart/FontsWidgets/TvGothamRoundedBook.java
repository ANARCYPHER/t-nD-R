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

public class TvGothamRoundedBook extends androidx.appcompat.widget.AppCompatTextView  {


    AttributeSet attr;

    public TvGothamRoundedBook(Context context) {
        super(context);
        setCustomFont(context, attr);
    }

    public TvGothamRoundedBook(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public TvGothamRoundedBook(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        String customFont = null;
        TypedArray a = null;
        if (attrs != null) {
            a = ctx.obtainStyledAttributes(attrs, R.styleable.GothamRoundedBook);
            customFont = a.getString(R.styleable.GothamRoundedBook_customFontGothamRoundedBook);
        }
        if (customFont == null)
            customFont = "fonts/GothamRoundedBook.ttf";
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
