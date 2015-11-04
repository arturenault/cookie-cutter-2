package cc2.g6;

import cc2.sim.Point;
import cc2.sim.Shape;

import java.util.function.Function;

/**
 * Created by rbtying on 10/27/15.
 */
public class Util {

    public static int findArgMin(int start, int end, Function<Integer, Integer> f) {
        int idx = start;
        int minval = Integer.MAX_VALUE;
        for (int l = start; l < end; ++l) {
            int test = f.apply(l);
            if (test < minval) {
                minval = test;
                idx = l;
            }
        }
        return idx;
    }

    /**
     * Returns (for this rotation of the shape)
     * an array containing:
     *  0: width of the
     * @param s
     * @return
     */
    public static Point[] dimensions(Shape s) {
        int left = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int top = Integer.MIN_VALUE;
        int bottom = Integer.MAX_VALUE;

        for (Point p : s) {
            left = Integer.min(p.i, left);
            right = Integer.max(p.i, right);
            bottom = Integer.min(p.j, left);
            top = Integer.max(p.j, right);
        }

        Point out[] = new Point[3];
        out[0] = new Point(right - left, top - bottom);
        out[1] = new Point(left, bottom);
        out[2] = new Point(right, top);

        return out;
    }

    public static int findMin(int start, int end, Function<Integer, Integer> f) {
        int i = findArgMin(start, end, f);
        return f.apply(i);
    }
}
