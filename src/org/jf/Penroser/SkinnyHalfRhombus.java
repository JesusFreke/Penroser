package org.jf.Penroser;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.nio.FloatBuffer;

public class SkinnyHalfRhombus extends HalfRhombus {
    private static final int SKINNY = 0;
    private static final int FAT = 1;

    private static final int NUM_CHILDREN = 2;
    private static final float[] leftVertices = new float[] {
            0.0f, 0.0f,
            EdgeLength.getEdgeLength(0).x(-4), EdgeLength.getEdgeLength(0).y(-4),
            0, EdgeLength.getEdgeLength(0).y(-4)*2
    };

    private static final float[] rightVertices = new float[] {
            0.0f, 0.0f,
            EdgeLength.getEdgeLength(0).x(4), EdgeLength.getEdgeLength(0).y(4),
            0, EdgeLength.getEdgeLength(0).y(4)*2
    };

    private static int leftVbo;
    private static int rightVbo;

    //light green
    private static final int leftColor = 0x9AFF9A;

    //dark green
    private static final int rightColor = 0x008900;

    public SkinnyHalfRhombus(int level, int side, float x, float y, float scale, int rotation) {
        super(level, side, x, y, scale, rotation);
    }

    @Override
    public void draw(GL11 gl, int maxLevel) {
        if (this.level < maxLevel) {
            for (int i=0; i<NUM_CHILDREN; i++) {
                HalfRhombus halfRhombus = getChild(i);
                halfRhombus.draw(gl, maxLevel);
            }
        } else {
            gl.glPushMatrix();
            gl.glTranslatef(this.x, this.y, 0);
            gl.glScalef(this.scale, this.scale, 0);
            gl.glRotatef(getRotationInDegrees(), 0, 0, -1);

            int vertexVbo;
            int color;
            if (side == LEFT) {
                vertexVbo = leftVbo;
                color = leftColor;
            } else {
                vertexVbo = rightVbo;
                color = rightColor;
            }

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexVbo);
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, 0);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glColor4ub((byte)((color >> 16) & 0xFF), (byte)((color >> 8) & 0xFF), (byte)(color & 0xFF), (byte)0xFF);

            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

            gl.glPopMatrix();
        }
    }

    @Override
    public HalfRhombus getChild(int i) {
        int sign = this.side==LEFT?1:-1;

        float newScale = scale / Constants.goldenRatio;

        EdgeLength edgeLength = EdgeLength.getEdgeLength(level);

        switch (i) {
            case SKINNY: {
                float topVerticeX = x + edgeLength.x(rotation-(sign*4)) + edgeLength.x(rotation+(sign*4));
                float topVerticeY = y + edgeLength.y(rotation-(sign*4)) + edgeLength.y(rotation+(sign*4));
                return new SkinnyHalfRhombus(level+1, side, topVerticeX, topVerticeY, newScale, rotation-(sign*6));
            }
            case FAT: {
                float sideVerticeX = x + edgeLength.x(rotation-(sign*4));
                float sideVerticeY = y + edgeLength.y(rotation-(sign*4));
                return new FatHalfRhombus(level+1, side, sideVerticeX, sideVerticeY, newScale, rotation+(sign*6));
            }
        }

        return null;
    }

    public static void onSurfaceCreated(GL11 gl) {
        int[] vboref = new int[1];
        gl.glGenBuffers(1, vboref, 0);
        leftVbo = vboref[0];

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, leftVbo);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, 6 * 4, FloatBuffer.wrap(leftVertices), GL11.GL_STATIC_DRAW);

        gl.glGenBuffers(1, vboref, 0);
        rightVbo = vboref[0];

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, rightVbo);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, 6 * 4, FloatBuffer.wrap(rightVertices), GL11.GL_STATIC_DRAW);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
    }
}
