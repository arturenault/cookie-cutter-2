package cc2.g6;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Point;
import cc2.sim.Shape;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.*;

/**
 * Created by rbtying on 10/31/15.
 */
public class MagicDough {

    public int width, height;

    public char data[][];

    private Move lastMove;

    public class Window {
        public int i;
        public int j;
        public int w;
        public int h;

        public Window(int i, int j, int w, int h) {
            this.i = i;
            this.j = j;
            this.w = w;
            this.h = h;
        }

        public boolean intersects(Window other) {
            return !(i + w < other.i || i > other.i + w || j + h < other.j || j > other.j + h);
        }

        public boolean contains(Point p) {
            return !(p.i < i || p.i >= i + w || p.j < j || p.j >= j + h);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Window)) {
                return false;
            }
            Window wd = (Window) o;

            return wd.i == i && wd.j == j && wd.w == w && wd.h == h;
        }

        @Override
        public int hashCode() {
            return i + j * 31 + w * 31 * 31 + h * 31 * 31 * 31;
        }

        @Override
        public String toString() {
            return String.format("Window i: %d j: %d w: %d h: %d", i, j, w, h);
        }
    }

    public MagicDough(int size) {
        this(size, size);
    }

    public MagicDough(int width, int height) {
        this.width = width;
        this.height = height;
        data = new char[width][height];
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data.length; ++j) {
                data[i][j] = 0;
            }
        }
        lastMove = null;
    }

    public boolean diffFromDough(Dough d) {
        assert(d.side() == width && d.side() == height);
        boolean changed = false;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < width; ++j) {
                if (!d.uncut(i, j) && isClear(i, j)) {
                    // set to -1 because it's due to an enemy
                    data[i][j] = (char) -1;
                    changed = true;
                }
            }
        }
        return changed;
    }

    public boolean isClear(Point loc, Shape s) {
        for (Point p : s) {
            if (!isClear(loc.i + p.i, loc.j + p.j)) {
                return false;
            }
        }
        return true;
    }

    public boolean isClear(Point loc, Shape s, Window w) {
        for (Point p : s) {
            Point np = new Point(loc.i + p.i, loc.j + p.j);
            if (!w.contains(np)) {
                return false;
            }
            if (!isClear(np)) {
                return false;
            }
        }
        return true;
    }

    public boolean isClear(Point p[]) {
        for (Point pp : p) {
            if (!isClear(pp.i, pp.j)) {
                return false;
            }
        }
        return true;
    }

    public boolean isClear(Point p) {
        return isClear(p.i, p.j);
    }

    public boolean isClear(int i, int j) {
        return !(i < 0 || i >= width || j < 0 || j >= height) && data[i][j] == 0;
    }

    public boolean isMine(Point p) {
        return isMine(p.i, p.j);
    }

    public boolean isMine(int i, int j) {
        return !(i < 0 || i >= width || j < 0 || j >= height) && data[i][j] > 0;
    }

    public boolean isEnemy(int i, int j) {
        return !(i < 0 || i >= width || j < 0 || j >= height) && data[i][j] < 0;
    }

    public boolean isEnemy(Point p) {
        return isEnemy(p.i, p.j);
    }

    public boolean canMakeMove(Move m, Shape s[]) {
        return isClear(m.point, s[m.shape].rotations()[m.rotation]);
    }

    public boolean makeMove(Move m, Shape s[], boolean enemy) {
        if (!canMakeMove(m, s)) {
            return false;
        }

        char x = (char) ((enemy) ? (-1) : (1));

        for (Point p : s[m.shape].rotations()[m.rotation]) {
            data[p.i + m.point.i][p.j + m.point.j] = x;
        }
        lastMove = m;
        return true;
    }

    public void undoLastMove(Shape s[]) {
        if (lastMove != null) {
            for (Point p : s[lastMove.shape].rotations()[lastMove.rotation]) {
                data[p.i + lastMove.point.i][p.j + lastMove.point.j] = 0;
            }
        }
        lastMove = null;
    }

    public Window effectWindow(int i, int j) {
        int WINDOW_SIZE = 22;

        if (i < 0) {
            i = 0;
        }

        if (j < 0) {
            j = 0;
        }

        if (i + WINDOW_SIZE >= width) {
            i = width - WINDOW_SIZE;
        }
        if (j + WINDOW_SIZE >= height) {
            j = height - WINDOW_SIZE;
        }

        return new Window(i, j, WINDOW_SIZE, WINDOW_SIZE);
    }

    public Map<Window, Long> scoreAllWindows(Shape s[]) {
        Map<Window, Long> out = new HashMap<>();

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Window w = effectWindow(i, j);
                if (!out.containsKey(w)) {
                    out.put(w, countSpaces(w, s));
                }
            }
        }
        return out;
    }

    /**
     * Searches a window w for places to put shapes
     * that are fully contained within w.
     * @param w the window to search
     * @param s an array of shapes to test
     * @return number of valid spaces to be consumed in the window
     */
    public long countSpaces(Window w, Shape s[]) {
        long num_spaces = 0;
        for (Shape ss : s) {
            for (int i = w.i; i < w.i + w.w; ++i) {
                for (int j = w.j; j < w.j + w.h; ++j) {
                    Point p = new Point(i, j);
                    for (Shape sss : ss.rotations()) {
                        if (isClear(p, sss, w)) {
                            num_spaces += sss.size();
                            break;
                        }
                    }
                }
            }
        }

        return num_spaces;
    }
}
