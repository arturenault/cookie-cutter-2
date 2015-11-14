package cc2.g1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Point;
import cc2.sim.Shape;

public class DiagonalStrategy {
	Queue<Point> diags;
	Set<Point> coveredPoints;
	
	public DiagonalStrategy(){
		diags=getAllDiagonalPoints();
		coveredPoints=new HashSet<Point>();
	}
	
	int rotation=0;
	public List<Point> getPointsOnLine(int x1, int y1, int x2, int y2){
		float ratio = (y2 - y1) / (x2 - x1);
		List<Point> points = new ArrayList<Point>();
		int width = x2 - x1;
		for(int i = 0; i < width; i++) {
		    float x = x1 + i;
		    float y = y1 + (ratio * i);
		    points.add(new Point(Math.round(x),Math.round(y)));
		}
		return points;
	}
	
	static class PQSort implements Comparator<Point>{
		public int compare(Point one, Point two){
			return   (one.i  + one.j) -(two.i + two.j ) ;
		}
	}
	public Queue<Point> getAllDiagonalPoints(){
		int x1=0,x2=49,y1=0,y2=49;
		Queue<Point> diags=new PriorityQueue<Point>(new PQSort());
	
		for(int shift=0;shift<50;shift=shift+3){
			diags.addAll( getPointsOnLine( x1+shift,  y1,  x2 + shift,  y2));
			diags.addAll( getPointsOnLine( x1,  y1+shift,  x2,  y2 + shift));
		}
		return diags;
	}
	
	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{	
		/**.
		 * First Run... prune larger shapes if initial move\
		 */
		int minShapeIndex=0;
		if (dough.uncut()) {
			int min = Integer.MAX_VALUE;
			Shape minShape = null;
			for (int si=0;si< shapes.length;si++){
				Shape s=shapes[si];
				if (min > s.size()){
					min = s.size();
					minShape=s;
					minShapeIndex=si;
				}
			}
			Shape[] rotations = minShape.rotations();
			int maxSize=Utils.getOpponentsMaxDimensionForShape(new Shape[]{minShape});
			Point q=new Point(45,45);
			for (int ri = 0 ; ri != rotations.length; ++ri) {
				Shape s = rotations[ri];
				if (dough.cuts(s, q) && overlapCount(shapes[minShapeIndex],ri,q, dough, 2) <= 4){
					updateNearbyPoints(shapes[minShapeIndex],ri,q,dough,3);
					return new Move(minShapeIndex, ri, q);
				}
			}
			for (int s = 0 ; s != shapes.length ; ++s)
				if (shapes[s].size() != min)
					shapes[s] = null;
		}
		
		/**.
		 * Normal Run...
		 */
		int ri=0;
	/*	int ri=rotation;
		for (int si = 0 ; si != shapes.length ; ++si) {
			if (shapes[si] == null) continue;
			if (shapes[si].size() != 11) continue;
			for (Point q :diags ){
				Shape[] rotations = shapes[si].rotations();
				if(ri>=rotations.length) continue;
				Shape s = rotations[ri];
				if (dough.cuts(s, q)){
					rotation=(rotation+1)%2;
					return new Move(si, ri, q);
				}
			}
		}
		*/
		/**.
		 * DIAGONAL FILL .. based on 11 shape only
		 */
		for ( ri = 0 ; ri != 4; ++ri) { 
			for (int si = 0 ; si != shapes.length ; ++si) {
				if (shapes[si] == null) continue;
				if (shapes[si].size() != 11) continue;
				for (Point q :diags ){
					Shape[] rotations = shapes[si].rotations();
					if(ri>=rotations.length) continue;
					Shape s = rotations[ri];
					if (dough.cuts(s, q) && overlapCount(shapes[si],ri,q, dough, 2) <= 4){
						updateNearbyPoints(shapes[si],ri,q,dough,3);
						return new Move(si, ri, q);
					}
				}
			}
		}
		
		/**.
		 * Diagonal Board BOARD FILL .. based on 8 shape only ..every move we miss 3 points its, a loss for us.
		 */
		 for (  ri = 0 ; ri != 4; ++ri) {
			for (int si = 0 ; si != shapes.length ; ++si) {
				if (shapes[si] == null) continue;
				if (shapes[si].size() != 8) continue;
				for (Point q :diags ){
					Shape[] rotations = shapes[si].rotations();
					if(ri>=rotations.length) continue;
					Shape s = rotations[ri];
					if (dough.cuts(s, q) &&  overlapCount(shapes[si],ri,q, dough, 1) <= 2){
						updateNearbyPoints(shapes[si],ri,q,dough,2);
						return new Move(si, ri, q);
					}
				}
			}
		} 
		 
			/**.
			 * Diagonal Board BOARD FILL .. based on 8 shape only ..every move we miss 3 points its, a loss for us.
			 */
			 for (  ri = 0 ; ri != 4; ++ri) {
				for (int si = 0 ; si != shapes.length ; ++si) {
					if (shapes[si] == null) continue;
					if (shapes[si].size() != 8) continue;
					for (Point q :diags ){
						Shape[] rotations = shapes[si].rotations();
						if(ri>=rotations.length) continue;
						Shape s = rotations[ri];
						if (dough.cuts(s, q) &&  overlapCount(shapes[si],ri,q, dough, 1) <= 6){
							updateNearbyPoints(shapes[si],ri,q,dough,3);
							return new Move(si, ri, q);
						}
					}
				}
			}
		 
				/**.
				 * Diagonal Board BOARD FILL .. based on 8 shape only ..every move we miss 3 points its, a loss for us.
				 */
				 for (  ri = 0 ; ri != 4; ++ri) {
					for (int si = 0 ; si != shapes.length ; ++si) {
						if (shapes[si] == null) continue;
						if (shapes[si].size() != 8) continue;
						for (Point q :diags ){
							Shape[] rotations = shapes[si].rotations();
							if(ri>=rotations.length) continue;
							Shape s = rotations[ri];
							if (dough.cuts(s, q) &&  overlapCount(shapes[si],ri,q, dough, 1) <= 8){
								updateNearbyPoints(shapes[si],ri,q,dough,3);
								return new Move(si, ri, q);
							}
						}
					}
				}
		/**.
		 * GREEDY Entire BOARD FILL using  11 shape only ..every move we miss 3 points its, a loss for us.
		 */
		for (int si = 0 ; si != shapes.length ; ++si) {
			if (shapes[si] == null) continue;
			if (shapes[si].size() != 11) continue;

			for (int i = 0 ; i != dough.side() ; ++i){
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point p = new Point(i, j);
					for (  ri = 0 ; ri != 4; ++ri) {
						Shape[] rotations = shapes[si].rotations();
						if(ri>=rotations.length) continue;
						Shape s = rotations[ri];
						if (dough.cuts(s, p) && overlapCount(shapes[si],ri,p, dough, 1) <= 4){
							updateNearbyPoints(shapes[si],ri,p,dough,3);
							return new Move(si, ri, p);
						}
					}
				}
			}
		}
		/**.
		 * GREEDY Entire BOARD FILL using  11 shape only ..every move we miss 3 points its, a loss for us.
		 */
		for (int si = 0 ; si != shapes.length ; ++si) {
			if (shapes[si] == null) continue;
			if (shapes[si].size() != 11) continue;

			for (int i = 0 ; i != dough.side() ; ++i){
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point p = new Point(i, j);
					for (  ri = 0 ; ri != 4; ++ri) {
						Shape[] rotations = shapes[si].rotations();
						if(ri>=rotations.length) continue;
						Shape s = rotations[ri];
						if (dough.cuts(s, p) && overlapCount(shapes[si],ri,p, dough, 1) <= 8){
							updateNearbyPoints(shapes[si],ri,p,dough,3);
							return new Move(si, ri, p);
						}
					}
				}
			}
		}
		
		/**.
		 * GREEDY Entire BOARD FILL using  11 shape only ..every move we miss 3 points its, a loss for us.
		 */
		for (int si = 0 ; si != shapes.length ; ++si) {
			if (shapes[si] == null) continue;
			if (shapes[si].size() != 11) continue;

			for (int i = 0 ; i != dough.side() ; ++i){
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point p = new Point(i, j);
					for (  ri = 0 ; ri != 4; ++ri) {
						Shape[] rotations = shapes[si].rotations();
						if(ri>=rotations.length) continue;
						Shape s = rotations[ri];
						if (dough.cuts(s, p)){
							return new Move(si, ri, p);
						}
					}
				}
			}
		}
		/**.
		 * Diagonal Board BOARD FILL .. based on 8 shape only ..every move we miss 3 points its, a loss for us.
		 */
		 for (  ri = 0 ; ri != 4; ++ri) {
			for (int si = 0 ; si != shapes.length ; ++si) {
				if (shapes[si] == null) continue;
				if (shapes[si].size() != 8) continue;
				for (Point q :diags ){
					Shape[] rotations = shapes[si].rotations();
					if(ri>=rotations.length) continue;
					Shape s = rotations[ri];
					if (dough.cuts(s, q) &&  overlapCount(shapes[si],ri,q, dough, 1) <= 4){
						updateNearbyPoints(shapes[si],ri,q,dough,3);
						return new Move(si, ri, q);
					}
				}
			}
		} 
		
		/**.
		 *  find all valid cuts
		 */

		ArrayList <Move> moves = new ArrayList <Move> ();
		for (int i = 0 ; i != dough.side() ; ++i)
			for (int j = 0 ; j != dough.side() ; ++j) {
				Point p = new Point(i, j);
				for (int si = 0 ; si != shapes.length ; ++si) {
					if (shapes[si] == null) continue;
					Shape[] rotations = shapes[si].rotations();
					for (  ri = 0 ; ri != rotations.length ; ++ri) {
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
	}
	/**.
	 * Returns the count of points overlapped by the current shape..
	 * @param shape
	 * @param rotationIndex
	 * @param q
	 * @param dough
	 * @return
	 */
	private int overlapCount(Shape shape, int rotationIndex, Point q, Dough dough , int nearbyPointRange) {
    	Shape s = shape.rotations()[rotationIndex];
    	int count=0;
    	for (Point p : s) {
    		int i = p.i + q.i;
    		int j = p.j + q.j;
    		if(coveredPoints.contains(p)) 
    			count+=1;
    		for(int coverRange=1; coverRange<=nearbyPointRange;coverRange++ ) {
    		// for each of its 4 neighbours, add to visited 
	    		for (int d = 0; d < dx.length; d++) {
	    			if (i + coverRange * dy[d] < dough.side() 
	    					&& i + coverRange * dy[d] >= 0
	    					&& j + coverRange * dx[d] < dough.side() 
	    					&& j + coverRange * dx[d] >= 0
	    					&& dough.uncut(i + coverRange * dy[d], j + coverRange * dx[d])) {
	    				Point p2=new Point(i + coverRange * dy[d], j + coverRange * dx[d]);
	    				if(coveredPoints.contains(p2)) 
	    					count+=1;
	    			}
	    		}
    		}
    	}
    	return count;
    }
	 

	static int[] dx = {-1, 0, 0, 1};
    static int[] dy = {0, 1, -1, 0};

    private void updateNearbyPoints(Shape shapes, int rotationIndex, Point q, Dough dough, int nearbyPointRange) {
    	Shape s = shapes.rotations()[rotationIndex];
 
    	for (Point p : s) {
    		int i = p.i + q.i;
    		int j = p.j + q.j;
    		// for each of its 4 neighbours, add to visited 
    		for(int coverRange=1; coverRange<=nearbyPointRange;coverRange++ ) {
	    		for (int d = 0; d < dx.length; d++) {
	    			if (i + coverRange*dy[d] < dough.side() 
	    					&& i + coverRange*dy[d] >= 0
	    					&& j + coverRange*dx[d] < dough.side() 
	    					&& j + coverRange*dx[d] >= 0
	    					&& dough.uncut(i + coverRange*dy[d], j + coverRange*dx[d])) {
	    				Point p2=new Point(i + coverRange*dy[d], j + coverRange*dx[d]);
	    				coveredPoints.add(p2);
	    			}
	    		}
    		}
    	}
    }
	
}
