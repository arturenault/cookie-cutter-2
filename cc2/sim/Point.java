package cc2.sim;

import java.util.*;

public class Point implements Comparable <Point> {

	public final int i;  // row
	public final int j;  // column

	public Point(int i, int j)
	{
		if (i < 0)
			throw new IllegalArgumentException("Negative row");
		if (j < 0)
			throw new IllegalArgumentException("Negative column");
		this.i = i;
		this.j = j;
	}

	public boolean equals(Point p)
	{
		return i == p.i && j == p.j;
	}

	public boolean equals(Object o)
	{
		if (o instanceof Point)
			return equals((Point) o);
		return false;
	}

	public int hashCode()
	{
		int f = 0x9e3779b1;
		return ((i * f) ^ j) * f;
	}

	public int compareTo(Point p)
	{
		return i != p.i ? i - p.i : j - p.j;
	}

	public String toString()
	{
		return "(" + i + ", " + j + ")";
	}

	public Point[] neighbors()
	{
		int m = Integer.MAX_VALUE;
		int n = (i > 0 ? 1 : 0) + (i < m ? 1 : 0)
		      + (j > 0 ? 1 : 0) + (j < m ? 1 : 0);
		Point[] points = new Point [n];
		if (i > 0) points[--n] = new Point(i - 1, j);
		if (i < m) points[--n] = new Point(i + 1, j);
		if (j > 0) points[--n] = new Point(i, j - 1);
		if (j < m) points[--n] = new Point(i, j + 1);
		return points;
	}

	public static boolean shape(Point[] points)
	{
		if (points == null || points.length == 0)
			return false;
		// add items in set and check uniqueness
		HashSet <Point> open = new HashSet <Point> ();
		for (Point p : points)
			if (p == null || !open.add(p))
				return false;
		// check if single connected component
		Stack <Point> fringe = new Stack <Point> ();
		fringe.push(points[0]);
		open.remove(points[0]);
		do {
			for (Point p : fringe.pop().neighbors())
				if (open.remove(p))
					fringe.push(p);
		} while (!fringe.empty());
		return open.isEmpty();
	}
}
