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

public class MathUtil {
    public static int positiveMod(int num, int mod) {
        int result = num%mod;
        if (result<0) {
            return result+mod;
        }
        return result;
    }

    public static float max(float f1, float f2, float f3) {
        float max = f1;
        if (f2 > max)
            max = f2;
        if (f3 > max)
            max = f3;
        return max;
    }

    public static float max(float f1, float f2, float f3, float f4) {
        float max = f1;
        if (f2 > max)
            max = f2;
        if (f3 > max)
            max = f3;
        if (f4 > max)
            max = f4;
        return max;
    }

    public static float min(float f1, float f2, float f3) {
        float min = f1;
        if (f2 < min)
            min = f2;
        if (f3 < min)
            min = f3;
        return min;
    }

    public static float min(float f1, float f2, float f3, float f4) {
        float min = f1;
        if (f2 < min)
            min = f2;
        if (f3 < min)
            min = f3;
        if (f4 < min)
            min = f4;
        return min;
    }
}
