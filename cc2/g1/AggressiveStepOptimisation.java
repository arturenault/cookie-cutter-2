package cc2.g1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Point;
import cc2.sim.Shape;

public class AggressiveStepOptimisation {
	private int getHullSize(Shape shape) {		
		Map<String, Integer> mp= getAllDimensionFromShape(   shape);
		int xDiff=mp.get("max_X")-mp.get("min_X");
		int yDiff=mp.get("max_Y")-mp.get("min_Y");
		return  Math.min(xDiff, yDiff) + 1;
	}

	Map<String, Integer> getAllDimensionFromShape(Shape  shape){
		int min_X=0, max_X=0, min_Y=0, max_Y=0;
		 for(Point p :shape){
				 min_X=Math.min(min_X, p.i);
				 max_X=Math.max(max_X, p.i);
				 
				 min_Y=Math.min(min_Y, p.j);
				 max_Y=Math.max(max_Y, p.j);
	
		}
		Map<String, Integer> mp=new HashMap<String,Integer>();
		mp.put("min_X", min_X);
		mp.put("max_X", max_X);
		mp.put("min_Y", min_Y);
		mp.put("max_Y", max_Y);
		return mp;
	}
	
 void printOpponentMoves(Shape[] opponent_shapes, HashMap<Point, HashSet<Move>> points2moves, Point p){
	 HashSet<Move> ms= points2moves.get(p);
	 System.out.println("Moves at point : " + p.i + " ," +p.j);
	 for(Move m:ms ){
		 System.out.println("Opponents moves at point : " + (opponent_shapes[m.shape].rotations())[m.rotation]);
	 }
 }
	
 
 Move reduceOpponentStepMaximizeUs(Dough dough, Shape[] shapes, Shape[] opponent_shapes,  int opponentLength, int ourLength){
		HashMap<Point, HashSet<Move>> points2moves ;
		HashMap<Point, HashSet<Move>> ourPoints2moves ;
		points2moves = getAllPossibleMoves(dough, opponent_shapes[opponentLength], opponentLength);
		ourPoints2moves = getAllPossibleMoves(dough, shapes[ourLength], ourLength);
		Set<Point> focusPoints=ourPoints2moves.keySet();
		focusPoints.retainAll(points2moves.keySet());
		Entry<Point, HashSet<Move>> expP = getMostExpensivePoint(  points2moves , focusPoints);
		if(expP!=null){
			Point p=  expP.getKey();
			Move rettt212=  ourPoints2moves.get(p).iterator().next();
			Move rettt=  getMostBenefittingMove(  ourPoints2moves.get(p),   dough, shapes ,  points2moves);
			if(rettt  ==null){ return null; }
			//ourPoints2moves.get(p)
			Shape retts=(shapes[rettt.shape].rotations())[rettt.rotation];
			if (dough.cuts(retts, rettt.point)) {
				if(canOpponentBenefit(retts, rettt.point, points2moves)) {

					System.out.println("Moves at point : " + rettt.point.i + " ," +rettt.point.j);
					System.out.println("count pof moves : " + points2moves.get(p).size());
					System.out.println(opponent_shapes[0].size());
					System.out.println("r3");
					printOpponentMoves( opponent_shapes, points2moves,   p);
					System.out.println( "Opponents Impacted Moves : " + countPossibleSteps(retts , p,   points2moves));
					return rettt;
				}
			}
		}
		System.out.println("returning NULL");
		return null;
 }
 
 
  Move getMostBenefittingMove(HashSet<Move> ourMoves, Dough dough, Shape[] shapes , HashMap<Point, HashSet<Move>> points2moves){
	  if(ourMoves == null){
		  return null;
	  }
	  if( ourMoves.size()<=1){
		  return ourMoves.iterator().next();
	  }
	  HashMap<Move, Integer> bestMoves=new HashMap<Move, Integer> ();
	  for(Move m:ourMoves){
		    Shape s = (shapes[m.shape].rotations())[m.rotation];
			Point p = m.point;
			int count=0;
			if (dough.cuts(s, p)) {
				for(HashMap.Entry<Point, HashSet<Move>> opp: points2moves.entrySet() ) {
					if(opp.getValue().contains(m))
						count=count+opp.getValue().size();
				}
			}
			bestMoves.put(m, count);
	  }
	  
	  /**.
	   * Find move with largest impact
	   */
	  int max=0; Move bestMoveFound=null;
	  for(HashMap.Entry<Move, Integer> e: bestMoves.entrySet()){
		  if(max< e.getValue()){
			  bestMoveFound=e.getKey();
			  max=e.getValue();
		  }
	  }
	  return bestMoveFound;
	  
 }
 
