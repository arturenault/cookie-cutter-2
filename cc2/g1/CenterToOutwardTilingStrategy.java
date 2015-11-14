package cc2.g1;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Point;
import cc2.sim.Shape;

public class CenterToOutwardTilingStrategy  implements ICutStrategy {
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
		Queue<PossibleMove> moves = new PriorityQueue<PossibleMove> (new Comparator<PossibleMove>(){
 			public int compare(PossibleMove rhs , PossibleMove lhs){
 				if (lhs.score > rhs.score) return +1;
 		        if (lhs.score == rhs.score) return 0;
 		        return -1;

			}
			
		});
		for (int i = 0 ; i != dough.side() ; ++i)
			for (int j = 0 ; j != dough.side() ; ++j) {
				Point p = new Point(i, j);
				for (int si = 0 ; si != shapes.length ; ++si) {
					if (shapes[si] == null) continue;
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length ; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p)){
							// calculate score
							Move m=new Move(si, ri, p) ;
							moves.add(new PossibleMove( m, calculateScore( m,  dough, shapes)));
						}
					}
				}
			}
		// return a cut randomly
		//return a max sized cut
/*		int maxShape=0;
		int maxSize=0;
		for(int i=0; i< moves.size();i++){
			if(shapes[moves.get(i).shape].size()>maxSize){
				maxShape=i;
				maxSize=shapes[moves.get(i).shape].size();
			}
		}
		return moves.get(maxShape);*/
		return moves.poll().m;
		//return moves.get(gen.nextInt(moves.size()));
	}
	
	static int[] dx = {-1, 0, 0, 1};
    static int[] dy = {0, 1, -1, 0};
	public static int calculateScore(Move m, Dough dough, Shape[] shapes) {
		Shape s = shapes[m.shape].rotations()[m.rotation];
		Set<Point> allP = getPoints(s);
		int count = 0;
		for (Point p : allP) {
			int i = p.i;
			int j = p.j;
			// for each of its 4 neighbours
			for (int d = 0; d < dx.length; d++) {
				if (i + 2*dy[d] < dough.side() && i + 2*dy[d] >= 0
						&& j + 2*dx[d] < dough.side() && j + 2*dx[d] >= 0
						&& dough.uncut(i + 2*dy[d], j + 2*dx[d]) ) {
					Point p2=new Point(i + 2*dy[d], j + 2*dx[d]);
					if(!allP.contains(p2))
						count = count + 3;
 				}else if (i + dy[d] < dough.side() && i + dy[d] >= 0
						&& j + dx[d] < dough.side() && j + dx[d] >= 0
						&& dough.uncut(i + dy[d], j + dx[d]) ) {
					Point p2=new Point(i + dy[d], j + dx[d]);
					if(!allP.contains(p2))
						count = count + 1;
				}
			}
		}
		
		return allP.size() * 10000 + count*100 + getDistanceFromCenter(dough.side(), m.point);
	}
	
	public static int getDistanceFromCenter(int side, Point p){
		int y=Math.abs(p.i-side/2);
		int x=Math.abs(p.j-side/2);
		return (int)Math.abs(side/2-Math.sqrt(x*x + y*y));
	}
	
	public static Set <Point>  getPoints(Shape shape){
		Set <Point> s = new HashSet <Point> ();
		for (Point p : shape)
		     s.add(p);
		return s;
	}
	
}
