package org.jf.Penroser;

public class MathUtil {
    public static int positiveMod(int num, int mod) {
        int result = num%mod;
        if (result<0) {
            return result+mod;
        }
        return result;
    }
}
