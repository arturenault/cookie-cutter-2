package cc2.sim;

public class Dough {

	// the array
	private boolean[][] dough;

	// number of cuts
	private boolean uncut;

	// create new Dough
	public Dough(int side)
	{
		if (side <= 0)
			throw new IllegalArgumentException();
		dough = new boolean [side][side];
		uncut = true;
	}

	// return number of rows
	public int side()
	{
		return dough.length;
	}

	// return if whole dough is uncut
	public boolean uncut()
	{
		return uncut;
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

	// perform cut using shape (simulator calls this)
	public boolean cut(Shape shape, Point q)
	{
		if (!cuts(shape, q))
			return false;
		for (Point p : shape)
			dough[p.i + q.i][p.j + q.j] = true;
		uncut = false;
		return true;
	}
}
