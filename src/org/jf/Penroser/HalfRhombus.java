package org.jf.Penroser;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import javax.microedition.khronos.opengles.GL11;

public abstract class HalfRhombus {
    public static final int LEFT=0;
    public static final int RIGHT=1;

    /*This is the number of levels to use for the static vbo data*/
    /*package*/ static final int VBO_LEVEL=6;

    /**
     * Number of level down the hierarchy. 0 is the top-most parent, 1 are the top-most parent's children, etc.
     */
    protected int level;
    protected int side;
    protected float x;
    protected float y;
    protected float scale;
    protected int rotation;
    protected Geometry geometry;

    protected HalfRhombus(int level, int side, float x, float y, float scale, int rotation) {
        this.level = level;
        this.side = side;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = EdgeLength.mod20(rotation);
        this.geometry = createGeometry();
    }

    public float getRotationInDegrees() {
        return rotation * 18;
    }

    public abstract int draw(GL11 gl, Geometry viewport, int maxLevel);
    protected abstract int draw(GL11 gl, Geometry viewport, int maxLevel, boolean checkIntersect);
    public abstract HalfRhombus getChild(int i);
    protected abstract Geometry createGeometry();

    protected int oppositeSide() {
        return side==RIGHT?LEFT:RIGHT;
    }

    protected static int oppositeSide(int side) {
        return side==RIGHT?LEFT:RIGHT;
    }

    public boolean envelopeIntersects(Envelope viewportEnvelope) {
        return geometry.getEnvelopeInternal().intersects(viewportEnvelope);
    }

    public boolean envelopeCoveredBy(Envelope viewportEnvelope) {
        return viewportEnvelope.covers(geometry.getEnvelopeInternal());
    }
}
