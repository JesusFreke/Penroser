package org.jf.Penroser;

public class ColorUtil {
    public static int glToAndroid(int color) {
        int red = color & 0xFF;
        int green = (color & 0xFF00) >>> 8;
        int blue = (color & 0xFF0000) >>> 16;
        return blue | (green << 8) | (red << 16);
    }
}
