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

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class FatHalfRhombus extends HalfRhombus {
    private static final int TOP_FAT_CHILD = 0;
    private static final int SKINNY_CHILD = 1;
    private static final int BOTTOM_FAT_CHILD = 2;

    private static final int NUM_CHILDREN = 3;

    /**
     * we just use a color placeholder when pre-generating the vertices. They will be replaced with actual colors
     * when we actually create the vbo
     */
    private static final int leftColor = 2;
    private static final int rightColor = 3;

    public FatHalfRhombus() {
    }

    public FatHalfRhombus(GLContext glContext, int level, int side, float x, float y, float scale, int rotation) {
        super(glContext, level, HalfRhombusType.getType(side, FAT), x, y, scale, rotation);
    }

    public void set(GLContext glContext, int level, int side, float x, float y, float scale, int rotation) {
        set(glContext, level, HalfRhombusType.getType(side, FAT), x, y, scale, rotation);
    }

    @Override
    protected void calculateVertices(float[] vertices) {
        EdgeLength edgeLength = EdgeLength.getEdgeLength(level);
        int sign = type.side==LEFT?1:-1;

        float sideX = x + edgeLength.x(rotation-(sign*2));
        float sideY = y + edgeLength.y(rotation-(sign*2));
        float topX = sideX + edgeLength.x(rotation+(sign*2));
        float topY = sideY + edgeLength.y(rotation+(sign*2));

        vertices[0] = x;
        vertices[1] = y;
        vertices[2] = sideX;
        vertices[3] = sideY;
        vertices[4] = topX;
        vertices[5] = topY;
    }

    @Override
    protected void calculateEnvelope(RectF envelope) {
        EdgeLength edgeLength = EdgeLength.getEdgeLength(level);
        int sign = type.side==LEFT?1:-1;

        float sideX = x + edgeLength.x(rotation-(sign*2));
        float sideY = y + edgeLength.y(rotation-(sign*2));
        float topX = sideX + edgeLength.x(rotation+(sign*2));
        float topY = sideY + edgeLength.y(rotation+(sign*2));

        float minX = Math.min(Math.min(x, sideX), topX);
        float maxX = Math.max(Math.max(x, sideX), topX);
        float minY = Math.min(Math.min(y, sideY), topY);
        float maxY = Math.max(Math.max(y, sideY), topY);

        envelope.set(minX, minY, maxX, maxY);
    }

    @Override
    public int draw(GL11 gl, RectF viewportEnvelope, int maxLevel) {
        return draw(gl, viewportEnvelope, maxLevel, true);
    }

    @Override
    protected int draw(GL11 gl, RectF viewportEnvelope, int maxLevel, boolean checkIntersect) {
        if (checkIntersect && !envelopeIntersects(viewportEnvelope)) {
            return 0;
        }

        if (checkIntersect && envelopeCoveredBy(viewportEnvelope)) {
            checkIntersect = false;
        }

        if (this.level < maxLevel) {
            int num=0;
            for (int i=0; i<NUM_CHILDREN; i++) {
                HalfRhombus halfRhombus = getChild(i);
                num += halfRhombus.draw(gl, viewportEnvelope, maxLevel, checkIntersect);
            }
            return num;
        } else {
            gl.glPushMatrix();
            gl.glTranslatef(this.x, this.y, 0);
            gl.glScalef(this.scale, this.scale, 0);
            gl.glRotatef(getRotationInDegrees(), 0, 0, -1);

            int vertexVbo;
            int colorVbo;
            int length;

            vertexVbo = glContext.getVertexVbo(type);
            colorVbo = glContext.getColorVbo(gl, type);
            length = glContext.getColorVboLength(type);

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexVbo);
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, 0);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, colorVbo);
            gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, 0);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, length);

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

            gl.glPopMatrix();

            return 1;
        }
    }

    @Override
    public HalfRhombus getChild(int i) {
        int sign = type.side==LEFT?1:-1;

        float newScale = scale / Constants.goldenRatio;

        EdgeLength edgeLength = EdgeLength.getEdgeLength(level);

        switch (i) {
            case TOP_FAT_CHILD: {
                float topVerticeX = x + edgeLength.x(rotation-(sign*2)) + edgeLength.x(rotation+(sign*2));
                float topVerticeY = y + edgeLength.y(rotation-(sign*2)) + edgeLength.y(rotation+(sign*2));
                return PenroserApp.halfRhombusPool.getFatHalfRhombus(glContext, level+1, oppositeSide(), topVerticeX, topVerticeY, newScale, rotation+10);
            }
            case SKINNY_CHILD: {
                float sideVerticeX = x + edgeLength.x(rotation-(sign*2));
                float sideVerticeY = y + edgeLength.y(rotation-(sign*2));
                return PenroserApp.halfRhombusPool.getSkinnyHalfRhombus(glContext, level + 1, oppositeSide(), sideVerticeX, sideVerticeY, newScale, rotation + (sign * 2));
            }
            case BOTTOM_FAT_CHILD: {
                float sideVerticeX = x + edgeLength.x(rotation-(sign*2));
                float sideVerticeY = y + edgeLength.y(rotation-(sign*2));
                return PenroserApp.halfRhombusPool.getFatHalfRhombus(glContext, level+1, type.side, sideVerticeX, sideVerticeY, newScale, rotation+(sign*8));
            }
        }

        return null;
    }

    @Override
    public int getRandomParentType(int edge) {
        if (edge == HalfRhombus.LOWER_EDGE) {
            return BOTTOM_FAT_CHILD;
        }
        if (edge == HalfRhombus.INNER_EDGE) {
            return PenroserApp.random.nextInt(3);
        }
        return PenroserApp.random.nextInt(2);
    }

    @Override
    public HalfRhombus getParent(int parentType) {
        float newScale = scale * Constants.goldenRatio;
        int sign = type.side==LEFT?1:-1;

        EdgeLength edgeLength = EdgeLength.getEdgeLength(level-1);

        switch (parentType) {
            case TOP_FAT_CHILD: {
                float parentSideX = x + edgeLength.x(rotation-(sign*2));
                float parentSideY = y + edgeLength.y(rotation-(sign*2));
                float parentBottomX = parentSideX + edgeLength.x(rotation+(sign*2));
                float parentBottomY = parentSideY + edgeLength.y(rotation+(sign*2));
                return new FatHalfRhombus(glContext, level-1, oppositeSide(), parentBottomX, parentBottomY, newScale, rotation+10);
            }
            case SKINNY_CHILD: {
                float parentBottomX = x + edgeLength.x(rotation);
                float parentBottomY = y + edgeLength.y(rotation);
                return new SkinnyHalfRhombus(glContext, level-1, type.side, parentBottomX, parentBottomY, newScale, rotation-(sign*6));
            }
            case BOTTOM_FAT_CHILD: {
                float parentBottomX = x + edgeLength.x(rotation);
                float parentBottomY = y + edgeLength.y(rotation);
                return new FatHalfRhombus(glContext, level-1, type.side, parentBottomX, parentBottomY, newScale, rotation-(sign*8));
            }
        }
        return null;
    }

    public static void generateVertices(int level, int side, float[][] vertices, int[][] colors) {
        assert vertices != null && vertices.length > 0;
        assert colors != null && colors.length > 0;

        int fat=1;
        int skinny=0;

        for (int i=0; i<level; i++) {
            int _fat = fat * 2 + skinny;
            int _skinny = fat+skinny;
            fat = _fat;
            skinny = _skinny;
        }

        int count = fat+skinny;
        vertices[0] = new float[count*3*2];
        colors[0] = new int[count*3];

        generateVertices(vertices[0], colors[0], 0, 0, level, 0, side, 0, 0);
    }

    //TODO: I'm curious whether using final for these args will cause javac to optimize the many instances of level+1, rotation+n, etc.
    protected static int generateVertices(final float[] vertices, final int[] colors, int index, final int level, final int maxLevel, final int rotation, final int side, final float x, final float y) {
        //x,y are the coordinates of the bottom vertex of the rhombus

        EdgeLength edgeLength = EdgeLength.getEdgeLength(level);

        float sideVertexX, sideVertexY;
        float topVertexX, topVertexY;

        int sign = side==LEFT?1:-1;

        sideVertexX = x + edgeLength.x(rotation-(sign*2));
        sideVertexY = y + edgeLength.y(rotation-(sign*2));

        topVertexX = sideVertexX + edgeLength.x(rotation+(sign*2));
        topVertexY = sideVertexY + edgeLength.y(rotation+(sign*2));

        if (level < maxLevel) {
            //top fat rhombus
            index = FatHalfRhombus.generateVertices(vertices, colors, index, level+1, maxLevel, rotation+10, oppositeSide(side), topVertexX, topVertexY);

            //left skinny rhombus
            index = SkinnyHalfRhombus.generateVertices(vertices, colors, index, level+1, maxLevel, rotation+(sign*2), oppositeSide(side), sideVertexX, sideVertexY);

            //left fat rhombus
            return FatHalfRhombus.generateVertices(vertices, colors, index, level+1, maxLevel, rotation+(sign*8), side, sideVertexX, sideVertexY);
        } else {
            int color = side==LEFT?leftColor:rightColor;

            colors[index>>1] = color;

            vertices[index++] = x;
            vertices[index++] = y;

            colors[index>>1] = color;

            vertices[index++] = sideVertexX;
            vertices[index++] = sideVertexY;

            colors[index>>1] = color;

            vertices[index++] = topVertexX;
            vertices[index++] = topVertexY;

            return index;
        }
    }
}
