package cc2.g9;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;
import java.lang.*;

public class Utils {
    public Utils() {
    }
    public static Move eachQueueMove(Dough dough, Shape s, int gapOffset, int colOffset, int row, boolean flag4 ) {
        // ---------- diagonal queue ( Reverse Order )----------
        int i = dough.side() - 1;
    	if(flag4)
    	{
    		switch(row)
    		{
    		case 2: i = 2; break;
    		case 3: i = 0; break;
    		case 4: i = 2;
    		}
    	}
        for (; i >= 0 ; i=i-gapOffset ){
            int j = (-1)*i + colOffset;
            if (j >= 0){
                Point thisPt = new Point(i, j);
                Move thisMv = new Move(0, 2, thisPt);
                if (dough.cuts(s, thisPt)){
                    return thisMv;
                }
            }
        }
        return null;
    }
    public static Move fillInQueueMove(Dough dough, Shape[] shapes) {
        System.out.println("size of shapes" + shapes.length);
        System.out.println("size of shapes rotation" + shapes[0].rotations().length);
        Shape[] rotations = shapes[0].rotations();
        Shape s = rotations[2];
        int[] colOffsets = {44, 33, 55, 22, 66, 11, 77};
        int[] densityOffsets = {4, 3, 2, 1};

        for (int densityOffset : densityOffsets) {
            for (int colOffset : colOffsets) {
                for (int i = 1; i < dough.side(); i = i + densityOffset) {
                    int j = (-1)*i + colOffset;
                    if (j >=0) {
                        Point thisPt = new Point(i, j);
                        Move thisMv = new Move(0, 2, thisPt);
                        if (dough.cuts(s, thisPt)){
                            return thisMv;
                        } 
                    }
                }
            }
        }
        return null;
    }
    public static ArrayList<Point> savePointsForLater(Move defenseMv, ArrayList<Move> prevDefMoves, ArrayList<Point> savedPoints, int gapOffset, Dough dough, Shape shape) {
        Point curPt = defenseMv.point;
        if (curPt.i + gapOffset < dough.side() && curPt.j - gapOffset >0 ) {
            Point targetPt = new Point(curPt.i + gapOffset, curPt.j - gapOffset);
            for (Move thisMv: prevDefMoves) {
                if ( thisMv.point.i == targetPt.i && thisMv.point.j == targetPt.j) {
                    // System.out.println(" =============== we found a match! ===============");
                    for (int i = 1; i < gapOffset; i++) {
                        for (Point p: shape) {
                            Point toBeAdded = new Point( p.i + curPt.i + i, p.j + curPt.j -i);
                            savedPoints.add(toBeAdded);
                        }
                    }
                    break; 
                }
            } 
        }
        return savedPoints;
    }
    public static boolean inSavedPoints(Shape shape, Point q, ArrayList<Point> savedPoints) {
        for ( Point thisSavedPt: savedPoints) {
            for ( Point p : shape) {
                if ((p.i + q.i) == thisSavedPt.i && (p.j + q.j) == thisSavedPt.j){
                    return true;
                }
            }
        }
        return false;
    }
    
    public static two_tuple getSize(Shape[] shapes, int size)
    {
    	int range_i, range_j, min_i, max_i, min_j, max_j;
		min_i = 10000; min_j = 10000;
		max_i = -1; max_j = -1;
		for (Shape s : shapes)
			if (s.size() == size)
			{
				for (Point p : s)
				{
					if (p.i > max_i) max_i = p.i;
					if (p.i < min_i) min_i = p.i;
					if (p.j > max_j) max_j = p.j;
					if (p.j < min_j) min_j = p.j;
				}
			}
		range_i = max_i - min_i + 1;
		range_j = max_j - min_j + 1;
		return new two_tuple(range_i, range_j);
    }
    
