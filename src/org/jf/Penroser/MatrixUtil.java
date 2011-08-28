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

import android.graphics.Matrix;

public class MatrixUtil {
    public static void convertMatrix(float[] androidMatrix, float[] glMatrix) {
        assert androidMatrix.length>=9;
        assert glMatrix.length >= 16;

        glMatrix[0] = androidMatrix[0];     glMatrix[4] = androidMatrix[1];     glMatrix[8] = 0;        glMatrix[12] = androidMatrix[2];
        glMatrix[1] = androidMatrix[3];     glMatrix[5] = androidMatrix[4];     glMatrix[9] = 0;        glMatrix[13] = androidMatrix[5];
        glMatrix[2] = 0;                    glMatrix[6] = 0;                    glMatrix[10] = 1;       glMatrix[14] = 0;
        glMatrix[3] = androidMatrix[6];     glMatrix[7] = androidMatrix[7];     glMatrix[11] = 0;       glMatrix[15] = androidMatrix[8];
    }

    private static float[] scaleSrcPoints = new float[] {0f, 0f, 0f, 1f};
    private static float[] scaleDestPoints = new float[4];
    public static float getMatrixScale(Matrix m) {
        m.mapPoints(scaleDestPoints, scaleSrcPoints);

        return (float)Math.sqrt((Math.pow(scaleDestPoints[2] - scaleDestPoints[0], 2) +
                                (Math.pow(scaleDestPoints[3] - scaleDestPoints[1], 2))));
    }
}
