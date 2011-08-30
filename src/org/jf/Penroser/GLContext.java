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

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL11;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

class GLContext {
    private int[] vertexVbos = new int[4];
    private int[] colorVbos = new int[4];

    public void generateVertexVbo(GL11 gl, int rhombusType, float[] vertices) {
        int[] vboref = new int[1];

        gl.glGenBuffers(1, vboref, 0);
        int vertexVbo = vboref[0];

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexVbo);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertices.length * 4, FloatBuffer.wrap(vertices), GL11.GL_STATIC_DRAW);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        vertexVbos[rhombusType] = vertexVbo;
    }

    public void generateColorVbo(GL11 gl, int rhombusType, int[] colors) {
        int[] vboref = new int[1];

        gl.glGenBuffers(1, vboref, 0);
        int colorVbo = vboref[0];

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, colorVbo);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, colors.length * 4, IntBuffer.wrap(colors), GL11.GL_STATIC_DRAW);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        colorVbos[rhombusType] = colorVbo;
    }

    public int getVertexVbo(HalfRhombusType rhombusType) {
        return vertexVbos[rhombusType.index];
    }

    public int getColorVbo(HalfRhombusType rhombusType) {
        return colorVbos[rhombusType.index];
    }

    /**
     * Create a new array where the values in colors are an index into replacementColors for the color to use at that
     * position
     */
    public static int[] replaceColors(int[] colors, int[] replacementColors) {
        int[] newColors = new int[colors.length];

        for (int i=0; i<colors.length; i++) {
            newColors[i] = replacementColors[colors[i]];
        }

        return newColors;
    }
}
