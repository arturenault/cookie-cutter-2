package cc2.sim;

import java.util.*;

public class Shape implements Iterable <Point> {

	// points in shape (this rotation)
	private Set <Point> points;

	// hash code of points (this rotation)
	private int hash;

	// circular list of rotations
	private Shape rotate;

	// internal constructor
	private Shape(Set <Point> points, Shape rotate)
	{
		this.points = points;
		this.rotate = rotate;
		hash = points.hashCode();
	}

	// generate shape and its rotations
	public Shape(Point[] points)
	{
		if (!Point.shape(points))
			throw new IllegalArgumentException("Not a shape");
		int min_i = Integer.MAX_VALUE;
		int min_j = Integer.MAX_VALUE;
		int max_i = Integer.MIN_VALUE;
		int max_j = Integer.MIN_VALUE;
		for (Point p : points) {
			if (min_i > p.i) min_i = p.i;
			if (max_i < p.i) max_i = p.i;
			if (min_j > p.j) min_j = p.j;
			if (max_j < p.j) max_j = p.j;
		}
		Set <Point> points_0 = new HashSet <Point> ();
		Set <Point> points_1 = new HashSet <Point> ();
		Set <Point> points_2 = new HashSet <Point> ();
		Set <Point> points_3 = new HashSet <Point> ();
		for (Point p : points) {
			points_0.add(new Point(p.i - min_i, p.j - min_j));
			points_1.add(new Point(p.j - min_j, max_i - p.i));
			points_2.add(new Point(max_i - p.i, max_j - p.j));
			points_3.add(new Point(max_j - p.j, p.i - min_i));
		}
		this.points = points_0;
		hash = points_0.hashCode();
		if (points_0.equals(points_1))
			rotate = this;
		else if (points_0.equals(points_2))
			rotate = new Shape(points_1, this);
		else
			rotate = new Shape(points_1,
			         new Shape(points_2,
			         new Shape(points_3, this)));
	}

	// size of shape
	public int size()
	{
		return points.size();
	}

	// check if shapes are equal
	public boolean equals(Shape shape)
	{
		if (points.size() == shape.points.size()) {
			Shape rot = this;
			do {
				if (rot.hash == shape.hash &&
				    rot.points.equals(shape.points))
					return true;
				rot = rot.rotate;
			} while (rot != this);
		}
		return false;
	}

	// generic equal
	public boolean equals(Object obj)
	{
		if (obj instanceof Shape)
			return equals((Shape) obj);
		return false;
	}

	// the only way to access the points
	public Iterator <Point> iterator()
	{
		return points.iterator();
	}

	// generate array of rotated shapes
	public Shape[] rotations()
	{
		Shape rot = this;
		int r = 0;
		do {
			r++;
			rot = rot.rotate;
		} while (rot != this);
		Shape[] rots = new Shape [r];
		rot = this;
		r = 0;
		do {
			rots[r++] = rot;
			rot = rot.rotate;
		} while (rot != this);
		return rots;
	}

	// invariant to rotation order
	public int hashCode()
	{
		Shape rot = this;
		int hash = 0;
		do {
			hash ^= rot.hash;
			rot = rot.rotate;
		} while (rot != this);
		return hash;
	}

	// convert shape to string
	public String toString(Point q, boolean par)
	{
		StringBuffer buf = new StringBuffer();
		boolean f = true;
		for (Point p : points) {
			if (f) f = false;
			else buf.append(", ");
			if (par) buf.append("(");
			buf.append((p.i + q.i) + ", " + (p.j + q.j));
			if (par) buf.append(")");
		}
		return buf.toString();
	}

	// default convert to string
	public String toString()
	{
		return toString(new Point(0, 0), true);
	}
}
