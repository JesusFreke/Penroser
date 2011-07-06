package org.jf.Penroser;


import javax.microedition.khronos.opengles.GL11;

public abstract class HalfRhombus {
    public static final int LEFT=0;
    public static final int RIGHT=1;

    /**
     * Number of level down the hierarchy. 0 is the top-most parent, 1 are the top-most parent's children, etc.
     */
    protected int level;
    protected int side;
    protected float x;
    protected float y;
    protected float scale;
    protected int rotation;

    protected HalfRhombus(int level, int side, float x, float y, float scale, int rotation) {
        this.level = level;
        this.side = side;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = EdgeLength.mod20(rotation);
    }

    public float getRotationInDegrees() {
        return rotation * 18;
    }

    public abstract void draw(GL11 gl, int maxLevel);
    public abstract HalfRhombus getChild(int i);

    protected int oppositeSide() {
        return side==RIGHT?LEFT:RIGHT;
    }
}
