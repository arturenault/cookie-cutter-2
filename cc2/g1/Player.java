package cc2.g1;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

	private boolean[] row_2 = new boolean [0];
	
	private Random gen = new Random();
	int[] shapeIndex = new int[3];
	
	public Point[] get5Shape_b(){
		Point[] p5=new Point[5];
		/**  ---didnt work with U shape
		 *    X   
		 *    X X 
		 *    X X        
		 */
		p5[0]=new Point(0,0);
		p5[1]=new Point(1,0);
		p5[2]=new Point(2,0);
		p5[3]=new Point(1,1);
		p5[4]=new Point(2,1);
		return p5;
	}
	
	public Point[] get5Shape_ZigZag(){
		Point[] p5=new Point[5];
		/**  ---didint work with U shape
		 *    X X  
		 *      X X 
		 *        X        
		 */
		p5[0]=new Point(0,0);
		p5[1]=new Point(0,1);
		p5[2]=new Point(1,1);
		p5[3]=new Point(1,2);
		p5[4]=new Point(2,2);
		return p5;
	}
	
	public Point[] get5Shape_U(){
		Point[] p5=new Point[5];
		/**  ---didint work with U shape
		 *    X X  
		 *    X  
		 *    X X        
		 */
		p5[0]=new Point(0,0);
		p5[1]=new Point(0,1);
		p5[2]=new Point(1,0);
		p5[3]=new Point(2,0);
		p5[4]=new Point(2,1);
		return p5;
	}
	
	public Point[] get5Shape_l(){
		Point[] p5=new Point[5];
		/**  ---didint work with U shape
		 *    X X X 
		 *    X  
		 *    X        
		 */
		p5[0]=new Point(0,0);
		p5[1]=new Point(0,1);
		p5[2]=new Point(0,2);
		p5[3]=new Point(1,0);
		p5[4]=new Point(2,0);
		return p5;
	}
	public Point[] get5Shape_I(){
		Point[] p5=new Point[5];
		/**  ---didint work with U shape
		 *    X 
		 *    X  
		 *    X        
		 */
		p5[0]=new Point(0,0);
		p5[1]=new Point(0,1);
		p5[2]=new Point(0,2);
		p5[3]=new Point(0,3);
		p5[4]=new Point(0,4);
		return p5;
	}
	
	public Point[] get8Shape_I(){
		Point[] p7=new Point[8];
		/**  ---didint work with U shape
		 *    X 
		 *    X  
		 *    X        
		 */
		p7[0]=new Point(0,0);
		p7[1]=new Point(0,1);
		p7[2]=new Point(0,2);
		p7[3]=new Point(0,3);
		p7[4]=new Point(0,4);
		p7[5]=new Point(0,5);
		p7[6]=new Point(0,6);
		p7[7]=new Point(0,7);
		return p7;
	}
	public Point[] get8Shape_ThickI(){
		Point[] p8=new Point[8];
		/**  ---didint work with U shape
		 *    X 
		 *    X  
		 *    X        
		 */
		p8[0]=new Point(0,0);
		p8[1]=new Point(0,1);
		p8[2]=new Point(0,2);
		p8[3]=new Point(0,3);
		p8[4]=new Point(1,0);
		p8[5]=new Point(1,1);
		p8[6]=new Point(1,2);
		p8[7]=new Point(1,3);
		return p8;
	}
	
	public Point[] get11Shape_U(){
		Point[] p11=new Point[11];
		/**
		 *    X   X X
		 *    X     X
		 *    X     X
		 *    X X X X
		 *    
		 */
		p11[0]=new Point(0,0);
		p11[1]=new Point(1,0);
		p11[2]=new Point(2,0);
		p11[3]=new Point(3,0);
		p11[4]=new Point(3,1);
		
		p11[5]=new Point(3,2);
		p11[6]=new Point(3,3);
		p11[7]=new Point(2,3);
		p11[8]=new Point(1,3);
		p11[9]=new Point(0,3);
		p11[10]=new Point(0,2);	
		return p11;
	}
	public Point[] get11Shape_BigU(){
		/**
		 * 	  X X
		 *    X     
		 *    X     X
		 *    X     X
		 *    X X X X
		 *    
		 */
		 Point[] p11={ 
			 new Point(0,0),
			 new Point(1,0),
			 new Point(2,0),
			 new Point(3,0),
			 new Point(4,0),
			
			 new Point(0,1),
			
			 new Point(4,1),
			 new Point(4,2),
			 new Point(4,3),
			 new Point(3,3),
			 new Point(2,3) 
		};
			
		return p11;
	}
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		// check if first try of given cutter length
		Point[] cutter = new Point [length];
		if (row_2.length != cutter.length - 1) {
			// save cutter length to check for retries
			row_2 = new boolean [cutter.length - 1];
			if(length==11){
				cutter=get11Shape_U();
			}else if(length==9){
				cutter=get8Shape_ThickI();
			}else if(length==5){
				cutter=get5Shape_b();
			}else{ //Standard Shapes from orig code
				for (int i = 0 ; i != cutter.length ; ++i)
					cutter[i] = new Point(i, 0);
			}
			
		} else {
			// pick a random cell from 2nd row but not same
			if(length==11 && shapeIndex[2] <1){
				if(shapeIndex[2]==0){
					cutter=get11Shape_BigU();
				}
				shapeIndex[2]=shapeIndex[2]+1;
			}else if(length==9 && shapeIndex[1] <1){
				if(shapeIndex[1]==0){
					cutter=get8Shape_I();
				}
				shapeIndex[1]=shapeIndex[1]+1;
			}else if(length==5 && shapeIndex[0] <3){
				if(shapeIndex[0]==0){
					cutter=get5Shape_b();
				}else if(shapeIndex[0]==1){
					cutter=get5Shape_l();
				}else{
					cutter=get5Shape_U();
				}
				shapeIndex[0]=shapeIndex[0]+1;
			}
			else{
				int i;
				do {
					i = gen.nextInt(cutter.length - 1);
				} while (row_2[i]);
				row_2[i] = true;
				cutter[cutter.length - 1] = new Point(i, 1);
				for (i = 0 ; i != cutter.length - 1 ; ++i)
					cutter[i] = new Point(i, 0);
			}
		}
		return new Shape(cutter);
	}

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
	
	static int[] dx = {-1,0,0,1};
    static int[] dy = {0,1,-1,0};
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
