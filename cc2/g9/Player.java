package cc2.g9;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;
import static java.lang.Math.*;

public class Player implements cc2.sim.Player {

	private boolean[] row_2 = new boolean [0];

	private Random gen = new Random();
	
	private int firstTry11 = 0;
	private int firstTry8 = 0;
	private int firstTry5 = 0;

	private boolean defensive = false;
	
	
	
	public boolean decideStrategy(Shape[] opponent_shapes)
	{
		int range_i, range_j, min_i, max_i, min_j, max_j;
		min_i = 10000; min_j = 10000;
		max_i = -1; max_j = -1;
		for (Shape s : opponent_shapes)
			if (s.size() == 11)
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
		// the square shape (1st chose)
		if (firstTry11 == 1)
		{
			// if (range_i <= 4 && range_j <= 4) return true;
			if (range_i <= 5 && range_j <= 5) return true;
			else return false;
		}
		else
		{
			if ((range_i <= 5 && range_j <= 4) || (range_i <= 4 && range_j <= 5)) return true;
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
					else cutter[i] = new Point(i-4,1);
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
				row_2 = new boolean [cutter.length - 1];
				for (int i = 0 ; i != cutter.length ; ++i)
					cutter[i] = new Point(i, 0);
				firstTry5++;
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
				firstTry5++;
			}
		}
		return new Shape(cutter);
	}

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
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
		ArrayList <Move> moves = new ArrayList <Move> ();
		ArrayList <Move> moves11 = new ArrayList <Move> ();
		ArrayList <Move> moves8 = new ArrayList <Move> ();
		ArrayList <Move> moves5 = new ArrayList <Move> ();
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
							if(shapes[si].size() == 11 && (ri == 0 || ri == 2)){
								moves11.add(new Move(si,ri,p));
							}
							else if(shapes[si].size() == 8){
								moves8.add(new Move(si,ri,p));
							}
							else{
								moves5.add(new Move(si,ri,p));
							}
						}
					}
				}
			}
		// return a cut randomly
		if(moves11.size()>0){
			if(defensive == false){
				int returnIndex = find11SpotCornerStrategy(dough, moves11, shapes);
				//System.out.println(returnIndex);
				if(returnIndex >= 0){
					return moves11.get(returnIndex);				
				}

			}			
			else {
				Move defenseMv = Utils.getDefenseIndex(dough, shapes);
				if (defenseMv != null) return defenseMv;
			}
			Move thisMv = moves11.get(gen.nextInt(moves11.size()));
			// System.out.println("Just moved shape, rotation, point: " + thisMv.shape + ", " + thisMv.rotation + ", " + thisMv.point);
			return thisMv;
		}
		else if(moves8.size()>0){
			return moves8.get(gen.nextInt(moves8.size()));
		}
		else {
			return moves5.get(gen.nextInt(moves5.size()));
		}
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
	
}