 /**.
  * Do tiling using Give Shape... First do tiling with 0 rotation and then go into rotation variations
  * @param dough
  * @param shapes
  * @param opponent_shapes
  * @param si
  * @param increment
  * @return
  */
 Move doAggressiveTiling(Dough dough, Shape[] shapes, Shape[] opponent_shapes, int si , int increment){
		int si2=si;
		HashMap<Point, HashSet<Move>> points2moves ;
		points2moves = getAllPossibleMoves(dough, opponent_shapes[0], 0);
		//Stack 11s
		Shape shape = shapes[si];
		Shape[] rotations = shape.rotations();
		shape = shapes[si2];
		rotations = shape.rotations();
		for(int ri = 0; ri < rotations.length; ++ri) {
	 
			for(int j = increment ; j < dough.side(); j += increment) {
				for(int i = dough.side(); i >0 ; --i) {
					Point p = new Point(i, j);
					Shape s = rotations[ri];
					if (dough.cuts(s, p)) {
						if(points2moves.get(p) != null && canOpponentBenefit(s, p, points2moves)) {

							System.out.println("Moves at point : " + p.i + " ," +p.j);
							System.out.println("count pof moves : " + points2moves.get(p).size());
							System.out.println(opponent_shapes[0].size());
							System.out.println("r1");
							printOpponentMoves( opponent_shapes, points2moves,   p);
							System.out.println( "Opponents Impacted Moves: " + countPossibleSteps(s , p,   points2moves));

							return new Move(si2, ri, p);
						}
					}
				}
			}
		}
		return null;
 }
 
 
	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		int opp_hull_min = getHullSize(opponent_shapes[0]);
		/**.
		 *  First step : cut using smallest shape
		 *  
		 */
		int si = 0;
		if (dough.uncut()) {
			si = 2;
		}

		int increment = 6;
		if(opp_hull_min == 2) {
			increment = 4;
		} else if(opp_hull_min <= 4) {
			increment = 4;
		}

		/**.
		 * Capture all points where opponent can place 11 shapes, Fill it  using our 11
		 * Do tiling first 
		 */
		Move bestM=doAggressiveTiling(  dough,   shapes,   opponent_shapes,   si ,   increment);
		if(bestM!=null) return bestM;
		/**.
		 * Capture all points where opponent can place 11 shapes, Fill it  using our 11
		 * Find most expensive point now... 
		 */
		bestM= reduceOpponentStepMaximizeUs( dough, shapes, opponent_shapes, 0,0);
		if(bestM!=null) return bestM;


		/**.
		 * Capture all points where opponent can place 8 shapes, Fill it  using our 11
		 * Find most expensive point now... 
		 */
		bestM= reduceOpponentStepMaximizeUs( dough, shapes, opponent_shapes,1,0);
		if(bestM!=null) return bestM;

		/**.
		 * Capture all points where opponent can place 5 shapes, Fill it  using our 11
		 * Find most expensive point now... 
		 */
		bestM= reduceOpponentStepMaximizeUs( dough, shapes, opponent_shapes,2,0);
		if(bestM!=null) return bestM;

		/**.
		 * Capture all points where opponent can place 11 shapes, Fill it  using our 8
		 * Find most expensive point now... 
		 */
		bestM= reduceOpponentStepMaximizeUs( dough, shapes, opponent_shapes, 0,1);
		if(bestM!=null) return bestM;


		/**.
		 * Capture all points where opponent can place 8 shapes, Fill it  using our 8
		 * Find most expensive point now... 
		 */
		bestM= reduceOpponentStepMaximizeUs( dough, shapes, opponent_shapes,1,1);
		if(bestM!=null) return bestM;



		/**.
		 * Capture all points where opponent can place 5 shapes, Fill it  using our 8
		 * Find most expensive point now... 
		 */
		bestM= reduceOpponentStepMaximizeUs( dough, shapes, opponent_shapes,2,1);
		if(bestM!=null) return bestM;

		/**.
		 * Capture all points where opponent can place 11 shapes, Fill it  using our 5
		 * Find most expensive point now... 
		 */
		bestM= reduceOpponentStepMaximizeUs( dough, shapes, opponent_shapes,0,2);
		if(bestM!=null) return bestM;

