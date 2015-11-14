package cc2.g1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cc2.sim.Point;
import cc2.sim.Shape;

public class KostaShapes {
	private boolean[] row_2 = new boolean [0];
	
	private Random gen = new Random();
	int[] shapeIndex = new int[3];
	static List<Point[]>  all11 = new ArrayList<Point[]>();
	{
		all11.add(ShapesBasket.get11Shape_I());
		all11.add(ShapesBasket.get11Shape_I_L());
		all11.add(ShapesBasket.get11Shape_I_L2());
        all11.add(ShapesBasket.getShape_I_Flexible(11, 8));
        all11.add(ShapesBasket.getShape_I_Flexible(11, 1));
//		for(int i=1;i<9;i++){
//			all11.add(ShapesBasket.getShape_I_Flexible(11, i));
//		}
		all11.add(ShapesBasket.generateDiag11());
		all11.add(ShapesBasket.generateDiag11_v2());
	}
	
	static List<Point[]>  all8 = new ArrayList<Point[]>();
	{
		//all8.add(ShapesBasket.get11Shape_I());  ShapesBasket.generateDiag8( )
		all8.add(ShapesBasket.get8Shape_I());
		all8.add(ShapesBasket.get8Shape_I_v2());
		all8.add(ShapesBasket.get8Shape_I_v3());
        all8.add(ShapesBasket.getShape_I_Flexible(8,5));
        all8.add(ShapesBasket.getShape_I_Flexible(8,1));
//		for(int i=1;i<6;i++){
//			all8.add(ShapesBasket.getShape_I_Flexible(8,i));
//		}
		all8.add(ShapesBasket.generateDiag8());
		all8.add(ShapesBasket.generateDiag8_v2());
	}
	
	 
	
	static List<Point[]>  all5= new ArrayList<Point[]>();
	{   all5.add(ShapesBasket.get5Shape_b());
		all5.add(ShapesBasket.get5Shape_I());
		all5.add(ShapesBasket.get5Shape_b_v2());
		
		for(int i=1;i<3;i++){
			all5.add(ShapesBasket.getShape_I_Flexible(5,i));
		}
	}
	
	static List<Point[]>  all5Lines= new ArrayList<Point[]>();
	{  
		all5Lines.add(ShapesBasket.get5Shape_I());
		
		all5Lines.add(ShapesBasket.getShape_I_Flexible(5,3));
		all5Lines.add(ShapesBasket.getShape_I_Flexible(5,0));
		for(int i=2;i>=0;i--){
			all5Lines.add(ShapesBasket.getShape_I_Flexible(5,i));
		}
		all5Lines.add(ShapesBasket.get5Shape_b());
		all5Lines.add(ShapesBasket.get5Shape_b_v2());
	}
	
	static Map<Integer, List<Point[]>>  len2shape = new HashMap<Integer, List<Point[]>>();
	{
		len2shape.put(11, all11 );
		len2shape.put(8, all8 );
		len2shape.put(5,all5Lines  );
		len2shape.put(4, all5 );
	}
	
	int getLengthToIndex(int length){
		return length==11?2: length==8?1:0;
	}
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		// check if first try of given cutter length
		Point[] cutter = new Point [length];
		
		int index=getLengthToIndex(length);
	/*	if(length==5 && isOpponentUsingLines(opponent_shapes)){
			length=length-1;
		}*/
		if(shapeIndex[index]<len2shape.get(length).size() ){
			Point[] p=len2shape.get(length).get(shapeIndex[index]);
			shapeIndex[index]+=1;
			return new Shape(p);
		}else{
			int i;
			do {
				i = gen.nextInt(cutter.length - 1);
			} while (row_2[i]);
			row_2[i] = true;
			cutter[cutter.length - 1] = new Point(i, 1);
			for (i = 0 ; i != cutter.length - 1 ; ++i)
				cutter[i] = new Point(i, 0);
		}
		return new Shape(cutter);

	}

}
