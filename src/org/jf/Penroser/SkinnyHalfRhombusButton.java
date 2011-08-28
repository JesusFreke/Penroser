package org.jf.Penroser;

import android.content.Context;
import android.util.AttributeSet;

public class SkinnyHalfRhombusButton extends HalfRhombusButton {

    //The ratio of height/width of a skinny half rhombus
    private static final float aspectRatio = (float)(2*Math.sin(18 * 2 * Math.PI/360)/Math.cos(18 * 2 * Math.PI/360));
    protected float getAspectRatio() {
        return aspectRatio;
    }

    public SkinnyHalfRhombusButton(Context context) {
        this(context, LEFT, false);
    }

    public SkinnyHalfRhombusButton(Context context, int side, boolean rotated) {
        super(context, side, rotated);
    }

    public SkinnyHalfRhombusButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SkinnyHalfRhombusButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
