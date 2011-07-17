package org.jf.Penroser;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import javax.microedition.khronos.opengles.GL11;

public abstract class HalfRhombus {
    public static final int LEFT=0;
    public static final int RIGHT=1;

    public static final int LOWER_EDGE=0;
    public static final int UPPER_EDGE=1;
    public static final int INNER_EDGE=2;

    /*This is the number of levels to use for the static vbo data*/
    /*package*/ static final int VBO_LEVEL=6;

    /**
     * A relative level number. Children have a higher level, parents have a lower level. A negative level is allowed,
     * for example, in the case of upwards generation
     */
    protected int level;

    /** Whether this is the right or left side of a rhombus */
    protected int side;

    /** The x coordinate of the bottom vertex */
    protected float x;

    /** The y coordinate of the bottom vertex */
    protected float y;

    /** The scale of the half rhombus */
    protected float scale;

    /** The rotation of the half rhombus, in increments of 18 degrees */
    protected int rotation;

    /** A Geometry object that represents this half rhombus */
    private Geometry geometry;

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

    public abstract int draw(GL11 gl, Geometry viewport, int maxLevel);
    protected abstract int draw(GL11 gl, Geometry viewport, int maxLevel, boolean checkIntersect);
    public abstract HalfRhombus getChild(int i);
    protected abstract Geometry createGeometry();
    public abstract int getRandomParentType(int edge);
    public abstract HalfRhombus getParent(int parentType);

    protected int oppositeSide() {
        return side==RIGHT?LEFT:RIGHT;
    }

    protected static int oppositeSide(int side) {
        return side==RIGHT?LEFT:RIGHT;
    }

    public Geometry getGeometry() {
        if (geometry == null) {
            geometry = createGeometry();
        }
        return geometry;
    }

    public boolean envelopeIntersects(Envelope viewportEnvelope) {
        return getGeometry().getEnvelopeInternal().intersects(viewportEnvelope);
    }

    public boolean envelopeCoveredBy(Envelope viewportEnvelope) {
        return viewportEnvelope.covers(getGeometry().getEnvelopeInternal());
    }
}
