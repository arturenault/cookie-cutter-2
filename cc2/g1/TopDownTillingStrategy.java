package cc2.g1;
import java.util.ArrayList;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Point;
import cc2.sim.Shape;
public class TopDownTillingStrategy implements ICutStrategy {
	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
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
		for (int i = 0 ; i != dough.side() ; ++i)
			for (int j = 0 ; j != dough.side() ; ++j) {
				Point p = new Point(i, j);
				for (int si = 0 ; si != shapes.length ; ++si) {
					if (shapes[si] == null) continue;
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length ; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p))
							moves.add(new Move(si, ri, p));
					}
				}
			}
		// return a cut randomly
		//return a max sized cut
		int maxShape=0;
		int maxSize=0;
		for(int i=0; i< moves.size();i++){
			if(shapes[moves.get(i).shape].size()>maxSize){
				maxShape=i;
				maxSize=shapes[moves.get(i).shape].size();
			}
			 
		}
		return moves.get(maxShape);
		//return moves.get(gen.nextInt(moves.size()));
	}
}
