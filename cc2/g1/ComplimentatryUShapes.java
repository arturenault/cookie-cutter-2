package cc2.g1;

import java.util.Random;

import cc2.sim.Point;
import cc2.sim.Shape;

public class ComplimentatryUShapes {

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
				cutter=ShapesBasket.get11Shape_U();
				
			}else if(length==9){
				cutter=ShapesBasket.get8Shape_ThickI();
			}else if(length==5){
				//cutter=get5Shape_b();
				for (int i = 0 ; i != cutter.length ; ++i)
					cutter[i] = new Point(i, 0);
			}else{ //Standard Shapes from orig code
				for (int i = 0 ; i != cutter.length ; ++i)
					cutter[i] = new Point(i, 0);
			}
			
		} else {
			// pick a random cell from 2nd row but not same
			if(length==11 && shapeIndex[2] <1){
				if(shapeIndex[2]==0){
					cutter=ShapesBasket.get11Shape_BigU();
				}
				shapeIndex[2]=shapeIndex[2]+1;
			}else if(length==9 && shapeIndex[1] <1){
				if(shapeIndex[1]==0){
					cutter=ShapesBasket.get8Shape_I();
				}
				shapeIndex[1]=shapeIndex[1]+1;
			}else if(length==5 && shapeIndex[0] <3){
				if(shapeIndex[0]==0){
					cutter=ShapesBasket.get5Shape_l();
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
