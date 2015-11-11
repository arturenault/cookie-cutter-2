package cc2.g2;

import java.util.*;
import cc2.sim.Point;
import cc2.sim.Shape;

public class ShapeFactory {
	
	static int option = 0;
	static List<Shape> fives = makeShapes(5);
	static List<Shape> eights = makeShapes(8);
	static List<Shape> elevens = makeShapes(11);
	
	static int elevenChoice = -1;
		
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
		if(length == 11) {
			//++elevenChoice;
			if(elevenChoice == 1) {
				eights = makeBackupEights();
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
	
}
