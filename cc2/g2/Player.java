package cc2.g2;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

	private Random gen = new Random();
	
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		return ShapeFactory.getNext(length);
	}
	
	private int getConvexHullMinSide(Shape eleven) {
		int minX, minY, maxX, maxY;
		minX = minY = Integer.MAX_VALUE;
		maxX = maxY = 0;
		Iterator<Point> iter = eleven.iterator();
		while(iter.hasNext()) {
			Point p = iter.next();
			if(p.i < minX) minX = p.i;
			if(p.i > maxX) maxX = p.i;
			if(p.j < minY) minY = p.j;
			if(p.j > maxY) maxY = p.j;
		}
		return Math.min(maxX - minX, maxY - minY) + 1;
	}

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		int opp_hull_min = getConvexHullMinSide(opponent_shapes[0]);
		
		int si = 0;
		
		// prune larger shapes if initial move
		
		if (dough.uncut()) {
			si = 2;
		}
		
		//Stack 11s
		Shape shape = shapes[si];
		Shape[] rotations = shape.rotations();
		
		int increment = 6;
		if(opp_hull_min == 2) {
			increment = 2;
		} else if(opp_hull_min <= 4) {
			increment = 4;
		}
		
		System.out.println(increment);
				
		for(int ri = 0; ri < rotations.length; ++ri) {
			for(int inc = 0; inc < increment/3+1; ++inc) {
				for(int j = increment-(inc*2+1); j < dough.side(); j += increment) {
					for(int i = 0; i < dough.side(); ++i) {
						Point p = new Point(i, j);
						Shape s = rotations[ri];
						if (dough.cuts(s, p)) {
							return new Move(si, ri, p);
						}
					}
				}
			}
		}
		
		//After there's no place left to stack
		for (si = 0; si < shapes.length; si++) {
			for (int i = 0; i < dough.side(); i++) {
				for (int j = 0; j < dough.side(); j++) {
					rotations = shape.rotations();
					for (int ri = 0; ri < rotations.length; ri++) {
						Point p = new Point(i, j);
						Shape s = rotations[ri];
						if (dough.cuts(s, p)) {
							return new Move(si, ri, p);
						}
					}
				}
			}
		}
		return randomLargestCut(dough, shapes);
	}
		
	public Move randomLargestCut(Dough dough, Shape[] shapes) {
		ArrayList <Move> moves = new ArrayList <Move> ();
		for(int si = 0; si < shapes.length; ++si) {
			if (shapes[si] == null)
				continue;
			for (int i = 0 ; i != dough.side() ; ++i) {
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point p = new Point(i, j);
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length ; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p))
							moves.add(new Move(si, ri, p));
					}
				}
			}
			if(moves.size() > 0)
				break;
		}
		return moves.get(gen.nextInt(moves.size()));
	}
	
	public int score(Dough dough, Shape[] opponent_shapes) {
		for(Shape nextLargestShape : opponent_shapes) {
			int nmoves = 0;
			for (int i = 0 ; i != dough.side() ; ++i) {
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point p = new Point(i, j);
					Shape[] rotations = nextLargestShape.rotations();
					for (int ri = 0 ; ri != rotations.length ; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p))
							nmoves++;
					}
				}
			}
			if(nmoves > 0)
				return nmoves*nextLargestShape.size();
		}
		return 0;
	}
}
