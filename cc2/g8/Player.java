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
			offset = (int)((3.0/8)*dough.side());
			int min = Integer.MAX_VALUE;
			for (Shape s : shapes)
				if (min > s.size())
					min = s.size();
			for (int s = 0 ; s != shapes.length ; ++s)
				if (shapes[s].size() != min)
					shapes[s] = null;
		}
		
		// find all valid cuts

		ArrayList <Move> moves = new ArrayList <Move> ();
		boolean large = false, med = false;
		
		// 11 piece
			int area = (dough.side() - 2*offset)*(dough.side()-2*offset);
			if (percentCut(dough, offset, area) > 0.5 && offset >= 0)
			{
				offset = offset/2;
			}
			moves = cutShapes(dough, 11, shapes, moves);
			if (moves.isEmpty()) {
				moves = cutShapes(dough, 8, shapes, moves);
			}
			if (moves.isEmpty()) {
				moves = cutShapes(dough, 5, shapes, moves);
			}

		// return a cut randomly
		return moves.get(gen.nextInt(moves.size()));
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
	
	private double percentCut(Dough dough, int offset, int area)
	{
		int num_cuts = 0;
		for (int i = offset ; i != dough.side()- offset ; ++i)
		{
			for (int j = offset; j != dough.side()-offset; ++j) 
			{
				if (dough.uncut(i,j) == false)
				{
					num_cuts++;
				}
			}
		}
		return num_cuts/((double)area);
	}
}
