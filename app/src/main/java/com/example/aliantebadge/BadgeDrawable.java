package com.example.aliantebadge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


/**
 * Created by Admin on 2/25/2016.
 */
public class BadgeDrawable extends Drawable {

    private Paint mBadgePaint;

    private boolean mWillDraw = false;


    public BadgeDrawable(Context context) {
        mBadgePaint = new Paint();
        mBadgePaint.setColor(context.getColor(R.color.red_wrong));
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);
    }



    @Override
    public void draw(Canvas canvas) {
        if (!mWillDraw) {
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;
        // Position the badge in the top-right quadrant of the icon.

        /*Using Math.max rather than Math.min */
//        float radius = ((Math.max(width, height) / 2)) / 2;
        float radius = width * 0.15f;
        float centerX = (width - radius) + 4;
        float centerY = height - 10;
        canvas.drawCircle(centerX, centerY, radius+3, mBadgePaint);
        // Draw badge count text inside the circle.
        //mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
        //float textHeight = mTxtRect.bottom - mTxtRect.top;
        //float textY = centerY + (textHeight / 2f);
       /* if(mCount.length() > 2)
            canvas.drawText("99+", centerX, textY, mTextPaint);
        else
            canvas.drawText(mCount, centerX, textY, mTextPaint);*/
    }

    /*
     Sets the count (i.e notifications) to display.
      */
    public void setCount(String count) {

        // Only draw a badge if there are notifications.
        mWillDraw = !count.equalsIgnoreCase("0");
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        // do nothing
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // do nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}