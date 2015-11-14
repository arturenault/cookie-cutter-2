package cc2.g1;

import java.util.ArrayList;
import java.util.List;

/*import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
*/
import cc2.sim.Point;
import cc2.sim.Shape;

public class ShapesParser {
	public static void main(String args[]){
		String input="[[0,0],[1,0],[2,0],[3,0],[3,1],[3,2],[3,3],[2,3],[1,3],[0,3],[0,2]],[[0,0],[1,0],[2,0],[3,0],[4,0],[5,0],[6,0],[7,0]],[[0,0],[1,0],[2,0],[1,1],[2,1]]";
		getShapes(input);
	}

	
	public static Shape[] getShapes(String input){
		int startIndex=0;
		Shape[] sh=new Shape[3];
		for(int i=0;i<3;i++){
			int indexOfEndOfShape=input.indexOf("]]",startIndex )+2;
			String s=input.substring(startIndex, indexOfEndOfShape);
			//remove end Comma
			if(s.endsWith(",")){
				s=s.substring(0, s.length()-1);
			}
			System.out.println(s);
			sh[i]=getShapeFromStr(s);
			startIndex=indexOfEndOfShape+1;
		}
		return sh;
	}
	
	public static Shape getShapeFromStr(String input){
		List<Point> p=new ArrayList<Point>();
		//[[0,0],[1,0],[2,0],[3,0],[3,1],[3,2],[3,3],[2,3],[1,3],[0,3],[0,2]]
		 String[] sss=input.replace("[[","[").replace("]]", "]").replace("[","").split("],");
         for(int i=0;i<sss.length; i++){
	        	  String[] pointCoOrds=  sss[i].replace("]","").split(",");
	        	 p.add(new Point(Integer.parseInt(pointCoOrds[0]),Integer.parseInt(pointCoOrds[1])));
         }
	         		 
		 return new Shape(p.toArray(new Point[p.size()]));
		 
	}
	
	/*public static Shape getShapeFromStr2(String input){
		List<Point> p=new ArrayList<Point>();
		JSONParser parser = new JSONParser();
		 try{
	         Object obj = parser.parse(input);
	         JSONArray array = (JSONArray)obj;
	         for(int i=0;i<array.size(); i++){
	        	 JSONArray a=(JSONArray) array.get(i);
	        	 p.add(new Point((int) (long)a.get(0),(int) (long)a.get(1)));
	         }
	         
		 } catch(ParseException pe){
				
	         System.out.println("position: " + pe.getPosition());
	         System.out.println(pe);
	      }
		 return new Shape(p.toArray(new Point[p.size()]));
		 
	}*/
	
}
