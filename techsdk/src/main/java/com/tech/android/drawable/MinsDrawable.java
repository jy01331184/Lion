package com.tech.android.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by tianyang on 16/12/21.
 */
public class MinsDrawable extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public MinsDrawable() {
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.parseColor("#F79709"));
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bs = getBounds();
        canvas.drawLine(bs.left,(bs.top+bs.bottom)/2,bs.right,(bs.top+bs.bottom)/2,mPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
