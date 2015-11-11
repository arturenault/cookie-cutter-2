package cc2.g1;

import cc2.sim.Point;
import cc2.sim.Shape;

public class ShapesBasket {

	static Shape generateDiag11( ){
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
		s[10] = new Point(5, 5);

		return new Shape(s);
	}
	
	  static Shape generateDiag8(){
		Point[] shape = new Point[8];
		shape[0] = new Point(0, 0);
		shape[1] = new Point(1, 0);
		shape[2] = new Point(1, 1);
		shape[3] = new Point(2, 1);
		shape[4] = new Point(2, 2);
		shape[5] = new Point(3, 2);
		shape[6] = new Point(3, 3);
		shape[7] = new Point(4, 3);
		return new Shape(shape);
	}

	public static Point[] get5Shape_b(){
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
}
