package cc2.g7;
 
import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

	private boolean[] row_2 = new boolean [0];

	private Random gen = new Random();
	
//	private Shape last_shape = null;
//	private Point last_pos = null;
	private Move last_move = null;
//	private int[] transform = {2,0,0};

	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		if (length == 11)
		{
			// Generate cutter of length 11
			Point[] cutter = new Point [length];
			cutter[0] = new Point(0, 0);
			cutter[1] = new Point(1, 0);
			cutter[2] = new Point(2, 0);
			cutter[3] = new Point(3, 0);
			cutter[4] = new Point(2, 1);
			cutter[5] = new Point(3, 1);
			cutter[6] = new Point(3, 2);
			cutter[7] = new Point(3, 3);
			cutter[8] = new Point(2, 3);
			cutter[9] = new Point(2, 4);
			cutter[10] = new Point(1, 4);
			return new Shape(cutter);
		}
		
		if (length == 8)
		{
			// Generate cutter of length 11
			Point[] cutter = new Point [length];
			cutter[0] = new Point(0, 1);
			cutter[1] = new Point(0, 2);
			cutter[2] = new Point(0, 3);
			cutter[3] = new Point(1, 1);
			cutter[4] = new Point(1, 2);
			cutter[5] = new Point(1, 3);
			cutter[6] = new Point(2, 2);
			cutter[7] = new Point(0, 4);
			return new Shape(cutter);
		}
		
		/*if (length == 5)
		{
			// Generate cutter of length 5
			Point[] cutter1 = new Point [length];
			cutter1[0] = new Point(0, 0);
			cutter1[1] = new Point(0, 1);
			cutter1[2] = new Point(0, 2);
			cutter1[3] = new Point(0, 3);
			cutter1[4] = new Point(0, 4);
			Shape shape1 = new Shape(cutter1);
			
			Point[] cutter2 = new Point [length];
			cutter2[0] = new Point(1, 0);
			cutter2[1] = new Point(1, 1);
			cutter2[2] = new Point(1, 2);
			cutter2[3] = new Point(1, 3);
			cutter2[4] = new Point(0, 3);
			Shape shape2 = new Shape(cutter2);
			
			
			if (row_2.length != 4) {
				row_2 = new boolean [4];
				return shape1;
			}
			else
			{
				Point[] cutter = new Point [length];
				int i;
				do {
					i = gen.nextInt(cutter.length - 1);
				} while (row_2[i]);
				row_2[i] = true;
				cutter[cutter.length - 1] = new Point(i, 1);
				for (i = 0 ; i != cutter.length - 1 ; ++i)
					cutter[i] = new Point(i, 0);
			}
		} */
			
			/*boolean sameAsOpponent = false;
			
			for (int i=0; i<opponent_shapes.length; i++)
			{
				if (opponent_shapes[i].equals(shape1))
				{
					sameAsOpponent = true;
				}
			}
			
			if (!sameAsOpponent)
			{
				return shape1;
			}
			else
			{
				return shape2;
			}
			
			
		}*/
		
		// check if first try of given cutter length
		Point[] cutter = new Point [length];
		if (row_2.length != cutter.length - 1) {
			// save cutter length to check for retries
			row_2 = new boolean [cutter.length - 1];
			for (int i = 0 ; i != cutter.length ; ++i)
				cutter[i] = new Point(i, 0);
		} else {
			// pick a random cell from 2nd row but not same
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
		if (!dough.uncut()) {
			if (last_move != null) {
				Shape[] rotations = shapes[last_move.shape].rotations();
				Shape s = rotations[last_move.rotation];
				int min_i = Integer.MAX_VALUE;
				int min_j = Integer.MAX_VALUE;
				int max_i = Integer.MIN_VALUE;
				int max_j = Integer.MIN_VALUE;
				for (Point p : s) {
					if (min_i > p.i) min_i = p.i;
					if (max_i < p.i) max_i = p.i;
					if (min_j > p.j) min_j = p.j;
					if (max_j < p.j) max_j = p.j;
					System.out.println("p.i " + p.i + " p.j " + p.j);
				}
				
				int i_coordinate = 0;
				int j_coordinate = 0;
				
				int[] transform = new int[2];
				
				System.out.println("max i " + max_i + " max j " + max_j);
				
				// Try to put below
				for (int i = max_i+last_move.point.i+1 ; i < dough.side() ; ++i) {
					Point pos = new Point(i, last_move.point.j);
					if (dough.cuts(s, pos)) {
						last_move = new Move(last_move.shape,last_move.rotation,pos);
						return (last_move);
					}
				}
				
				// Try to put right
				for (int j = max_j + last_move.point.j+1  ; j < dough.side() ; ++j) {
					Point pos = new Point(last_move.point.i, j);
					if (dough.cuts(s, pos)) {
						last_move = new Move(last_move.shape,last_move.rotation,pos);
						return (last_move);
					}
				}
				
				
				/*if (max_i > max_j) {
					transform[0]= 0;
					//transform[1]= max_j+1;
					transform[1]= 2>max_j+1?2:max_j+1;
				}
				else {
					//transform[0]= max_i+1;
					transform[0]= 2>max_i+1?2:max_i+1;
					transform[1]= 0;
				}*/

				
			}
		}
		// prune larger shapes if initial move
		if (dough.uncut()) {
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
		for (int si = 0 ; si != shapes.length ; ++si) {
			if (shapes[si] == null) continue;
			for (int i = 0 ; i != dough.side() ; ++i){
				for (int j = 0 ; j != dough.side() ; ++j) {
					
					/*int min_i = Integer.MAX_VALUE;
					int min_j = Integer.MAX_VALUE;
					int max_i = Integer.MIN_VALUE;
					int max_j = Integer.MIN_VALUE;
					if (si == 0)
					{
						for (Point p : shapes[si]) {
							if (min_i > p.i) min_i = p.i;
							if (max_i < p.i) max_i = p.i;
							if (min_j > p.j) min_j = p.j;
							if (max_j < p.j) max_j = p.j;
						}
						
						if (j > max_j || i > max_i)
						{
							Point p = new Point(i, j);
							Shape[] rotations = shapes[si].rotations();
							for (int ri = 0 ; ri != rotations.length; ++ri) {
								Shape s = rotations[ri];
								if (dough.cuts(s, p))
									moves.add(new Move(si, ri, p));
									if (moves.size() > 0)
									{
										Move rand_move = moves.get(0);
										if (!dough.uncut()) {
											last_move = rand_move;
										}
										return rand_move;
									}
							}
						}
					}*/
					Point p = new Point(i, j);
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p))
							moves.add(new Move(si, ri, p));
							/*if (moves.size() > 0)
							{
								break;
							}*/
					}
				}
			}
			if (moves.size() > 0)
				break;
		}
		// return a cut randomly
		//Move rand_move = moves.get(0);
		Move rand_move = moves.get(gen.nextInt(moves.size()));
		if (!dough.uncut()) {
			last_move = rand_move;
		}
		return rand_move;
	}
}