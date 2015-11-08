package cc2.g2;

import java.util.*;
import cc2.sim.Point;
import cc2.sim.Shape;

public class ShapeFactory {
	
	//static HashMap<Integer, ArrayList<Shape>> options;
	static int option = 0;
	static List<Shape> fives = makeShapes(5);
	static List<Shape> eights = makeShapes(8);
	static List<Shape> elevens = makeShapes(11);
	
	public static void init() {
		fives = makeShapes(5);
		eights = makeShapes(8);
		elevens = makeShapes(11);
		
/*		//Option 0
		Point[] cutter = new Point[11];
		for (int i = 0 ; i != cutter.length ; ++i) {
			cutter[i] = new Point(i, 0);
		}
		Shape eleven = new Shape(cutter);
		
		cutter = new Point[8];
		for (int i = 0 ; i != cutter.length ; ++i) {
			cutter[i] = new Point(i, 0);
		}
		Shape eight = new Shape(cutter);
		
		cutter = new Point[5];
		for (int i = 0 ; i != cutter.length ; ++i) {
			cutter[i] = new Point(i, 0);
		}
		Shape five = new Shape(cutter);
		ArrayList<Shape> shapes = new ArrayList<>();
		shapes.add(eleven);
		shapes.add(eight);
		shapes.add(five);
		options.put(option, shapes);
		//incrememnt options
*/
	}
	
	private static List<Shape> makeShapes(int length) {
		Point[] s1 = new Point[length];
		Point[] s2 = new Point[length];
		Point[] s3 = new Point[length];
		
		for(int i = 0; i < length-1; ++i) {
			s1[i] = new Point(i,0);
			s2[i] = new Point(i,0);
			s3[i] = new Point(i,0);
		}
		
		s1[length-1] = new Point(length-1, 0);
		s2[length-1] = new Point(0,1);
		s3[length-1] = new Point(length-2, 1);
		
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		shapes.add(new Shape(s1));
		shapes.add(new Shape(s2));
		shapes.add(new Shape(s3));
		
		for(Shape s : shapes)
			System.out.println(s);
		
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
		return next;
	}
	
/*	public static List<Shape> getShape() {
		List<Shape> nextOption = options.get(option);
		option++;
		return nextOption;
	}
*/
}
