package org.jf.Penroser;

public class GeometryUtil {
    /**
     * Determines if a point is in a polygon
     *
     * Algorithm taken from http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     *
     * @param vertices The vertices of the polygon, as x,y pairs
     * @param x the x coordinate of the point to test
     * @param y the y coordinate of the point to test
     * @return true if the point is contained within the polygon
     */
    public static boolean containsPoint(float[] vertices, float x, float y) {
        int nvert = vertices.length/2;
        boolean c=false;

        for (int i=0, j=nvert-1; i<nvert; j=i++) {
            float yi = vertices[(i<<1)+1];
            float yj = vertices[(j<<1)+1];

            if ((yi>y) != (yj>y)) {
                float xi = vertices[i<<1];
                float xj = vertices[j<<1];

                if  (x < ((xj-xi) * (y-yi) / (yj-yi) + xi)) {
                    c = !c;
                }
            }
        }
        return c;
    }

    /**
     * Determines if 3 points are in counter clockwise order
     *
     * Algorithm taken from http://compgeom.cs.uiuc.edu/~jeffe/teaching/373/notes/x05-convexhull.pdf
     */
    private static boolean ccw(float x1, float y1, float x2, float y2, float x3, float y3) {
        return (y3-y1)*(x2-x1) > (y2-y1)*(x3-x1);
    }

    /**
     * Determines of the line segments ab and cd intersect
     *
     * Algorithm taken from http://compgeom.cs.uiuc.edu/~jeffe/teaching/373/notes/x06-sweepline.pdf
     */
    private static boolean lineIntersects(float ax, float ay, float bx, float by, float cx, float cy, float dx, float dy) {
        return (ccw(ax, ay, cx, cy, dx, dy) != ccw(bx, by, cx, cy, dx, dy)) &&
               (ccw(ax, ay, bx, by, cx, cy) != ccw(ax, ay, bx, by, dx, dy));
    }

    /**
     * Computes which edges of the triangle intersect with the rectangle.
     * @return A bitmask denoting which triangle edges intersect with the rectangle.
     *
     * The bits in the mask will be set as follows
     * bit 0 - edge (0,1)
     * bit 1 - edge (1,2)
     * bit 2 - edge (2,0)
     */
    public static int triangleRectIntersection(float[] triangle, float[] rect) {
        int mask = 0;

        for (int i=0; i<3; i++) {
            int xindex = i<<1;
            int yindex = xindex+1;
            if (containsPoint(rect, triangle[xindex], triangle[yindex])) {
                mask |= 1<<MathUtil.positiveMod(i-1, 3);
                mask |= 1<<i;
            }
        }

        for (int i=0; i<3; i++) {
            if ((mask & (1<<i)) == 0) {
                int xi = i<<1;
                int yi = xi+1;
                int xi2 = MathUtil.positiveMod(yi+1, 6);
                int yi2 = xi2+1;

                float trianglex1 = triangle[xi];
                float triangley1 = triangle[yi];
                float trianglex2 = triangle[xi2];
                float triangley2 = triangle[yi2];

                for (int j=0; j<4; j++) {
                    int xj = j<<1;
                    int yj = xj+1;
                    int xj2 = MathUtil.positiveMod(yj+1, 8);
                    int yj2 = xj2+1;

                    float rectx1 = rect[xj];
                    float recty1 = rect[yj];
                    float rectx2 = rect[xj2];
                    float recty2 = rect[yj2];

                    if (lineIntersects(trianglex1, triangley1, trianglex2, triangley2, rectx1, recty1, rectx2, recty2)) {
                        mask |= 1<<i;
                        break;
                    }
                }
            }
        }

        return mask;
    }
}
