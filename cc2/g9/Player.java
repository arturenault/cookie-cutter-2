package cc2.g9;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.g9.Utils;

import java.util.*;
import static java.lang.Math.*;

public class Player implements cc2.sim.Player {

	private boolean[] row_2 = new boolean [0];

	private Random gen = new Random();
	
	private int firstTry11 = 0;
	private int firstTry8 = 0;
	private int firstTry5 = 0;

	private boolean defensive = false;
	
	public boolean board[][] = null;
	public Point lastOppPlay;

	public ArrayList <Move> prevDefMoves = new ArrayList <Move> ();
	public ArrayList <Point> savedPoints = new ArrayList <Point> ();
	
	
	public boolean decideStrategy(Shape[] opponent_shapes)
	{
		two_tuple range = Utils.getSize(opponent_shapes, 11);
		
		// the square shape (1st chose)
		if (firstTry11 == 1)
		{
			// if (range_i <= 4 && range_j <= 4) return true;
			if (range.range_i <= 5 && range.range_j <= 5) return true;
			else return false;
		}
		else
		{
			if ((range.range_i <= 5 && range.range_j <= 4) || (range.range_i <= 4 && range.range_j <= 5)) return true;
			else return false;
		}
		
	}
	
	
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		// check if first try of given cutter length
		Point[] cutter = new Point [length];
		///////// 11 shape //////////
		if (length == 11)
		{
			int half;
			switch(firstTry11)
			{
			case 0: 
				half = length / 2+1;
				for(int i=0; i<length; i++)
				{
					if (i < half) cutter[i] = new Point(i, 0);
					else cutter[i] = new Point(half-1, i-(half-1));
				}
				firstTry11++;
				break;
			case 1:
				half = length / 2 ;
				for(int i=0; i<length; i++)
				{
					if (i < half) cutter[i] = new Point(i, 0);
					else cutter[i] = new Point(half-1, i-(half-1));
				}
				firstTry11++;
				break;
			case 2:
				half = length / 2 ;
				for(int i=0; i<length; i++)
				{
					if (i < half) cutter[i] = new Point(i, 0);
					else cutter[i] = new Point(0, i-(half-1));
				}
				firstTry11++;
				break;
			}
		}
		///////// 8 shape //////////
		if (length == 8)
		{
			if (firstTry8 == 0)
			{
				for(int i=0; i<length; i++)
					if (i<4) cutter[i] = new Point(i,0);
					else cutter[i] = new Point(i-3,1);
				firstTry8++;
			}
			else
			{
				switch(firstTry8)
				{
				case 1: 
					for(int i=0; i<length; i++)
					if (i<5) cutter[i] = new Point(i,0);
					else cutter[i] = new Point(i-5,1);
					firstTry8++;
					break;
				case 2:
					for(int i=0; i<length; i++)
					if (i<5) cutter[i] = new Point(i,0);
					else cutter[i] = new Point(i-4,1);
					firstTry8++;
					break;
				case 3:
					for(int i=0; i<length; i++)
					if (i<5) cutter[i] = new Point(i,0);
					else cutter[i] = new Point(i-3,1);
					firstTry8++;
					break;
				}
			}
		}
		///////// 5 shape //////////
		if (length == 5)
		{
			if (firstTry5 == 0) {
				// save cutter length to check for retries
				
				Point[] counterShape = getCounterTo4x4Square(opponent_shapes);
				// System.out.println(counterShape );
				
				if(counterShape != null){
					cutter = counterShape;
				}
				else{
					for (int i = 0 ; i != cutter.length ; ++i)
						cutter[i] = new Point(i, 0);
					
				}

				row_2 = new boolean [cutter.length - 1];

				firstTry5++;
			} else if (firstTry5 == 1 ) {
				
				cutter[cutter.length - 1] = new Point(0, 1);
				// System.out.println("Is same cutter?");
				// System.out.println(cutter[cutter.length - 1].i + ", " + cutter[cutter.length - 1].j );
				
				for (int i = 0 ; i != cutter.length - 1 ; ++i){
					cutter[i] = new Point(i, 0);
					
					// System.out.println(cutter[i].i + ", " + cutter[i].j );
				}
				firstTry5++;
			} else {
				
				cutter[cutter.length - 1] = new Point(3, 1);
				// System.out.println("Is same cutter?");
				// System.out.println(cutter[cutter.length - 1].i + ", " + cutter[cutter.length - 1].j );
				
				for (int i = 0 ; i != cutter.length - 1 ; ++i){
					cutter[i] = new Point(i, 0);
					
					// System.out.println(cutter[i].i + ", " + cutter[i].j );
				}
				firstTry5++;
			}
		}
		System.out.println();
		for (int i = 0 ; i != cutter.length ; ++i){
			// System.out.println(cutter[i].i + ", " + cutter[i].j );
		}
		System.out.println();

