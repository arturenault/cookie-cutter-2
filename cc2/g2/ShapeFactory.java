package cc2.g2;

import java.util.*;
import cc2.sim.Point;
import cc2.sim.Shape;

public class ShapeFactory {
	
	static int option = 0;
	static boolean diagonalFallback = true;

	static List<Shape> fives = makeShapes(5);
	static List<Shape> eights = makeShapes(8);
	static List<Shape> elevens = makeShapes(11);
	
	static int elevenChoice = -1;
	static int eightChoice = -1;
	
		
	public static void init() {
		fives = makeShapes(5);
		eights = makeShapes(8);
		elevens = makeShapes(11);
	}
	
	private static List<Shape> makeShapes(int length) {
		Point[] s1 = new Point[length];
		Point[] s2 = new Point[length];
		Point[] s3 = new Point[length];
		Point[] s4 = new Point[length];
		Point[] s5 = new Point[length];
		
		for(int i = 0; i < length-1; ++i) {
			s1[i] = new Point(i,0);
			s2[i] = new Point(i,0);
			s3[i] = new Point(i,0);
			s4[i] = new Point(i,0);
			s5[i] = new Point(i,0);
		}
		
		s1[length-1] = new Point(length-1, 0);
		s2[length-1] = new Point(0,1);
		s3[length-1] = new Point(length-2, 1);		
		s4[length-1] = new Point(length-3, 1);
		s5[length-1] = new Point(1, 1);
				
		if(diagonalFallback && length == 11) {
			for(int i = 0; i < length; ++i) {
				s2[i] = new Point(6-i/2, (i/2+i%2));
			}
		}
		
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		shapes.add(new Shape(s1));
		if(length == 11)
			shapes.add(new Shape(s2));
		shapes.add(new Shape(s3));
		if(length != 11)
			shapes.add(new Shape(s2));
		shapes.add(new Shape(s4));
		shapes.add(new Shape(s5));
		
		return shapes;
	}
	
	public static Shape getNext(int length) {
		List<Shape> arr = fives;
		if(length == 11)
			arr = elevens;
		else if(length == 8)
			arr = eights;

		Shape next = arr.get(0);
		arr.remove(0);
		if(diagonalFallback && length == 11) {
			++elevenChoice;
			if(elevenChoice == 1) {
				Point[] s2eight = new Point[8];
				for(int i = 0; i < s2eight.length; ++i) {
					s2eight[i] = new Point(i/2, i/2+i%2);
				}
				Point[] s1five = new Point[5];
				for(int i = 0; i < s1five.length; ++i) {
					s1five[i] = new Point(i/2, i/2+i%2);
				}
				eights.set(1, new Shape(s2eight));
				fives.set(0, new Shape(s1five));
				Player.tileDiagonal();
			}
		}
		if(length == 8 && elevenChoice == 1) {
			++eightChoice;
			if(eightChoice == 1) {
				Point[] s1= new Point[5];
				for(int i = 0; i < s1.length; ++i) {
					s1[i] = new Point(i, 0);
				}
				fives.set(0, new Shape(s1));
			}
		}
		return next;
	}
	
	public static List<Shape> makeBackupEights() {
		int length = 8;
		
		Point[] s1 = new Point[length];
		Point[] s2 = new Point[length];
		Point[] s3 = new Point[length];
		Point[] s4 = new Point[length];
		
		for(int i = 0; i < length; ++i) {
			s1[i] = new Point(i, 0);
			s2[i] = new Point(i%4, i/4);
			s3[i] = new Point(i%3, i/3);
			s4[i] = new Point(i%5, i/5);
		}
		
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		shapes.add(new Shape(s1));
		shapes.add(new Shape(s2));
		shapes.add(new Shape(s3));
		shapes.add(new Shape(s4));
		
		return shapes;
	}
	
//	public static Shape getFiveFromElevenHull(Shape opp_eleven) {
//		int minX, minY, maxX, maxY;
//		minX = minY = Integer.MAX_VALUE;
//		maxX = maxY = 0;
//		
//		for(Point p : opp_eleven) {
//			if(p.i < minX)
//				minX = p.i;
//			if(p.i > maxX)
//				maxX = p.i;
//		}
//		
//		return null;
//	}
	
}
