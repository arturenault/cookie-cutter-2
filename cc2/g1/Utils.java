package cc2.g1;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	static final String NEW_LINE_SEPARATOR="\n";
	
	public static void writeLine(Date startTime, long timeTaken, 
			String fileNameFor1LineResult, int gameId,
			String player1, String player2,
			int player1Score, int player2Score, String inputShapesString){

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
					+ inputShapesString; //input Shape
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
}
