package cc2.g8;

import cc2.sim.Point;
import cc2.sim.Shape;

import java.util.*;

public class ShapeGen {


	// writes map to shape
	public Shape returnShape(Map<Integer, List<Integer>> pairs, int length) {
		Point[] cutter = new Point [length];
		int i = 0;
		while (i < cutter.length) {
			for (Map.Entry<Integer, List<Integer>> pair: pairs.entrySet()) {
				for (int j : pair.getValue()) {
					cutter[i] = new Point(pair.getKey(), j);
					i++;
				}
			}
		}
		return new Shape(cutter);
	}

	// returns 11 shape 
	public Shape elevenShape(int length, int count) {
		Map<Integer, List<Integer>> pairs = new HashMap<Integer, List<Integer>>();

		if (count == 0) {
			pairs.put(0, Arrays.asList(0 , 3, 4));
			pairs.put(1, Arrays.asList(0, 1, 2, 3, 4));
			pairs.put(2, Arrays.asList(1, 2, 3));	
		}
		else if (count == 1) {	
			pairs.put(0, Arrays.asList(0, 3, 4));
			pairs.put(1, Arrays.asList(0, 1, 2, 3, 4));
			pairs.put(2, Arrays.asList(2, 3, 4));	
		}
		else if (count == 2) {
			pairs.put(0, Arrays.asList(0, 3, 4));
			pairs.put(1, Arrays.asList(0, 1, 2, 3, 4));
			pairs.put(2, Arrays.asList(0, 1, 2));	
		}
		else if (count == 3) {
			pairs.put(0, Arrays.asList(0, 3, 4));
			pairs.put(1, Arrays.asList(0, 1, 2, 3, 4));
			pairs.put(2, Arrays.asList(0, 1, 4));	
		}
		else {
			pairs.put(0, Arrays.asList(0, 3, 4));
			pairs.put(1, Arrays.asList(0, 1, 2, 3, 4));
			pairs.put(2, Arrays.asList(0, 3, 4));	
		}

		return returnShape(pairs, length);
	}

