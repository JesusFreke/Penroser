package org.jf.Penroser;

public class EdgeLength {
    private static float[][] xlengths;
    private static float[][] ylengths;

    static {
        initToLevel(0);
    }

    private static void initToLevel(int level) {
        int startLevel = 0;
        float[][] newXlengths = new float[level+1][20];
        float[][] newYlengths = new float[level+1][20];

        if (xlengths != null) {
            for (int i=0; i<xlengths.length; i++) {
                for (int j=0; j<20; j++) {
                    newXlengths[i][j] = xlengths[i][j];
                    newYlengths[i][j] = ylengths[i][j];
                }
            }

            startLevel = xlengths.length;
        } else {
            for (int i=0; i<20; i++) {
                newXlengths[0][i] = (float)Math.sin(i*Math.PI/10);
                newYlengths[0][i] = (float)Math.cos(i*Math.PI/10);
            }

            startLevel = 1;
        }

        for (int i=startLevel; i<=level; i++) {
            for (int j=0; j<20; j++) {
                newXlengths[i][j] = newXlengths[i-1][j] * Constants.goldenRatio;
                newYlengths[i][j] = newYlengths[i-1][j] * Constants.goldenRatio;
            }
        }

        xlengths=newXlengths;
        ylengths=newYlengths;
    }

    private static int mod20(int num) {
        int result = num%20;
        if (result<0) {
            return result+20;
        }
        return result;
    }

    public static float x(int level, int rotation) {
        if (level >= xlengths.length) {
            initToLevel(level);
        }
        return xlengths[level][mod20(rotation)];
    }

    public static float y(int level, int rotation) {
        if (level >= ylengths.length) {
            initToLevel(level);
        }
        return ylengths[level][mod20(rotation)];
    }
}
