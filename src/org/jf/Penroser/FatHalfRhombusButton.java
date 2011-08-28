package org.jf.Penroser;

import android.content.Context;
import android.util.AttributeSet;

public class FatHalfRhombusButton extends HalfRhombusButton {

    //The ratio of height/width of a fat half rhombus
    private static final float aspectRatio = (float)(2*Math.sin(54 * 2 * Math.PI/360)/Math.cos(54 * 2 * Math.PI/360));
    @Override
    protected float getAspectRatio() {
        return aspectRatio;
    }

    public FatHalfRhombusButton(Context context) {
        this(context, LEFT, false);
    }

    public FatHalfRhombusButton(Context context, int side, boolean rotated) {
        super(context, side, rotated);
    }

    public FatHalfRhombusButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FatHalfRhombusButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
