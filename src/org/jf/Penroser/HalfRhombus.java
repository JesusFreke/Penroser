/*
 * [The "BSD licence"]
 * Copyright (c) 2011 Ben Gruver
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.Penroser;

import android.graphics.RectF;
import static org.jf.Penroser.HalfRhombusType.LEFT;
import static org.jf.Penroser.HalfRhombusType.RIGHT;
import static org.jf.Penroser.HalfRhombusType.FAT;
import static org.jf.Penroser.HalfRhombusType.SKINNY;

import javax.microedition.khronos.opengles.GL11;

public abstract class HalfRhombus {
    public static final int LOWER_EDGE=0;
    public static final int UPPER_EDGE=1;
    public static final int INNER_EDGE=2;

    public static final int LOWER_EDGE_MASK=1;
    public static final int UPPER_EDGE_MASK=2;
    public static final int INNER_EDGE_MASK=3;

    /**
     * A relative level number. Children have a higher level, parents have a lower level. A negative level is allowed,
     * for example, in the case of upwards generation
     */
    protected int level;

    /** Whether this is the right or left side of a rhombus, and whether it is a fat or skinny rhombus */
    protected HalfRhombusType type;

    /** The x coordinate of the bottom vertex */
    protected float x;

    /** The y coordinate of the bottom vertex */
    protected float y;

    /** The scale of the half rhombus */
    protected float scale;

    /** The rotation of the half rhombus, in increments of 18 degrees */
    protected int rotation;

    /** A rect object that represents the envelope of this half rhombus */
    private RectF envelope = new RectF();
    private boolean envelopeValid = false;

    /** The vertices of this half rhombus */
    private float[] vertices = new float[6];
    private boolean verticesValid = false;

    protected PenroserContext penroserContext;

    protected HalfRhombus() {
    }

    protected HalfRhombus(PenroserContext penroserContext, int level, HalfRhombusType type, float x, float y, float scale, int rotation) {
		this.penroserContext = penroserContext;
        this.level = level;
        this.type = type;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = MathUtil.positiveMod(rotation, 20);
        envelopeValid = false;
        verticesValid = false;
    }

    public void set(PenroserContext penroserContext, int level, HalfRhombusType type, float x, float y, float scale, int rotation) {
		this.penroserContext = penroserContext;
        this.level = level;
        this.type = type;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = MathUtil.positiveMod(rotation, 20);
        envelopeValid = false;
        verticesValid = false;
    }

    public static int getLeftRight(int rhombusType) {
        return rhombusType & 1;
    }

    public static int getSkinnyFat(int rhombusType) {
        return (rhombusType & 2) >>> 1;
    }

    public static int makeRhombusType(int side, int type) {
        return side | (type << 1);
    }

    public float getRotationInDegrees() {
        return rotation * 18;
    }

    public abstract int draw(GL11 gl, RectF viewportEnvelope, int maxLevel);
    protected abstract int draw(GL11 gl, RectF viewportEnvelope, int maxLevel, boolean checkIntersect);
    public abstract HalfRhombus getChild(int i);
    protected abstract void calculateVertices(float[] vertices);
    protected abstract void calculateEnvelope(RectF envelope);
    public abstract int getRandomParentType(int edge);
    public abstract HalfRhombus getParent(int parentType);

    protected int oppositeSide() {
        return type.side==RIGHT?LEFT:RIGHT;
    }

    protected static int oppositeSide(int side) {
        return side==RIGHT?LEFT:RIGHT;
    }

    public float[] getVertices() {
        if (!verticesValid) {
            calculateVertices(vertices);
            verticesValid = true;
        }

        return vertices;
    }

    public RectF getEnvelope() {
        if (!envelopeValid) {
            calculateEnvelope(envelope);
            envelopeValid = true;
        }

        return envelope;
    }

    public boolean envelopeIntersects(RectF viewportEnvelope) {
        return RectF.intersects(getEnvelope(), viewportEnvelope);
    }

    public boolean envelopeCoveredBy(RectF viewportEnvelope) {
        return viewportEnvelope.contains(getEnvelope());
    }

    /**
     * Finds the edges of this half rhombus that intersect with the given viewport. This can be used to detect when the
     * viewport has extended past one or more of the top-most half rhombus's edges

     * @param viewport The viewport to check against
     * @return A bitmask indicating which edges intersect. The bitmask consists of the LOWER_EDGE_MASK, UPPER_EDGE_MASK
     * and INNER_EDGE_MASK values
     */
    public int getIntersectingEdges(float[] viewport) {
        assert viewport.length == 8;

        float[] triangle = getVertices();
        int edgeMask = GeometryUtil.triangleRectIntersection(triangle, viewport);
        return edgeMask;
    }
}
