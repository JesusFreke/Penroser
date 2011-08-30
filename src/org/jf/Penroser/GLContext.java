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

import javax.microedition.khronos.opengles.GL11;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.jf.Penroser.HalfRhombusType.LEFT;
import static org.jf.Penroser.HalfRhombusType.RIGHT;
import static org.jf.Penroser.HalfRhombusType.FAT;
import static org.jf.Penroser.HalfRhombusType.SKINNY;

class GLContext {
    /*This is the number of levels to use for the static vbo data*/
    /*package*/ static final int VBO_LEVEL=5;

    private final float[][] vertices = new float[4][];
    private final int[][] colors = new int[4][];

    private int[] rhombusColors = new int[4];

    private int[] vertexVbos = new int[4];
    private int[] colorVbos = new int[4];

    //This is set when the colors are dynamically changed, so that the vbos will be regenerated with the new colors
    private boolean recreateColorVbos = false;

    public GLContext(int[] rhombusColors) {
        float[][] vertices = new float[1][];
        int[][] colors = new int[1][];

        SkinnyHalfRhombus.generateVertices(VBO_LEVEL, LEFT, vertices, colors);
        this.vertices[0] = vertices[0];
        this.colors[0] = colors[0];

        SkinnyHalfRhombus.generateVertices(VBO_LEVEL, RIGHT, vertices, colors);
        this.vertices[1] = vertices[0];
        this.colors[1] = colors[0];

        FatHalfRhombus.generateVertices(VBO_LEVEL, LEFT, vertices, colors);
        this.vertices[2] = vertices[0];
        this.colors[2] = colors[0];

        FatHalfRhombus.generateVertices(VBO_LEVEL, RIGHT, vertices, colors);
        this.colors[3] = colors[0];
        this.vertices[3] = vertices[0];

        assert rhombusColors != null && rhombusColors.length == 4;
        this.rhombusColors = rhombusColors;
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

    private static int generateColorVbo(GL11 gl, int rhombusType, int[] colors) {
        int[] vboref = new int[1];

        gl.glGenBuffers(1, vboref, 0);
        int colorVbo = vboref[0];

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, colorVbo);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, colors.length * 4, IntBuffer.wrap(colors), GL11.GL_STATIC_DRAW);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        return colorVbo;
    }

    public void setRhombusColors(int[] rhombusColors) {
        this.rhombusColors = rhombusColors;
        this.recreateColorVbos = true;
    }

    public void onSurfaceCreated(GL11 gl) {
        for (int i=0; i<4; i++) {
            vertexVbos[i] = generateVertexVbo(gl, i, vertices[i]);
            colorVbos[i] = generateColorVbo(gl, i, replaceColors(colors[i], rhombusColors));
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
                colorVbos[i] = generateColorVbo(gl, i, replaceColors(colors[i], rhombusColors));
            }
            recreateColorVbos = false;
        }
        return colorVbos[rhombusType.index];
    }

    public int getColorVboLength(HalfRhombusType rhombusType) {
        return colors[rhombusType.index].length;
    }

    /**
     * Create a new array where the values in colors are an index into rhombusColors for the color to use at that
     * position
     */
    private static int[] replaceColors(int[] colors, int[] rhombusColors) {
        int[] newColors = new int[colors.length];

        for (int i=0; i<colors.length; i++) {
            newColors[i] = rhombusColors[colors[i]];
        }

        return newColors;
    }
}
