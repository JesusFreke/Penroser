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

import java.util.ArrayList;

public class HalfRhombusPool {
    private ArrayList<SkinnyHalfRhombus> negSkinnyHalfRhombi = new ArrayList<SkinnyHalfRhombus>();
    private ArrayList<SkinnyHalfRhombus> posSkinnyHalfRhombi = new ArrayList<SkinnyHalfRhombus>();

    private ArrayList<FatHalfRhombus> negFatHalfRhombi = new ArrayList<FatHalfRhombus>();
    private ArrayList<FatHalfRhombus> posFatHalfRhombi = new ArrayList<FatHalfRhombus>();

    public void initToLevels(int negLevel, int posLevel) {
        for (int i=negSkinnyHalfRhombi.size(); i<=-negLevel; i++) {
            negSkinnyHalfRhombi.add(new SkinnyHalfRhombus());
        }

        for (int i=posSkinnyHalfRhombi.size(); i<=posLevel; i++) {
            posSkinnyHalfRhombi.add(new SkinnyHalfRhombus());
        }

        for (int i=negFatHalfRhombi.size(); i<=-negLevel; i++) {
            negFatHalfRhombi.add(new FatHalfRhombus());
        }

        for (int i=posFatHalfRhombi.size(); i<=posLevel; i++) {
            posFatHalfRhombi.add(new FatHalfRhombus());
        }
    }

    public FatHalfRhombus getFatHalfRhombus(PenroserContext penroserContext, int level, int side, float x, float y, float scale, int rotation) {
        FatHalfRhombus rhombus;
        if (level < 0) {
            rhombus = negFatHalfRhombi.get(-level);
        } else {
            rhombus = posFatHalfRhombi.get(level);
        }
        rhombus.set(penroserContext, level, side, x, y, scale, rotation);
        return rhombus;
    }

    public SkinnyHalfRhombus getSkinnyHalfRhombus(PenroserContext penroserContext, int level, int side, float x, float y, float scale, int rotation) {
        SkinnyHalfRhombus rhombus;
        if (level < 0) {
            rhombus = negSkinnyHalfRhombi.get(-level);
        } else {
            rhombus = posSkinnyHalfRhombi.get(level);
        }
        rhombus.set(penroserContext, level, side, x, y, scale, rotation);
        return rhombus;
    }
}
