package org.jf.Penroser;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public abstract class HalfRhombusButton extends Button {
    private static final String TAG = "HalfRhombusButton";

    private static final boolean DEBUG_MEASURE = true;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    private final int side;
    private final boolean rotated;

    public HalfRhombusButton(Context context, int side, boolean rotated) {
        super(context);
        this.side = side;
        this.rotated = rotated;
    }

    public HalfRhombusButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.style.Widget);
    }

    public HalfRhombusButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HalfRhombusButton);
        side = typedArray.getInt(R.styleable.HalfRhombusButton_side, 0);
        rotated = typedArray.getBoolean(R.styleable.HalfRhombusButton_rotated, false);
    }

    //The ratio of height/width of this half rhombus
    protected abstract float getAspectRatio();

    private int getHeightGivenWidth(int width) {
        if (rotated) {
            return (int)Math.ceil(width/getAspectRatio());
        }
        return (int)Math.ceil(width*getAspectRatio());
    }

    private int getWidthGivenHeight(int height) {
        if (rotated) {
            return (int)Math.ceil(height*getAspectRatio());
        }
        return (int)Math.ceil(height/getAspectRatio());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float viewRatio;

        if (rotated) {
            viewRatio = getWidth()/(float)getHeight();
        } else {
            viewRatio = getHeight()/(float)getWidth();
        }

        int height, width;
        int horizontalOffset, verticalOffset;

        if ((viewRatio < getAspectRatio()) ^ rotated) {
            //we are constrained by the height of the view
            height = getHeight();
            width = getWidthGivenHeight(height);

            if (rotated) {
                horizontalOffset = (getWidth()-width)/2;
            } else {
                horizontalOffset = getWidth()-width;
            }
            verticalOffset = 0;
        } else {
            //we are constrained by the width of the view
            width = getWidth();
            height = getHeightGivenWidth(width);

            horizontalOffset = 0;
            if (rotated) {
                verticalOffset = getHeight()-height;
            } else {
                verticalOffset = (getHeight()-height)/2;
            }
        }

        Path path = new Path();

        if (side == LEFT) {
            if (rotated) {
                //we're actually drawing the top element
                path.moveTo(0+horizontalOffset, verticalOffset + height);
                path.lineTo(horizontalOffset + (width/2), verticalOffset);
                path.lineTo(horizontalOffset+width, verticalOffset + height);
                path.close();
            } else {
                path.moveTo(0+horizontalOffset, verticalOffset + (height/2));
                path.lineTo(width, verticalOffset);
                path.lineTo(width, verticalOffset+height);
                path.close();
            }
        } else {
            if (rotated) {
                //we're actually drawing the bottom element
                path.moveTo(0+horizontalOffset, 0);
                path.lineTo(horizontalOffset + (width / 2), height);
                path.lineTo(horizontalOffset + width, 0);
                path.close();
            } else {
                path.moveTo(0, verticalOffset);
                path.lineTo(0, verticalOffset + height);
                path.lineTo(width, verticalOffset + (height/2));
                path.close();
            }
        }

        Paint fillPaint = new Paint();
        fillPaint.setColor(Color.argb(255, 255, 0, 255));
        fillPaint.setStyle(Paint.Style.FILL);

        Paint outlinePaint = new Paint();
        outlinePaint.setColor(Color.argb(255, 128, 128, 128));
        outlinePaint.setStrokeWidth(2);
        outlinePaint.setStyle(Paint.Style.STROKE);

        canvas.drawPath(path, fillPaint);
        canvas.drawPath(path, outlinePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean widthCanShrink=true, heightCanShrink=true;
        int width=0, height=0;

        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
                widthCanShrink = true;
                break;
            case MeasureSpec.AT_MOST:
                width = MeasureSpec.getSize(widthMeasureSpec);
                widthCanShrink = true;
                break;
            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);
                widthCanShrink = false;
                break;
        }

        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
                heightCanShrink = true;
                break;
            case MeasureSpec.AT_MOST:
                height = MeasureSpec.getSize(heightMeasureSpec);
                heightCanShrink = true;
                break;
            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                heightCanShrink = false;
                break;
        }

        if (!widthCanShrink) {
            if (heightCanShrink) {
                int newHeight = getHeightGivenWidth(width);
                height = Math.min(newHeight, height);
            }
        } else {
            if (heightCanShrink) {
                width = 0;
                height = 0;
            } else {
                int newWidth = getWidthGivenHeight(height);
                width = Math.min(width, newWidth);
            }
        }

        if (DEBUG_MEASURE) {
            Log.v(this.getClass().getSimpleName(), "OnMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", " +
                    MeasureSpec.toString(heightMeasureSpec) + ") = " + width + ", " + height);
        }

        setMeasuredDimension(width, height);
    }
}
