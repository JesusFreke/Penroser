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

    public FatHalfRhombus getFatHalfRhombus(int level, int side, float x, float y, float scale, int rotation) {
        FatHalfRhombus rhombus;
        if (level < 0) {
            rhombus = negFatHalfRhombi.get(-level);
        } else {
            rhombus = posFatHalfRhombi.get(level);
        }
        rhombus.set(level, side, x, y, scale, rotation);
        return rhombus;
    }

    public SkinnyHalfRhombus getSkinnyHalfRhombus(int level, int side, float x, float y, float scale, int rotation) {
        SkinnyHalfRhombus rhombus;
        if (level < 0) {
            rhombus = negSkinnyHalfRhombi.get(-level);
        } else {
            rhombus = posSkinnyHalfRhombi.get(level);
        }
        rhombus.set(level, side, x, y, scale, rotation);
        return rhombus;
    }
}
