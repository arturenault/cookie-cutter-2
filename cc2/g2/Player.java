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
		Shape s = ShapeFactory.getNext(length);
		System.out.println("MUHSHAPE "+length+":"+s);
		return s;
	}

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		// prune larger shapes if initial move

		if (dough.uncut()) {
			// play random possible move of the 5-piece
			Shape[] forceFiveShape = {null, null, shapes[2]};
			return randomLargestCut(dough, forceFiveShape);
		}
		
		//Stack 11s
		int si = 0;
		Shape shape = shapes[si];
		Shape[] rotations = shape.rotations();
		for (int ri = 0; ri < rotations.length; ri++) {
			for (int j = 1 ; j < dough.side(); j+=2) {
				for (int i = 0 ; i < dough.side() ; i++) {
					Point p = new Point(i, j);
					Shape s = rotations[ri];
					if (dough.cuts(s, p)) {
						return new Move(si, ri, p);
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