		return new Shape(cutter);
	}

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{	
		if (dough.countCut() ==0) {
			Point startPt = new Point(45, 0);
			Move startMv = new Move(2, 0, startPt);
			if (dough.cuts(shapes[2].rotations()[0], startPt)){
				return startMv;
			} 
		}
		Move returnMove = null;
		if(board == null){
			board = new boolean[dough.side()][dough.side()];
		}
		lastOppPlay = getLastOppPlay(dough);
		defensive = decideStrategy(opponent_shapes);
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
		ArrayList <Move> moves11 = new ArrayList <Move> ();
		ArrayList <Move> moves8 = new ArrayList <Move> ();
		ArrayList <Move> moves5 = new ArrayList <Move> ();
		ArrayList <Move> moves = new ArrayList <Move> ();
		for (int i = 0 ; i != dough.side() ; ++i)
			for (int j = 0 ; j != dough.side() ; ++j) {
				Point p = new Point(i, j);
				for (int si = 0 ; si != shapes.length ; ++si) {
					if (shapes[si] == null) continue;
					// rotations are clockwise
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length ; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p)){
							// System.out.println("This shape is not in saved Points.");
							if(shapes[si].size() == 11&& !Utils.inSavedPoints(s, p, savedPoints)){
								moves11.add(new Move(si,ri,p));
							}
							else if(shapes[si].size() == 8){
								moves8.add(new Move(si,ri,p));
							}
							else if(shapes[si].size() == 5){
								moves5.add(new Move(si,ri,p));
							}
							moves.add(new Move(si,ri,p));
						}
					}
				}
			}
		// return a cut randomly
		if(moves11.size()>0){
			int gapOffset = Utils.getGapOffset(shapes, opponent_shapes);
			Move defenseMv = Utils.getDefenseIndex(dough, shapes, opponent_shapes, lastOppPlay);

			if (defenseMv != null)
			{
				// if current defense move is next to previous, save in between moves for later.
				savedPoints = Utils.savePointsForLater(defenseMv, prevDefMoves, savedPoints, gapOffset, dough, shapes[0]);
				prevDefMoves.add(defenseMv);
				returnMove = defenseMv;
			} else {
				Move thisMv = moves11.get(gen.nextInt(moves11.size()));
				returnMove = thisMv;
			}
		} else if (moves8.size()>0) {
			Move fillInMv = Utils.fillInQueueMove(dough, shapes);
			if ( fillInMv != null ) {
				returnMove = fillInMv;
			} else {
				returnMove = moves8.get(gen.nextInt(moves8.size()));	
			}
		}
		else if (moves5.size()>0){
			returnMove = moves5.get(gen.nextInt(moves5.size()));
		} else {
			returnMove = moves.get(gen.nextInt(moves.size()));
		}
		
		if(returnMove != null){
			updateBoardMyMove(shapes, returnMove);
		}
		
		return returnMove;
	}
	
	public int find11SpotCornerStrategy(Dough dough, ArrayList<Move> moves11, Shape[] shapes){
		Move cur;
		Point[] boardPoints = new Point[11];
		boolean goodCut;
		int backupCut = -1;
		for(int z = 0; z<moves11.size(); z++){
			goodCut = true;
			cur = moves11.get(z);
			int index = 0;			
			for (Point p : shapes[cur.shape]){
				boardPoints[index] = new Point(p.i + cur.point.i, p.j + cur.point.j);
				index++;
			}
			boardPoints = sortPoints(boardPoints);
			for(int i = 0; i<boardPoints.length; i++){
		//		System.out.println(boardPoints[i].i + ", " + boardPoints[i].j);
			}
//			System.out.println();
			Point[] corners = getCorners(boardPoints);
			
			for(int i = Math.min(corners[0].i, corners[1].i); i <= Math.max(corners[0].i, corners[1].i); i++){
				for(int j = Math.min(corners[0].j, corners[1].j); j <= Math.max(corners[0].j, corners[1].j); j++){
					if(i == -1 || j == -1 || i == dough.side() || j == dough.side()){
						continue;
					}
					else if(i < -1 || j < -1 || i > dough.side() || j > dough.side()){
						goodCut = false;
//						 System.exit(0);
						break;
					}
					else if(dough.uncut(i,j) == false){
						goodCut = false;
	//					 System.exit(0);
						break;
					}										
				}
				if(goodCut == false){
					break;
				}				
			}
			

			if(goodCut == true){
				backupCut = z;
//				return z;
			}

			//
			int extendI = (corners[0].i > corners[2].i)? corners[0].i + 1 : corners[0].i - 1;
			int extendJ = (corners[1].j > corners[2].j)? corners[1].j + 1 : corners[1].j - 1;
			if(extendI > 0 && extendI < dough.side()){
				for(int j = Math.min(corners[2].j, extendJ); j <= Math.max(corners[2].j, extendJ); j++){
					if(j < 0 || j >= dough.side()){
						continue;
					}
					else if(dough.uncut(extendI,j) == true){
						goodCut = false;
					}
				}
			}
			if(goodCut == false){
				continue;
			}
			if(extendJ > 0 && extendJ < dough.side()){
				for(int i = Math.min(corners[2].i, extendI); i <= Math.max(corners[2].i, extendI); i++){
					if(i < 0 || i >= dough.side()){
						continue;
					}
					else if(dough.uncut(i,extendJ) == true){
						goodCut = false;
					}
				}
			}
			if(goodCut == true){
				return z;
			}
		}

		return backupCut;
	}
	
	public Point[] sortPoints(Point[] points){
		ArrayList <Point> pointList = new ArrayList <Point> ();
		for(int i = 0; i<points.length; i++){
			pointList.add(points[i]);
		}
		ArrayList <Point> pointSorted = new ArrayList<Point>();
		while(pointList.size() > 0){
			int index = 0;
			int minI;
			int minJ;
			for(int i = 0; i< pointList.size(); i++){
				Point p = pointList.get(i);
				if(p.i < pointList.get(index).i){
					index = i;
				}
				else if(p.i == pointList.get(index).i){
					if(p.j < pointList.get(index).j){
						index = i;
					}
				}
			}
			pointSorted.add(pointList.get(index));
			pointList.remove(index);
		}
		
		for(int i = 0; i < points.length; i++){
			points[i] = pointSorted.get(i);
		}
		
		return points;
	}
	
	// corners: {end of horizontal leg, end of vertical leg, corner}
	public Point[] getCorners(Point[] points){
		Point[] corners = new Point[3];
		int index; 
		if(points[1].i > points[0].i){
			if(points[points.length - 1].j == points[0].j){
				corners[0] = points[0];
				corners[2] = points[points.length - 1];
				for(int i = 0; i < points.length; i++){
					if(points[i].j != points[0].j){
						corners[1] = points[i];
						break;
					}
				}
			}
			else{
				corners[0] = points[0];
				corners[1] = points[points.length - 1];
				for(int i = 0; i < points.length; i++){
					if(points[i].j != points[0].j){
						corners[2] = points[i - 1];
						break;
					}					
				}
			}
		}
		else{
			if(points[points.length - 1].j == points[0].j){
				corners[2] = points[0];
				corners[0] = points[points.length - 1];
				for(int i = 0; i < points.length; i++){
					if(points[i].i != points[0].i){
						corners[1] = points[i - 1];
						break;
					}
				}
			}			
			else{
				corners[1] = points[0];
				corners[0] = points[points.length - 1];
				for(int i = 0; i < points.length; i++){
					if(points[i].i != points[0].i){
						corners[2] = points[i - 1];
						break;
					}					
				}
			}
		}
		
		return corners;
	}

	public Point[] getCounterTo4x4Square(Shape[] opponent_shapes){
		Shape opp11 = null;
		for(int i = 0; i<opponent_shapes.length; i++){
			if(opponent_shapes[i].size() == 11){
				opp11 = opponent_shapes[i];
				break;
			}
		}
		if(opp11 == null){
			return null;
		}
		
		int minI = Integer.MAX_VALUE;
		int maxI = Integer.MIN_VALUE;
		int minJ = Integer.MAX_VALUE;
		int maxJ = Integer.MIN_VALUE;
		
		for (Point p : opp11){
			if(p.i < minI)
				minI = p.i;
			if(p.i > maxI)
				maxI = p.i;
			if(p.j < minJ)
				minJ = p.j;
			if(p.j > maxJ)
				maxJ = p.j;
		}
		if((maxI - minI == 3 && maxJ - minJ == 3) == false){
			return null;
		}
		System.out.println("IS MATCH");
		
		boolean[][] box = new boolean[4][4];
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				Point curr = new Point(i+minI, j+minJ);
				for (Point p : opp11){
					if(curr.equals(p)){
						box[i][j] = true;
						break;
					}
				}
			}
		}
		
		Point[] counterShape = new Point[5];
		int index = 0;

		for(int i = 0; i< 4; i++){
			for(int j = 0; j < 4; j++){
				if(box[i][j] == false){
					System.out.println("Add: " + i + ", " + j);

					counterShape[index] = new Point(i+minI, j+minJ);
					index++;
				}
			}
		}
				
		return counterShape;
	}
	
	// Updates board and returns location of a single point from opponent's last move
	public Point getLastOppPlay(Dough dough){
		Point oppMove = null;
		for(int i = 0; i < dough.side(); i++){
			for(int j = 0; j < dough.side(); j++){
				if(board[i][j] == false && dough.uncut(i,j) == false){
					oppMove = new Point(i,j);
					board[i][j] = true;
				}
			}
		}
		
		// if opponent didn't move last move, return null
		return oppMove;
	}
	
	public void updateBoardMyMove(Shape[] shapes, Move myMove){
		for (Point p : shapes[myMove.shape]){
			board[p.i][p.j] = true;
		}
	}

}
