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

import android.content.SharedPreferences;

import javax.microedition.khronos.opengles.GL11;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.jf.Penroser.HalfRhombusType.LEFT;
import static org.jf.Penroser.HalfRhombusType.RIGHT;
import static org.jf.Penroser.HalfRhombusType.FAT;
import static org.jf.Penroser.HalfRhombusType.SKINNY;

class PenroserContext {
    /*This is the number of levels to use for the static vbo data*/
    /*package*/ static final int VBO_LEVEL=5;

    private final float[][] vertices = new float[4][];
    private final HalfRhombusType[][] rhombusTypes = new HalfRhombusType[4][];

    private int[] vertexVbos = new int[4];
    private int[] colorVbos = new int[4];

    //This is set when the colors are dynamically changed, so that the vbos will be regenerated with the new colors
    private boolean recreateColorVbos = false;

    private PenroserPreferences preferences = new PenroserPreferences();

    public PenroserContext() {
        float[][] vertices = new float[1][];
        HalfRhombusType[][] rhombusTypes = new HalfRhombusType[1][];

        SkinnyHalfRhombus.generateVertices(VBO_LEVEL, LEFT, vertices, rhombusTypes);
        this.vertices[0] = vertices[0];
        this.rhombusTypes[0] = rhombusTypes[0];

        SkinnyHalfRhombus.generateVertices(VBO_LEVEL, RIGHT, vertices, rhombusTypes);
        this.vertices[1] = vertices[0];
        this.rhombusTypes[1] = rhombusTypes[0];

        FatHalfRhombus.generateVertices(VBO_LEVEL, LEFT, vertices, rhombusTypes);
        this.vertices[2] = vertices[0];
        this.rhombusTypes[2] = rhombusTypes[0];

        FatHalfRhombus.generateVertices(VBO_LEVEL, RIGHT, vertices, rhombusTypes);
        this.vertices[3] = vertices[0];
        this.rhombusTypes[3] = rhombusTypes[0];
    }

    private static int generateVertexVbo(GL11 gl, int rhombusType, float[] vertices) {
        int[] vboref = new int[1];

        gl.glGenBuffers(1, vboref, 0);
        int vertexVbo = vboref[0];

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexVbo);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertices.length * 4, FloatBuffer.wrap(vertices), GL11.GL_STATIC_DRAW);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        return vertexVbo;
    }

    private static int generateColorVbo(GL11 gl, int[] colors) {
        int[] vboref = new int[1];

        gl.glGenBuffers(1, vboref, 0);
        int colorVbo = vboref[0];

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, colorVbo);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, colors.length * 4, IntBuffer.wrap(colors), GL11.GL_STATIC_DRAW);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        return colorVbo;
    }

    public void setPreferences(PenroserPreferences preferences) {
        this.preferences.setPreferences(preferences);
        this.recreateColorVbos = true;
    }

    public void saveTo(SharedPreferences sharedPreferences, String sharedPreferenceName) {
        this.preferences.saveTo(sharedPreferences, sharedPreferenceName);
    }

    public void onSurfaceCreated(GL11 gl) {
        for (int i=0; i<4; i++) {
            vertexVbos[i] = generateVertexVbo(gl, i, vertices[i]);
            colorVbos[i] = generateColorVbo(gl, generateGLColorArray(rhombusTypes[i]));
        }
        recreateColorVbos = false;
    }

    public int getVertexVbo(HalfRhombusType rhombusType) {
        return vertexVbos[rhombusType.index];
    }

    public int getColorVbo(GL11 gl, HalfRhombusType rhombusType) {
        if (recreateColorVbos) {
            gl.glDeleteBuffers(4, colorVbos, 0);
            for (int i=0; i<4; i++) {
                colorVbos[i] = generateColorVbo(gl, generateGLColorArray(rhombusTypes[i]));
            }
            recreateColorVbos = false;
        }
        return colorVbos[rhombusType.index];
    }

    public int getColorVboLength(HalfRhombusType rhombusType) {
        return rhombusTypes[rhombusType.index].length*3;
    }

    /**
     * Create a new array where each element is the current color of the corresponding rhombus type in rhombusTypes.
     * The colors in the array are also swapped so that they can be bound directly to a GL color buffer
     */
    private int[] generateGLColorArray(HalfRhombusType[] rhombusTypes) {
        int[] colors = new int[rhombusTypes.length*3];

        for (int i=0; i<rhombusTypes.length; i++) {
            int colorIndex = i*3;
            int color = ColorUtil.swapOrder(preferences.getColor(rhombusTypes[i]));

            colors[colorIndex] = colors[colorIndex+1] = colors[colorIndex+2] = color;
        }

        return colors;
    }
}
