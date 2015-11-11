package cc2.g1;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class TournamentRunAgainstAllPlayers {

	public static void main(String[] args) throws IOException {
		int gameId=1;
		

		
		SimpleDateFormat dt = new SimpleDateFormat("yyyymmdd_hhmm"); 
		String tournamentId=dt.format(new Date());
		//int tournamentId=8888_1;
		
		BufferedOutputStream fileRedirect = new
				BufferedOutputStream(new FileOutputStream("RunAgainstAll_"+tournamentId+".bat"));
		System.setOut(new PrintStream(fileRedirect));
		System.out.println("	mkdir .\\output\\"+ tournamentId );	
		for(int round=1;round<=5;round++){
			for(int i=1;i<=9;i++){
				for(int j=1;j<=9;j++){
					if(i==j){
						continue;
					}
					
					String command="\"C:\\Program Files\\Java\\jdk1.8.0_60\\bin\\java\" cc2.sim.Simulator --groups g"+ i +" g"+ j +" -o1 \".\\output\\"+tournamentId+"\\"+tournamentId+"_"+ round+"\" -i " + gameId;
					System.out.println(command);
					//executeRun(command);
					gameId+=1;
				}
			}
		}
		
		System.out.println("copy .\\output\\"+ tournamentId +"\\* .\\output\\"+tournamentId+"\\merged_" + tournamentId +".csv");	
		System.out.println("");
		fileRedirect.flush();
		fileRedirect.close();
	}

	private static void executeRun(String command){
		Process process;
		try {
			process = new ProcessBuilder(
					command).start();
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;

				System.out.printf("Output of running %s is:" , command);

				while ((line = br.readLine()) != null) {
				  System.out.println(line);
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
