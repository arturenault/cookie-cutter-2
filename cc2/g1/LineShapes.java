package cc2.g1;

import java.util.Random;

import cc2.sim.Point;
import cc2.sim.Shape;

public class LineShapes {


	private boolean[] row_2 = new boolean [0];
	
	private Random gen = new Random();
	int[] shapeIndex = new int[3];
	
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		// check if first try of given cutter length
		Point[] cutter = new Point [length];
		if (row_2.length != cutter.length - 1) {
			// save cutter length to check for retries
			row_2 = new boolean [cutter.length - 1];
			if(length==11){
				return new Shape(ShapesBasket.get11Shape_I());
			
			}else if(length==8){
				return new Shape(ShapesBasket.get8Shape_I_v2());
				
			}else if(length==5){
				 cutter=ShapesBasket.get5Shape_b();//get5Shape_ZigZag();
				 
			}else{ //Standard Shapes from orig code
				for (int i = 0 ; i != cutter.length ; ++i)
					cutter[i] = new Point(i, 0);
			}
			
		} else {
			// pick a random cell from 2nd row but not same
			/*if(length==11 && shapeIndex[2] <2){
				Shape shape= new Shape(ShapesBasket.all11.get(shapeIndex[2]));
				shapeIndex[2]=shapeIndex[2]+1;
				return shape;
			}else */if(length==8 && shapeIndex[1] <1){
				if(shapeIndex[1]==0){
					return new Shape(ShapesBasket.generateDiag8( ));
				}
				shapeIndex[1]=shapeIndex[1]+1;
			}else if(length==5 && shapeIndex[0] <3){
				if(shapeIndex[0]==0){
					cutter=ShapesBasket.get5Shape_b_v2();
				}else if(shapeIndex[0]==1){
					cutter=ShapesBasket.get5Shape_U();
				}else{
					cutter=ShapesBasket.get5Shape_b();
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

}
