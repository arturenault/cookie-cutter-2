package cc2.g1;

import java.util.ArrayList;
import java.util.List;

import cc2.sim.Point;
import cc2.sim.Shape;

public class ShapesBasket {

	static Point[] generateDiag11( ){
		Point[] s = new Point[11];
		s[0] = new Point(5, 0);
		s[1] = new Point(5, 1);
		s[2] = new Point(4, 1);
		s[3] = new Point(4, 2);
		s[4] = new Point(3, 2);
		s[5] = new Point(3, 3);
		s[6] = new Point(2, 3);
		s[7] = new Point(2, 4);
		s[8] = new Point(1, 4);
		s[9] = new Point(1, 5);
		s[10] = new Point(0, 5);

		return s;
	}
	static Point[] generateDiag11_v2( ){
		Point[] s = new Point[11];
		s[0] = new Point(0, 0);
		s[1] = new Point(0, 1);
		s[2] = new Point(1, 1);
		s[3] = new Point(1, 2);
		s[4] = new Point(2, 2);
		s[5] = new Point(2, 3);
		s[6] = new Point(3, 3);
		s[7] = new Point(3, 4);
		s[8] = new Point(4, 4);
		s[9] = new Point(4, 5);
		s[10] = new Point(4, 6);

		return s;
	}
	public static Point[] mirrorImage(Point[] ps){
		List<Point> newPs=new ArrayList<Point>();
		int minX=49, newX;
		for(Point p : ps){
			newX=22-p.i;
			minX=Math.min(minX, newX);
			Point temp=new Point(newX,p.j);
			newPs.add(temp);
		}
		//shift X ?
		List<Point> returnPs=new ArrayList<Point>();
		for(Point p : newPs){
			returnPs.add(new Point(p.i-minX, p.j));
		}
		Point[] arr=new Point[returnPs.size()];
		return  returnPs.toArray(arr);
	}
	
	static Point[] generateDiag8(){
		Point[] shape = new Point[8];
		shape[0] = new Point(5, 0);
		shape[1] = new Point(4, 0);
		shape[2] = new Point(4, 1);
		shape[3] = new Point(3, 1);
		shape[4] = new Point(3, 2);
		shape[5] = new Point(2, 2);
		shape[6] = new Point(2, 3);
		shape[7] = new Point(1, 3);
		return  shape ;
	}
	static Point[] generateDiag8_v2(){
		Point[] shape = new Point[8];
		shape[0] = new Point(0, 0);
		shape[1] = new Point(1, 0);
		shape[2] = new Point(1, 1);
		shape[3] = new Point(2, 1);
		shape[4] = new Point(2, 2);
		shape[5] = new Point(3, 2);
		shape[6] = new Point(3, 3);
		shape[7] = new Point(3, 4);
		return  shape ;
	}
	public static Point[] get5Shape_b(){
		Point[] p5=new Point[5];
		/**  ---didnt work with U shape
		 *    X   
		 *    X X 
		 *    X X        
		 */
		p5[0]=new Point(0,1);
		p5[1]=new Point(1,0);
		p5[2]=new Point(2,0);
		p5[3]=new Point(1,1);
		p5[4]=new Point(2,1);
		return p5;
	}
	public static Point[] get5Shape_b_v2(){
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
	public static Point[] get5Shape_ZigZag(){
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
	
	public static  Point[] get5Shape_U(){
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
	
	public  static Point[] get5Shape_l(){
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
	public  static Point[] get5Shape_I(){
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
	
	public  static Point[] get8Shape_I(){
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
	public  static Point[] get8Shape_I_v2(){
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
		p7[7]=new Point(1,0);
		return p7;
	}
	public  static Point[] get8Shape_I_v3(){
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
		p7[7]=new Point(1,6);
		return p7;
	}
	public  static Point[] get8Shape_ThickI(){
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

	
	public  static Point[] get11Shape_I(){
		Point[] p7=new Point[11];
		/**  ---didint work with U shape
		 *    X 
		 *    X  
		 *    X        
		 */
		p7[0]=new Point(0,0);
		p7[1]=new Point(1,0);
		p7[2]=new Point(2,0);
		p7[3]=new Point(3,0);
		p7[4]=new Point(4,0);
		p7[5]=new Point(5,0);
		p7[6]=new Point(6,0);
		p7[7]=new Point(7,0);
		p7[8]=new Point(8,0);
		p7[9]=new Point(9,0);
		p7[10]=new Point(10,0);
		return p7;
	}
	
	public  static Point[] get11Shape_I_L(){
		Point[] p7=new Point[11];
		/**  ---didint work with U shape
		 *    X 
		 *    X  
		 *    X        
		 */
		p7[0]=new Point(0,0);
		p7[1]=new Point(1,0);
		p7[2]=new Point(2,0);
		p7[3]=new Point(3,0);
		p7[4]=new Point(4,0);
		p7[5]=new Point(5,0);
		p7[6]=new Point(6,0);
		p7[7]=new Point(7,0);
		p7[8]=new Point(8,0);
		p7[9]=new Point(9,0);
		p7[10]=new Point(9,1);
		return p7;
	}
	
	public  static Point[] get11Shape_I_L2(){
		Point[] p7=new Point[11];
		/**  ---didint work with U shape
		 *    X 
		 *    X  
		 *    X        
		 */
		p7[0]=new Point(0,0);
		p7[1]=new Point(1,0);
		p7[2]=new Point(2,0);
		p7[3]=new Point(3,0);
		p7[4]=new Point(4,0);
		p7[5]=new Point(5,0);
		p7[6]=new Point(6,0);
		p7[7]=new Point(7,0);
		p7[8]=new Point(8,0);
		p7[9]=new Point(9,0);
		p7[10]=new Point(0,1);
		return p7;
	}
	public  static Point[] get11Shape_I_v2(){
		Point[] p7=new Point[11];
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
		p7[8]=new Point(0,8);
		p7[9]=new Point(0,9);
		p7[10]=new Point(1,9);
		return p7;
	}
 
	public static  Point[] get11Shape_U(){
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
	public static  Point[] get11Shape_BigU(){
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

	public static Point[] getShape_I_Flexible(int length, int offset) {

		Point[] p7 = new Point[length];
		for (int i = 0; i < length-1; i++) {
			p7[i] = new Point(i, 0);
		}
		p7[length-1] = new Point(offset, 1);
		return p7;
	}
}
