package cc2.g1;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cc2.sim.Point;
import cc2.sim.Shape;

public class Utils {
	static final String NEW_LINE_SEPARATOR="\n";
	
	public static void writeLine(Date startTime, long timeTaken, 
			String fileNameFor1LineResult, String gameId,
			String player1, String player2,
			int player1Score, int player2Score, String inputShapesString, List<Shape> c1, List<Shape> c2){

		SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd__hh_mm_ss"); 
		String startTimeString=dt.format(startTime);
		String p1Name=player1.trim().toLowerCase().replace("g", "");
		String p2Name=player2.trim().toLowerCase().replace("g", "");
		String line2=startTimeString	+"|" 
					+ timeTaken +"|" 
					+ gameId	+"|"
					+ p1Name + "|"
					+ player2.replace("g", "") + "|"
					+ player1Score	+"|"
					+ player2Score	+"|"
					+ (player1Score>player2Score? p1Name : p2Name)	+"|" //winner
					+ (player1Score<player2Score? p1Name : p2Name) +"|" 
					+ inputShapesString
					+ c1.get(0) 	+"|"
					+ c1.get(1)  	+"|"  //input Shape
					+ c1.get(2)  	+"|"
					+ c2.get(0) 	+"|"
					+ c2.get(1) 	+"|"
					+ c2.get(2) ;
		if(fileNameFor1LineResult!=null && fileNameFor1LineResult.trim().length()>0){
			writeLine( fileNameFor1LineResult+"__"+gameId+"__"+startTimeString,  line2);
		}
		System.out.println(line2);
	}
 
	public static void writeLine(String fileName, String line){
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(fileName,true);


			//Add a new line separator after the header
			//Write a new student object list to the CSV file
			fileWriter.append(line);
			fileWriter.append(NEW_LINE_SEPARATOR);

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {

			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}
	}
	
	static int getOpponentsMinDimensionFor11Shape(Shape[] opponent_shapes){
		int min_X=0, max_X=0, min_Y=0, max_Y=0;
		for(Shape s :   opponent_shapes){
			if(s.size()!=11)continue;
			 for(Point p :s){
				 min_X=Math.min(min_X, p.i);
				 max_X=Math.max(max_X, p.i);
				 
				 min_Y=Math.min(min_Y, p.j);
				 max_Y=Math.max(max_Y, p.j);
			 }
		}
		return Math.min( max_X-min_X,max_Y - min_Y);
	}
	static int getOpponentsMaxDimensionForShape(Shape[] opponent_shapes){
		int min_X=0, max_X=0, min_Y=0, max_Y=0, maxGap = 0;
		for(Shape s :   opponent_shapes){
			 for(Point p :s){
				 min_X=Math.min(min_X, p.i);
				 max_X=Math.max(max_X, p.i);
				 
				 min_Y=Math.min(min_Y, p.j);
				 max_Y=Math.max(max_Y, p.j);
			 }
			 maxGap=Math.max( max_X-min_X,max_Y - min_Y);
		}
		return maxGap;
	}
}
