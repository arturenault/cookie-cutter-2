package cc2.g9;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Utils {
    public Utils() {
    }
    // public static Boolean isComplement(ArrayList<Move> past11Moves, Move curMove){
    // 	for(int i=0; i<past11Moves.size(); i++){
    // 		Move pastMove = past11Moves.get(i);
    // 		if(pastMove.rotation != curMove.rotation && distance(pastMove.point, curMove.point) >=7 ){
    // 			// return true;
    // 		}
    // 	}
    // 	return false;
    // }
    public static double distance(Point a, Point b){
    	return Math.sqrt(Math.pow(a.i - b.i, 2) + Math.pow(a.j - b.j, 2)); 
    }
    public static Move getDefenseIndex(Dough dough, Shape[] shapes) {

        Shape[] rotations = shapes[0].rotations();
        Shape s = rotations[2];

        // ---------- diagonal stack ----------
        for (int i = 2 ; i <= dough.side() ; i = i+3 ){
            int j = (-1)*i + 44;
            if (j >= 0){
                Point thisPt = new Point(i, j);
                Move thisMv = new Move(0, 2, thisPt);
                if (dough.cuts(s, thisPt)){
                    // System.out.println("Wow we found a defensive move!" + thisMv);
                    return thisMv;
                    
                }
            }
        }
        // ---------- 2nd to diagonal stack on left ----------
        for (int i = 2 ; i <= dough.side() ; i = i+3 ){
            int j = (-1)*i + 34;
            if (j >= 0){
                Point thisPt = new Point(i, j);
                Move thisMv = new Move(0, 2, thisPt);
                if (dough.cuts(s, thisPt)){
                    // System.out.println("Wow we found a 2nd defensive move on left!" + thisMv);
                    return thisMv;
                    
                }
            }
        }
        // ---------- 2nd to diagonal stack on right ----------
        for (int i = 2 ; i <= dough.side() ; i = i+3 ){
            int j = (-1)*i + 54;
            if (j >= 0){
                Point thisPt = new Point(i, j);
                Move thisMv = new Move(0, 2, thisPt);
                if (dough.cuts(s, thisPt)){
                    // System.out.println("Wow we found a 2nd defensive move on right!" + thisMv);
                    return thisMv;
                    
                }
            }
        }
        // ---------- 3rd to diagonal stack on right ----------
        for (int i = 2 ; i <= dough.side() ; i = i+3 ){
            int j = (-1)*i + 64;
            if (j >= 0){
                Point thisPt = new Point(i, j);
                Move thisMv = new Move(0, 2, thisPt);
                if (dough.cuts(s, thisPt)){
                    // System.out.println("Wow we found a 3rd defensive move on right!" + thisMv);
                    return thisMv;
                    
                }
            }
        }
        // ---------- 3rd to diagonal stack on left ----------
        for (int i = 2 ; i <= dough.side() ; i = i+3 ){
            int j = (-1)*i + 24;
            if (j >= 0){
                Point thisPt = new Point(i, j);
                Move thisMv = new Move(0, 2, thisPt);
                if (dough.cuts(s, thisPt)){
                    // System.out.println("Wow we found a 3rd defensive move on left!" + thisMv);
                    return thisMv;
                    
                }
            }
        }
        // ---------- 4th to diagonal stack on right ----------
        for (int i = 2 ; i <= dough.side() ; i = i+3 ){
            int j = (-1)*i + 74;
            if (j >= 0){
                Point thisPt = new Point(i, j);
                Move thisMv = new Move(0, 2, thisPt);
                if (dough.cuts(s, thisPt)){
                    // System.out.println("Wow we found a 4th defensive move on right!" + thisMv);
                    return thisMv;
                    
                }
            }
        }
        // ---------- 4th to diagonal stack on left ----------
        for (int i = 2 ; i <= dough.side() ; i = i+3 ){
            int j = (-1)*i + 14;
            if (j >= 0){
                Point thisPt = new Point(i, j);
                Move thisMv = new Move(0, 2, thisPt);
                if (dough.cuts(s, thisPt)){
                    // System.out.println("Wow we found a 4th defensive move on left!" + thisMv);
                    return thisMv;
                    
                }
            }
        }

        System.out.println("No defensive move found.");
        return null;
    }

}