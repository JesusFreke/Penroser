package org.jf.Penroser;

import java.util.ArrayList;
import java.util.List;

public class EdgeLength {
    private static List<EdgeLength> edgeLengths_pos = new ArrayList<EdgeLength>();
    private static List<EdgeLength> edgeLengths_neg = new ArrayList<EdgeLength>();

    private float[] xLengths;
    private float[] yLengths;

    private EdgeLength(float scale) {
        xLengths = new float[20];
        yLengths = new float[20];
        for (int i=0; i<20; i++) {
            xLengths[i] = (float)(scale * Math.sin(i*Math.PI/10));
            yLengths[i] = (float)(scale * Math.cos(i*Math.PI/10));
        }
    }

    private static void initToLevel(int level) {
        if (level < 0) {
            for (int i=edgeLengths_neg.size(); i<=-level; i++) {
                edgeLengths_neg.add(new EdgeLength((float)Math.pow(Constants.goldenRatio, i)));
            }
        } else {
            for (int i=edgeLengths_pos.size(); i<=level; i++) {
                edgeLengths_pos.add(new EdgeLength((float)Math.pow(Constants.goldenRatio, -i)));
            }
        }
    }

    public static int mod20(int num) {
        int result = num%20;
        if (result<0) {
            return result+20;
        }
        return result;
    }

    public static EdgeLength getEdgeLength(int level) {
        initToLevel(level);
        if (level < 0) {
            return edgeLengths_neg.get(-level);
        }
        return edgeLengths_pos.get(level);
    }

    public float x(int rotation) {
        return xLengths[mod20(rotation)];
    }

    public float y(int rotation) {
        return yLengths[mod20(rotation)];
    }
}
