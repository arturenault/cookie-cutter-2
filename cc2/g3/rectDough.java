package cc2.g3;

import cc2.sim.Point;
import cc2.sim.Shape;

public class rectDough {

    // the array
    protected boolean[][] dough;
    protected int width;
    protected int height;
    protected int size;
    
    // number of cuts
    protected int n_cuts;

    // create new Dough
    public rectDough(Point dimensions)
    {
	height = dimensions.i;
	width = dimensions.j;
	size = height*width;
	dough = new boolean[height][width];
	n_cuts = 0;
    }

    // return number of rows and columns
    public Point dimensions()
    {
	return new Point(dough.length, dough[0].length);
    }

    // return if whole dough is uncut
    public boolean uncut()
    {
	return n_cuts == 0;
    }

    // return if everything is cut
    public boolean saturated() {
	return n_cuts == size;
    }

    // return how much of the dough is cut
    public int countCut()
    {
	return n_cuts;
    }

    // check if specific position can be cut
    public boolean uncut(Point q) {
	return uncut(q.i,q.j);
    }
    
    // check if specific position can be cut
    public boolean uncut(int i, int j)
    {
	return i >= 0 && i < dough.length &&
	    j >= 0 && j < dough[i].length &&
	    dough[i][j] == false;
    }

    // check if shape can cut dough
    public boolean cuts(Shape shape, Point q)
    {
	for (Point p : shape)
	    if (!uncut(p.i + q.i, p.j + q.j))
		return false;
	return true;
    }

    // cut a single point
    public boolean cut(Point q) {
	if (dough[q.i][q.j] == true) { // bug somewhere else
	    System.out.println("Already cut");
	    System.exit(0);
	}
	dough[q.i][q.j] = true;
	n_cuts++;
	return true;
    }
    
    // perform cut using shape (simulator calls this)
    public boolean cut(Shape shape, Point q)
    {
	if (!cuts(shape, q))
	    return false;
	for (Point p : shape) {
	    dough[p.i + q.i][p.j + q.j] = true;
	    n_cuts++;
	}
	return true;
    }
}