	public Shape changeShape(Shape shape, List<Shape> used_shapes) {
		Map<Integer, List<Integer>> pairs = new HashMap<Integer, List<Integer>>();

		Map<Integer, List<Integer>> newpairs = new HashMap<Integer, List<Integer>>();
		newpairs.put(0, Arrays.asList(0, 1, 2));
		newpairs.put(1, Arrays.asList(0, 2));
		Shape newShape = shape;

		if (shape.size() == 5 && !shape.equals(returnShape(newpairs, shape.size()))) {
			newShape = returnShape(newpairs, shape.size());
		}
		else {

			Shape s;
			if (shape.size() == 8) {
				pairs.put(0, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
				s = returnShape(pairs, shape.size());
			}
			else {
				pairs.put(0, Arrays.asList(0, 1, 2, 3, 4));
				s = returnShape(pairs, shape.size());
			}

			newShape = s;
			boolean match = false;
			int i = shape.size() - 1;
			for (Shape n : used_shapes) {
				if (n.equals(newShape)) {
					match = true;
				}
			}
			if (match == true) {
				match = false;
			}
			else {
				match = true;
			}
			while (!match) {
				if (shape.size() == 8 && pairs.get(0).contains(shape.size() - 1)) {
					pairs.put(0, Arrays.asList(0, 1, 2, 3, 4, 5, 6));
				}
				else if (shape.size() == 5 && pairs.get(0).contains(shape.size() - 1)) {
					pairs.put(0, Arrays.asList(0, 1, 2, 3));
				}
				pairs.put(1, Arrays.asList(i - 1));

				i--;
				newShape = returnShape(pairs, shape.size());

				for (Shape n : used_shapes) {
					if (n.equals(newShape)) {
						match = true;
					}
				}
				if (match == true) {
					match = false;
				}
				else {
					match = true;
				}
			}

		}

		return newShape;
	}


	// returns Shape fitting convex hull
	public Shape createDynamicShape(Shape opponent_shape, int n) {

		Iterator <Point> it = opponent_shape.iterator();
		int minLength = Integer.MAX_VALUE;
		int minWidth = Integer.MAX_VALUE;
		int maxLength = Integer.MIN_VALUE;
		int maxWidth = Integer.MIN_VALUE;

		while (it.hasNext()) {
			Point p = it.next();
			minLength = Math.min(minLength, p.i);
			maxLength = Math.max(maxLength, p.i);
			minWidth = Math.min(minWidth, p.j);
			maxWidth = Math.max(maxWidth, p.j);
		}

		boolean[][] block = new boolean[maxLength + 1][maxWidth + 1];
		Iterator <Point> it2 = opponent_shape.iterator();

		while (it2.hasNext()) {
			Point p = it2.next();
			block[p.i][p.j] = true;
		}

		boolean[][] newblock = new boolean[maxLength + 4][maxWidth + 4];
		for (int i = 0; i < block.length; i++) {
			for (int j = 0; j < block[i].length; j++) {
				newblock[i + 2][j + 2] = block[i][j];
			}
		}

		for (int i = 0; i < newblock.length; i++) {
			for (int j = 0; j < newblock[i].length; j++) {
				System.out.print(newblock[i][j] + " ");
			}
			System.out.println();
		}

		Set<Point> points = new HashSet<Point>();
		System.out.println("maxwidth, maxlength: " + maxWidth + ", " + maxLength);
		if (maxWidth == 0 || maxLength == 0) {
			if (n == 5) {
				points.add(new Point(0, 0));
				points.add(new Point(0, 1));
				points.add(new Point(0, 2));
				points.add(new Point(0, 3));
				points.add(new Point(0, 4));
			}
			else if (n == 8) {
				points.add(new Point(0, 0));
				points.add(new Point(0, 1));
				points.add(new Point(0, 2));
				points.add(new Point(0, 3));
				points.add(new Point(0, 4));
				points.add(new Point(0, 5));
				points.add(new Point(0, 6));
				points.add(new Point(0, 7));

			}

		}
		else {
			points = createShape(newblock, n, points, 2);
		}

		Map<Integer, List<Integer>> rMap = new HashMap<Integer, List<Integer>>();
		for (Point p : points) {
			if (!rMap.containsKey(p.i)) {
				List<Integer> list = new ArrayList<Integer>();
				list.add(p.j);
				rMap.put(p.i, list);
			}
			else {
				rMap.get(p.i).add(p.j);
			}
		}
		return returnShape(rMap, n);
	}

	// find a shape from opponent's shape
	public Set<Point> createShape(boolean[][] cutout, int n, Set<Point> points, int edges) {
		for (int i = 0; i < cutout.length; i++) {
			for (int j = 0; j < cutout.length; j++) {
				int count = 0;
				if (cutout[i][j] == false) {
					if (i > 0 && cutout[i - 1][j]) {
						count++;
					}
					if (i < cutout.length - 1 && cutout[i + 1][j]) {
						count++;
					}
					if (j > 0 && cutout[i][j - 1]) {
						count++;
					}
					if (j < cutout[j].length - 1 && cutout[i][j + 1]) {
						count++;
					}
					if (count >= edges) {

						if (points.size() == 0) {
							Point p = new Point(i, j);
							cutout[i][j] = true;
							points.add(p);
						}
						else if (points.size() != 0 && checkAllAdjacent(points, i, j)){
							if (checkAdjacent(points, i, j)) {
								Point p = new Point(i, j);
								cutout[i][j] = true;
								points.add(p);
							}
							else if (points.size() + 2 <= n){
								Point p = new Point(i, j);
								cutout[i][j] = true;
								points.add(p);

								Point new_p = returnAdjPoint(cutout, points, i, j);
								cutout[new_p.i][new_p.j] = true;
								points.add(new_p);
							}
						}
						if (points.size() >= n) {
							return points;
						}
					}
				}
			}
		}
		return createShape(cutout, n, points, edges - 1);
	}

	// checks for adjacent points from all 8 sides
	public boolean checkAllAdjacent(Set<Point> point, int i, int j) {
		for (Point p : point) {
			if ((p.i == i - 1 && p.j == j) || (p.i == i + 1 && p.j == j) || (p.i == i && p.j == j - 1) || (p.i == i && p.j == j + 1)
					|| (p.i == i - 1 && p.j == j - 1) || (p.i == i + 1 && p.j == j - 1) || (p.i == i - 1 && p.j == j + 1) || (p.i == i + 1 && p.j == j + 1)) {
				return true;
			}
		}
		return false;
	}

	// checks for adjacent points from 4 sides
	public boolean checkAdjacent(Set<Point> point, int i, int j) {
		for (Point p : point) {
			if ((p.i == i - 1 && p.j == j) || (p.i == i + 1 && p.j == j) || (p.i == i && p.j == j - 1) || (p.i == i && p.j == j + 1)) {
				return true;
			}
		}
		return false;
	}


	// returns adjacent point between two pieces
	public Point returnAdjPoint(boolean[][] cutout, Set<Point> point, int i, int j) {
		for (Point p : point) {
			if ((p.i - 1 == i - 1 || p.i - 1 == i || p.i - 1 == i + 1) && (p.j == j - 1 || p.j == j || p.j == j + 1)) {
				if (p.i - 1 >= 0 && !cutout[p.i - 1][p.j]) {
					return new Point(p.i - 1, p.j);
				}
			}
			if ((p.i + 1 == i - 1 || p.i + 1 == i || p.i + 1 == i + 1) && (p.j == j - 1 || p.j == j || p.j == j + 1)) {
				if (p.i + 1 <= i && !cutout[p.i + 1][p.j])
					return new Point(p.i + 1, p.j);
			}
			if ((p.i == i - 1 || p.i == i || p.i == i + 1) && (p.j - 1 == j - 1 || p.j - 1 == j || p.j - 1 == j + 1)) {
				if (p.j - 1 >= 0 && !cutout[p.i][p.j - 1])
					return new Point(p.i, p.j - 1);
			}
			if ((p.i == i - 1 || p.i == i || p.i == i + 1) && (p.j + 1 == j - 1 || p.j + 1 == j || p.j + 1 == j + 1))
				if (p.j + 1 <= j && !cutout[p.i][p.j + 1])
					return new Point(p.i, p.j + 1);
		}
		return new Point(i, j);
	}

}

