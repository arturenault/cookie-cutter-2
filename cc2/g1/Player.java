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
	DiagonalShapes dg;
	
	public Player(){
		 dg=new DiagonalShapes();
	}
	
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		return dg.cutter(  length,  shapes,  opponent_shapes);
	}

	
	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		//AggressiveStrategy strategy=new AggressiveStrategy();
		AggressiveSpaceStrategy strategy=new AggressiveSpaceStrategy();
		return strategy.cut(  dough,  shapes,  opponent_shapes);
		
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
