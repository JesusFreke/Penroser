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

public enum HalfRhombusType {
    LEFT_SKINNY(0, 0, 0, "left_skinny_color", R.id.left_skinny),
    RIGHT_SKINNY(1, 0, 0x7296d1, "right_skinny_color", R.id.right_skinny),
    LEFT_FAT(0, 1, 0x7296d1, "left_fat_color", R.id.left_fat),
    RIGHT_FAT(1, 1, 0, "right_fat_color", R.id.right_fat);

    //allocate a static array of all types so we can avoid calling the values() method on the enum (which does an array allocation)
    private static final HalfRhombusType[] types = new HalfRhombusType[] {LEFT_SKINNY, RIGHT_SKINNY, LEFT_FAT, RIGHT_FAT};

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    public static final int SKINNY = 0;
    public static final int FAT = 1;

    public final int side;
    public final int type;
    public final int index;
    public final int defaultColor;
    public final String colorKey;
    public final float aspectRatio;
    public final int viewId;

    private HalfRhombusType(int side, int type, int defaultColor, String colorKey, int viewId) {
        assert side==LEFT || side==RIGHT;
        assert type==SKINNY || type == FAT;

        this.side = side;
        this.type = type;
        this.index = side | (type<<1);
        this.defaultColor = defaultColor;
        this.colorKey = colorKey;
        this.aspectRatio = getAspectRatio(type);
        this.viewId = viewId;
    }

    private static float getAspectRatio(int type) {
        if (type == SKINNY) {
            return (float)(2*Math.sin(18 * 2 * Math.PI/360)/Math.cos(18 * 2 * Math.PI/360));
        } else {
            return (float)(2*Math.sin(54 * 2 * Math.PI/360)/Math.cos(54 * 2 * Math.PI/360));
        }
    }

    public static HalfRhombusType fromTypeAndSide(int type, int side) {
        return types[side | (type<<1)];
    }

    public static HalfRhombusType fromIndex(int index) {
        return types[index];
    }
}
