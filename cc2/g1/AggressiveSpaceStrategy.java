package cc2.g1;

import java.util.ArrayList;
import java.util.Random;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Point;
import cc2.sim.Shape;

public class AggressiveSpaceStrategy {
	private Random gen = new Random();
	private int opponentMinDim11=0;
	int getOpponentsMinDimensionFor11Shape(Shape[] opponent_shapes){
		int min_X=0, max_X=0, min_Y=0, max_Y=0;
		for(Shape s :   opponent_shapes){
			if(s.size()!=11)continue;
			 for(Point p :s){
				 min_X=Math.min(min_X, p.i);
				 max_X=Math.max(max_X, p.i);
				 
				 min_Y=Math.min(min_Y, p.j);
				 max_Y=Math.max(max_Y, p.j);
			 }
		}
		return Math.min( max_X-min_X,max_Y - min_Y);
	}
	
	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		// prune larger shapes if initial move
		if(opponentMinDim11==0){
			opponentMinDim11=getOpponentsMinDimensionFor11Shape( opponent_shapes)-1;
			if(opponentMinDim11<=0){
				opponentMinDim11=3;
			}
		}
		if (dough.uncut()) {
			// play random possible move of the 5-piece
			Shape[] forceFiveShape = {null, null, shapes[2]};
			return randomLargestCut(dough, forceFiveShape);
		}
		
		//Stack 11s
		int si = 0;
		Shape shape = shapes[si];
		Shape[] rotations = shape.rotations();
		ArrayList <Move> moves = new ArrayList <Move> ();
		for (int ri = 0; ri < rotations.length; ri++) {
			for (int j = 1 ; j < dough.side(); j+=opponentMinDim11) {
				for (int i = 0 ; i < dough.side() ; i+=opponentMinDim11) {
					Point p = new Point(i, j);
					Shape s = rotations[ri];
					if (dough.cuts(s, p)) {
			
						moves.add(new Move(si, ri, p));
					}
				}
			}
		}
		if(moves.size()>0)
			return   moves.get(gen.nextInt(moves.size()));
		
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
}
