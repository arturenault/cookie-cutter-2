package cc2.g8;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

	private boolean[] row_2 = new boolean [0];
	private int[] count =  new int[3];
	Shape curShape;
	private List<Shape> used_shapes = new ArrayList<Shape>(); 
	private static int offset = 0;
	private Random gen = new Random();
	private static int[][] dough_cache;
	private HashMap<Move, Shape> move_rotation = new HashMap<Move, Shape>();
	private HashMap<Move, Point> move_point = new HashMap<Move, Point>();
	private HashMap<Integer, List<Move>> stackingMoves = new HashMap<Integer,List<Move>>();
	private int run = 0;
	private static int shapeIndex = 11;

	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes) {


		Point[] cutter = new Point [length];
		ShapeGen sg = new ShapeGen();


		// first time picking shape
		if (row_2.length != cutter.length - 1) {
			row_2 = new boolean [cutter.length - 1];
			if (length == 11) {
				curShape = sg.elevenShape(length, count[0]);
				count[0]++;
			}
			else if (length == 8) {
				curShape = sg.createDynamicShape(opponent_shapes[0], length);
				count[1]++;
				used_shapes.add(curShape);
			}
			else {
				curShape = sg.createDynamicShape(opponent_shapes[0], length);
				count[2]++;
				used_shapes.add(curShape);
			}
		}
		// backup shapes
		else {
			if (length == 11) {
				curShape = sg.elevenShape(length, count[0]);

				count[0]++;
			}
			else {
				curShape = sg.changeShape(curShape, used_shapes);
			}	
			used_shapes.add(curShape);
		}
		return curShape;
	}


	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		if (dough.uncut() || dough.countCut() == 5) 
		{
			for(int i = 11; i >= 5; i -=3)
			{
				stackingMoves.put(i, cutShapes(dough, i, shapes));
			}
			dough_cache = new int[dough.side()][dough.side()];
		}

		Move nextMove = null;
		List<Move> commonMoves = new ArrayList<Move>();
		Set<Point> opponent_move = getOpponentMove(dough);
		Set<Point> convex_hull = new HashSet<Point>();
		Set<Point> neighbors = new HashSet<Point>();
		List<Move> cur_stackingMoves = new ArrayList<Move>();
		List<Move> destructiveMoves = new ArrayList<Move>();
		List<Move> neighborMoves = new ArrayList<Move>();

		if(!opponent_move.isEmpty())
		{
			convex_hull = convexHull(opponent_move, dough);
			neighbors = getNeighbors(opponent_move);
		}

		while(true)
		{	
			if(dough.uncut())
			{
				destructiveMoves = destructOpponent(convex_hull, dough, 5, shapes);
				cur_stackingMoves = stackingMoves.get(5);
			}
			else
			{
				cur_stackingMoves = stackingMoves.get(shapeIndex);
				destructiveMoves = destructOpponent(convex_hull, dough, shapeIndex, shapes);
			}

			while(neighbors.size() <= dough.side()*dough.side())
			{
				if(dough.uncut())
				{
					neighborMoves = destructOpponent(neighbors, dough, 5, shapes);
				}
				else
				{
					neighborMoves = destructOpponent(neighbors, dough, shapeIndex, shapes);
				}
				commonMoves = getIntersection(destructiveMoves, neighborMoves);

				System.out.println("found destructive moves: " + destructiveMoves.size());
				System.out.println("found neighbor moves: " + neighborMoves.size());
				System.out.println("found stacking moves: " + cur_stackingMoves.size());
				System.out.println("Found common moves: " + commonMoves.size());

				while(!destructiveMoves.isEmpty() || !neighborMoves.isEmpty())
				{
					if (commonMoves.isEmpty()) 
					{
						System.out.println("no common moves");
						if (!destructiveMoves.isEmpty()) 
						{
							nextMove = popRandom(destructiveMoves);
						}
						else
						{
							nextMove = popRandom(neighborMoves);
						}
					}
					else
					{
						System.out.println("selecting common move");
						if (!commonMoves.isEmpty()) {
							nextMove = popRandom(commonMoves);
						}
					}

					Shape[] rotations = shapes[nextMove.shape].rotations();
					if (dough.cuts(rotations[nextMove.rotation], nextMove.point)) {
						System.out.println("This move is @ point " + nextMove.point.toString());
						updateDough(nextMove);
						return nextMove;
					}
				}
				if(neighbors.size() == 0 || neighbors.size() == 2500)
				{
					if(dough.uncut())
					{
						List<Move> nextMoves = randomMove(dough, 5, shapes);
						return nextMoves.get(gen.nextInt(nextMoves.size()));
					}
					System.out.println("Opponent ran out of moves!");
					for (int i = 11; i >=5; i-=3)
					{
						List<Move> nextMoves = randomMove(dough, i, shapes);
						if (nextMoves.size() > 0)
						{
							return nextMoves.get(gen.nextInt(nextMoves.size()));
						}
					}
				}
				System.out.println("Expanding Neighbors: " + neighbors.size());
				neighbors.addAll(getNeighbors(neighbors));
			}
			if(shapeIndex > 5)
			{
				neighbors = getNeighbors(opponent_move);
				shapeIndex -= 3;
			}
			else
			{
				return null;
			}
		}
	}

	private Move popRandom(List<Move> moves)
	{
		int randomIdx = gen.nextInt(moves.size());
		Move result = moves.get(randomIdx);
		moves.remove(randomIdx);
		return result;
	}

	public void updateDough(Move nextMove)
	{
		Move myMove = nextMove;
		Shape myShape = move_rotation.get(myMove);
		Point q = move_point.get(myMove);
		Iterator<Point> pts = myShape.iterator();
		while(pts.hasNext())
		{
			Point p = pts.next();
			dough_cache[p.i + q.i][p.j + q.j] = 1;
		}
	}

	public List<Move> getIntersection(List<Move> list1, List<Move> list2) 
	{
		List<Move> temp = new ArrayList<Move>();
		for(Move m1: list1)
		{
			for(Move m2: list2)
			{
				if (m1.shape == m2.shape && m1.rotation == m2.rotation && m1.point.equals(m2.point))
				{
					System.out.println("SHAPES: " + m1.shape + " " + m2.shape);
					temp.add(m1);
				}
			}	
		}
		return temp;
	}

	private List<Move> destructOpponent(Set<Point> neighbors, Dough dough, int index, Shape[] shapes)
	{	
		List<Move> moves = new ArrayList<Move>();
		for (Point p: neighbors)
		{
			for (int si = 0 ; si != shapes.length ; ++si) 
			{
				if (shapes[si] == null) continue;
				if (shapes[si].size() != index) continue;
				Shape[] rotations = shapes[si].rotations();
				for (int ri = 0 ; ri != rotations.length ; ++ri) 
				{
					Shape s = rotations[ri];
					if (dough.cuts(s, p))
					{
						Move cur_Move = new Move(si, ri, p);
						moves.add(cur_Move);
						move_rotation.put(cur_Move, rotations[ri]);
						move_point.put(cur_Move, p);
					}
				}
			}
		}
		return moves;
	}
	private List<Move> randomMove(Dough dough, int index, Shape[] shapes) {
		List<Move> moves = new ArrayList<Move>();
		for (int i = offset ; i != dough.side() - offset ; ++i) {
			for (int j = offset; j != dough.side() - offset; ++j) {
				Point p = new Point(i, j);
				for (int si = 0 ; si != shapes.length ; ++si) {
					if (shapes[si] == null) continue;
					if (shapes[si].size() != index) continue;
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length ; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p))
							moves.add(new Move(si, ri, p));
					}
				}
			}
		}
		return moves;
	}

	private List<Move> cutShapes(Dough dough, int index, Shape[] shapes) {
		int oddRow = 0;
		int oddStep = 0;
		List <Move> moves = new ArrayList <Move> ();
		for (int i = 0; i < dough.side(); i++) {
			for (int j = 0; j < dough.side(); j++) {
				Point p = new Point(i, j);

				for (int si = 0 ; si != shapes.length ; ++si) {

					if (shapes[si] == null) continue;
					if (shapes[si].size() != index) continue;

					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0; ri < rotations.length; ri++) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p)) {
							// System.out.println("Found a move");
							Move cur_Move = new Move(si, ri, p);
							moves.add(cur_Move);
							move_rotation.put(cur_Move, rotations[ri]);
							move_point.put(cur_Move, p);
						}
					}
				}
			}
		}
		return moves;
	}


	private Set<Point> getOpponentMove(Dough dough)
	{
		Set<Point> opponent_moves = new HashSet<Point>();
		for(int i = 0; i < dough.side(); i++)
		{
			for(int j = 0; j < dough.side(); j++)
			{
				if(dough_cache[i][j] == 0 && !dough.uncut(i,j))
				{
					dough_cache[i][j] = 1;
					opponent_moves.add(new Point(i,j));
				}
			}
		}
		return opponent_moves;
	}

	private Set<Point> getNeighbors(Set<Point> points)
	{
		Set<Point> neighbors = new HashSet<Point>();
		for(Point point: points)
		{
			int m = dough_cache.length;
			int n = (point.i > 0 ? 1 : 0) + (point.i < m ? 1 : 0)
					+ (point.j > 0 ? 1 : 0) + (point.j < m ? 1 : 0);
			if (point.i > 0) neighbors.add(new Point(point.i - 1, point.j));
			if (point.i < m) neighbors.add(new Point(point.i + 1, point.j));
			if (point.j > 0) neighbors.add(new Point(point.i, point.j - 1));
			if (point.j < m) neighbors.add(new Point(point.i, point.j + 1));
		}
		return neighbors;
	}

	private Set<Point> convexHull(Set<Point> opponent_moves, Dough dough) {
		int side_length = 4;
		Set<Point> result = new HashSet<Point>();
		int minLength = Integer.MAX_VALUE;
		int minWidth = Integer.MAX_VALUE;
		int maxLength = Integer.MIN_VALUE;
		int maxWidth = Integer.MIN_VALUE;

		int centroid_x = 0;
		int centroid_y = 0;
		for(Point p : opponent_moves)
		{
			centroid_x += p.i;
			centroid_y += p.j;
		}
		centroid_x = (int)((double)centroid_x/opponent_moves.size());
		centroid_y = (int)(centroid_y/opponent_moves.size());

		Point centroid = new Point(centroid_x, centroid_y);
		System.out.println(centroid.toString());
		minWidth = (int)(centroid_x-(side_length/2.0)) > 0? (int)(centroid_x-(side_length/2.0)): 0;
		maxWidth = (int)(centroid_x+(side_length/2.0)) < dough.side()? (int)(centroid_x+(side_length/2.0)) : dough.side();
		minLength = (int)(centroid_y-(side_length/2.0)) > 0? (int)(centroid_y-(side_length/2.0)): 0;
		maxLength = (int)(centroid_y+(side_length/2.0)) < dough.side() ? (int)(centroid_y+(side_length/2.0)) : dough.side();

		System.out.println("maxlength: "+ maxLength);
		for(int i = minWidth; i < maxWidth; i++)
		{
			for(int j = minLength; j < maxLength; j++)
			{
				result.add(new Point(i,j));
			}
		}
		System.out.println((maxWidth-minWidth) + " by " + (maxLength-minLength));
		return result;
	}

	private void printDough()
	{
		for(int i = 0; i < dough_cache.length; i++)
		{
			for(int j = 0; j < dough_cache.length; j++)
			{
				System.out.print(dough_cache[i][j]);
			}
			System.out.print("\n");
		}
	}
}