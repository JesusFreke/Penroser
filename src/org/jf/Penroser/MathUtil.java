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
