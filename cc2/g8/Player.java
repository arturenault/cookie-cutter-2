package cc2.g8;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

	private boolean[] row_2 = new boolean [0];
	private static int offset = 0;
	private Random gen = new Random();
	private static int[][] dough_cache;

	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		// check if first try of given cutter length
		Point[] cutter = new Point [length];
		Map<Integer, List<Integer>> pairs = new HashMap<Integer, List<Integer>>();
		
		if (row_2.length != cutter.length - 1) {
			// save cutter length to check for retries
			row_2 = new boolean [cutter.length - 1];

			if (length == 11) {
				pairs.put(0, Arrays.asList(0,3));
				pairs.put(1, Arrays.asList(0, 1, 2, 3, 4));
				pairs.put(2, Arrays.asList(1, 3));
				pairs.put(3, Arrays.asList(3, 4));	
			}

			else if (length == 8) {
				pairs.put(0, Arrays.asList(1, 2));
				pairs.put(1, Arrays.asList(2, 3, 4));
				pairs.put(2, Arrays.asList(0, 1, 2));
			}

			else {
				pairs.put(0, Arrays.asList(0, 1));
				pairs.put(1, Arrays.asList(1));
				pairs.put(2, Arrays.asList(0, 1));
			}

			int i = 0;
			while (i < cutter.length) {
				for (Map.Entry<Integer, List<Integer>> pair: pairs.entrySet()) {
					for (int j : pair.getValue()) {
						cutter[i] = new Point(pair.getKey(), j);
						i++;
					}
				}
			}
		}
		else {
			int i;
			do {
				i = gen.nextInt(cutter.length - 1);
			} while (row_2[i]);
			row_2[i] = true;
			cutter[cutter.length - 1] = new Point(i, 1);
			for (i = 0 ; i != cutter.length - 1 ; ++i)
				cutter[i] = new Point(i, 0);
		}
		return new Shape(cutter);
	}

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		// prune larger shapes if initial move
		if (dough.uncut()) {
			dough_cache = new int[dough.side()][dough.side()];
			int min = Integer.MAX_VALUE;
			for (Shape s : shapes)

				if (min > s.size())
					min = s.size();
			for (int s = 0 ; s != shapes.length ; ++s)
				if (shapes[s].size() != min)
					shapes[s] = null;
		}
		if(dough.countCut() ==5)
		{
			dough_cache = new int[dough.side()][dough.side()];
		}

		// find all valid cuts
		ArrayList <Move> moves = new ArrayList<Move>();
		Set<Point> opponent_move = getOpponentMove(dough);
		Set<Point> neighbors = new HashSet<Point>();
		if(!opponent_move.isEmpty())
			neighbors = getNeighbors(opponent_move);

		if(moves.isEmpty())
		{
			neighbors = getNeighbors(opponent_move);
			moves = destructOpponent(neighbors, dough, 11, shapes, moves);
			while(moves.isEmpty() &&  neighbors.size() != 0 && neighbors.size() < dough.side()*dough.side())
			{
				neighbors.addAll(getNeighbors(neighbors));
				moves = destructOpponent(neighbors, dough, 11, shapes, moves);
			}
		}
		if(moves.isEmpty())
		{
			moves = cutShapes(dough, 11, shapes, moves);
		}

		if(moves.isEmpty())
		{
			neighbors = getNeighbors(opponent_move);
			moves = destructOpponent(neighbors, dough, 8, shapes, moves);
			while(moves.isEmpty() && neighbors.size() != 0 && neighbors.size() < dough.side()*dough.side())
			{
				neighbors.addAll(getNeighbors(neighbors));
				moves = destructOpponent(neighbors, dough, 8, shapes, moves);
			}
		}
		if(moves.isEmpty())
		{
			moves = cutShapes(dough, 8, shapes, moves);
		}

		if(moves.isEmpty())
		{
			neighbors = getNeighbors(opponent_move);
			moves = destructOpponent(neighbors, dough, 5, shapes, moves);
			while(moves.isEmpty() && neighbors.size()!= 0 && neighbors.size() < dough.side()*dough.side())
			{
				neighbors.addAll(getNeighbors(neighbors));
				moves = destructOpponent(neighbors, dough, 5, shapes, moves);
			}
		}
		if(moves.isEmpty())
		{
			moves = cutShapes(dough, 5, shapes, moves);
		}


		Move myMove = moves.get(gen.nextInt(moves.size()));
		Shape myShape = shapes[myMove.shape];
		Iterator <Point> myPoints = myShape.iterator();
		while(myPoints.hasNext())
		{
			Point p = myPoints.next();
			dough_cache[p.i][p.j] = 1;
		}
		return myMove;
	}
	
	private ArrayList<Move> destructOpponent(Set<Point> neighbors, Dough dough, int index, Shape[] shapes, ArrayList<Move> moves)
	{
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
						moves.add(new Move(si, ri, p));
				}
			}
		}
		return moves;
	}

	private ArrayList<Move> cutShapes(Dough dough, int index, Shape[] shapes, ArrayList<Move> moves) {
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
			neighbors.addAll(new HashSet<Point>(Arrays.asList(point.neighbors())));
		}
		return neighbors;
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
