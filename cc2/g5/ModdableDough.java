package cc2.g5;

import cc2.sim.Dough;
import cc2.sim.Point;
import cc2.sim.Shape;

public class ModdableDough extends Dough {
    protected int size;
    protected int height;
    protected int width;

    public ModdableDough(Dough d) {
        super(d.side());
        this.height = d.side();
        this.width = d.side();
        size = d.side() * d.side();
        Point[] s = {new Point(0, 0)};
        Shape shape = new Shape(s);
        for (int i = 0; i < d.side(); ++i) {
            for (int j = 0; j < d.side(); ++j) {
                if (!d.uncut(i, j))
                    cut(shape, new Point(i, j));
            }
        }
    }

    // create Dough with specified height & width
    public ModdableDough(int width, int height) {
        super(height);
        this.height = height;
        this.width = width;
        if (height <= 0 || width <= 0)
            throw new IllegalArgumentException();
        size = height * width;
        super.dough = new boolean[height][width];
        super.n_cuts = 0;
    }

    public boolean saturated() {
        return super.n_cuts == size;
    }

    public boolean cut(Point q) {
        dough[q.i][q.j] = true;
        n_cuts++;
        return true;
    }

    public boolean undoCut(Shape shape, Point q)
    {
        for (Point p : shape) {
            dough[p.i + q.i][p.j + q.j] = false;
        }
        return true;
    }

    // check if specific position can be cut
    public boolean uncut(Point q) {
        return uncut(q.i, q.j);
    }

}