    public static int getGapOffset(Shape[] shapes, Shape[] opponent_shapes)
    {
    	/*
    	two_tuple range = getSize(opponent_shapes, 11);
    	int longLeg = Math.max(range.range_i, range.range_j);
    	if (longLeg >= 9) return 5 - (11-longLeg);
    	else return Math.min(range.range_i, range.range_j);
    	*/
    	
    	int gap = 6;
    	
    	Shape[] rotations = shapes[0].rotations();
    	Shape[] op_rotations = opponent_shapes[0].rotations();
        Shape s = rotations[2];
        
        while(gap>0)
        {
        	int side = 6 + gap;
        	Dough smallDough = new Dough(side);
        	int length = 2 * (side - 2*gap) - 1;
        	if(length>0)
        	{
        		Point[] smallCutter = new Point [length];
            	for(int i=0; i<length; i++)
            	{
            		if(i <= side - 2*gap - 1) smallCutter[i] = new Point(0, i);
            		else smallCutter[i] = new Point(i - (side - 2*gap - 1), side - 2*gap - 1);
            	}
            	Shape smallShape = new Shape(smallCutter);
            	if(smallCutter != null) smallDough.cut(smallShape, new Point(2*gap, 0));
        	}
        	boolean cutFlag = false;
        	smallDough.cut(s, new Point(0,side-6));
            smallDough.cut(s, new Point(side-6,0));
            
            loop:
            for(int i=0; i<side; i++)
            	for(int j=0; j<side; j++)
            		for(Shape os_s : op_rotations)
            			if(smallDough.cuts(os_s, new Point(i,j)))
                    	{
                    		gap--;
                    		cutFlag = true;
                    		break loop;
                    	}
            if(!cutFlag) return gap;
        }
        return 1;
    }
    
    
    public static Move getDefenseIndex(Dough dough, Shape[] shapes, Shape[] opponent_shapes, Point lastOppMove) {

        Shape[] rotations = shapes[0].rotations();
        Shape s = rotations[2];
        
        
        int gapOffset = getGapOffset(shapes, opponent_shapes);
        boolean flag4 = gapOffset == 4 ? true : false;
        
        ArrayList<Move> queueMoves = new ArrayList<Move>();
        
        
        // ---------- diagonal queue ----------
        Move firstQueue =  eachQueueMove(dough, s, gapOffset, 44, 1, false);
        if (firstQueue != null) queueMoves.add(firstQueue);

        // ---------- 2nd to diagonal queue ----------
        Move secondLeftQueue = eachQueueMove(dough, s, gapOffset, 33, 2, flag4);
        if (secondLeftQueue != null) queueMoves.add(secondLeftQueue);
        Move secondRightQueue = eachQueueMove(dough, s,  gapOffset, 55, 2, flag4);
        if (secondRightQueue != null) queueMoves.add(secondRightQueue);

        // ---------- 3rd to diagonal queue ----------
        Move thirdLeftQueue = eachQueueMove(dough, s,  gapOffset, 22, 3, flag4);
        if (thirdLeftQueue != null) queueMoves.add(thirdLeftQueue);
        Move thirdRightQueue = eachQueueMove(dough, s,  gapOffset, 66, 3, flag4);
        if (thirdRightQueue != null) queueMoves.add(thirdRightQueue);

        // ---------- 4th to diagonal queue ----------
        Move fourthLeftQueue = eachQueueMove(dough, s,  gapOffset, 11, 4, flag4);
        if (fourthLeftQueue != null) queueMoves.add(fourthLeftQueue);
        Move fourthRightQueue = eachQueueMove(dough, s,  gapOffset, 77, 4, flag4);
        if (fourthRightQueue != null) queueMoves.add(fourthRightQueue);

        if(queueMoves.size() > 0){
        	if(lastOppMove == null){
                // System.out.println("Opp move null");
            	return queueMoves.get(0);
        		
        	}
            Point placePoint;
            int minDistIndex = 0;
            float minDist = getDistBetweenPoints(lastOppMove, queueMoves.get(0).point);
            float dist;
        	for(int i = 0; i< queueMoves.size(); i++){
        		placePoint = queueMoves.get(i).point;
        		dist = getDistBetweenPoints(lastOppMove, placePoint);
        		if(dist < minDist){
        			minDist = dist;
        			minDistIndex = i;
        		}
        	}
            // System.out.println(minDistIndex);
        	return queueMoves.get(minDistIndex);
        }
        
        // ---------- filling in each queue ----------
        // Move fillGap = fillInQueueMove(dough, s);
        // if (fillGap != null) return fillGap;

        // System.out.println("No defensive move found.");
        return null;
    }

    public static float getDistBetweenPoints(Point a, Point b){
    	return (float)Math.sqrt(Math.pow(a.j-b.j,2) + Math.pow(a.i-b.i,2));
    }
}