		/**.
		 * Capture all points where opponent can place 8 shapes, Fill it  using our 5
		 * Find most expensive point now... 
		 */
		bestM= reduceOpponentStepMaximizeUs( dough, shapes, opponent_shapes,1,2);
		if(bestM!=null) return bestM;

		/**.
		 * Capture all points where opponent can place 8 shapes, Fill it  using our 5
		 * Find most expensive point now... 
		 */
		bestM= reduceOpponentStepMaximizeUs( dough, shapes, opponent_shapes,2,2);
		if(bestM!=null) return bestM;

		/**
		 * Return largest cut. Aggressive Search ?
		 */
		return firstLargestCut(dough, shapes);
	}
	
	private boolean canOpponentBenefit(Shape ours, Point place, HashMap<Point, HashSet<Move>> points2moves) {
		for(Point p : ours) {
			Point affectedPoint = new Point(p.i + place.i, p.j + place.j);
			if(points2moves.containsKey(affectedPoint))
				return true;
		}
		return false;
	}
	

	
	public Move firstLargestCut(Dough dough, Shape[] shapes) {
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
		}
		
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
	
	
	private int countPossibleSteps(Shape ours, Point place, HashMap<Point, HashSet<Move>> points2moves) {
		int count=0;
		for(Point p : ours) {
			Point affectedPoint = new Point(p.i + place.i, p.j + place.j);
			if(points2moves.containsKey(affectedPoint))
				 count=count + points2moves.get(affectedPoint).size();
		}
		return count;
	}
 
	public HashMap<Point, HashSet<Move>> getAllPossibleMoves(Dough dough, Shape[] shapes) {
		HashMap<Point, HashSet<Move>> points2moves = new HashMap<Point, HashSet<Move>>();
		for(int si = 0; si < shapes.length; ++si) {
			Shape[] rotations = shapes[si].rotations();
			for(int ri = 0; ri < rotations.length; ++ri) {
				for(int i = 0; i < dough.side(); ++i) {
					for(int j = 0; j < dough.side(); ++j) {
						Shape s = rotations[ri];
						Point p = new Point(i,j);
						if (dough.cuts(s, p)) {
							for(Point sp : s) {
								Point affectedPoint = new Point(sp.i + p.i, sp.j + p.j);
								if(!points2moves.containsKey(affectedPoint))
									points2moves.put(affectedPoint, new HashSet<Move>());
								points2moves.get(affectedPoint).add(new Move(si, ri, p));
							}
						}
					}
				}
			}
		}
		return points2moves;
	}
	
	/**.
	 * for opponents moves, find the best point
	 * @param points2moves
	 * @return
	 */
	public Map.Entry<Point, HashSet<Move>>  getMostExpensivePoint(HashMap<Point, HashSet<Move>> points2moves, Set<Point> focus){
		int min=0;
		Point maxPoint = null;
		Map.Entry<Point, HashSet<Move>> ret = null;
		for(HashMap.Entry<Point, HashSet<Move>> m: points2moves.entrySet()){
			if(!focus.contains(m.getKey())) continue;
			if(m.getValue().size() >min){
				min=m.getValue().size();
				maxPoint=m.getKey();
				ret=m;
			}
		}
		System.out.println("Max score "+ min +" , at point "+ maxPoint);
		return ret;
		
	}
	/**.
	 * 
	 * @param dough
	 * @param shape
	 * @param si
	 * @return
	 */
	public HashMap<Point, HashSet<Move>> getAllPossibleMoves(Dough dough, Shape  shape, int si ) {
		HashMap<Point, HashSet<Move>> points2moves = new HashMap<Point, HashSet<Move>>();
 
			Shape[] rotations = shape.rotations();
			for(int ri = 0; ri < rotations.length; ++ri) {
				for(int i = 0; i < dough.side(); ++i) {
					for(int j = 0; j < dough.side(); ++j) {
						Shape s = rotations[ri];
						Point p = new Point(i,j);
						if (dough.cuts(s, p)) {
							for(Point sp : s) {
								Point affectedPoint = new Point(sp.i + p.i, sp.j + p.j);
								if(!points2moves.containsKey(affectedPoint))
									points2moves.put(affectedPoint, new HashSet<Move>());
								points2moves.get(affectedPoint).add(new Move(si, ri, p));
							}
						}
					}
				}
			}
		return points2moves;
	}

}
