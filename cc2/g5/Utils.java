package cc2.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Point;
import cc2.sim.Shape;

public class Utils {

    public static HashMap<Integer, ArrayList<Move>> generateMoves(Dough dough, Shape[] cutters){

        HashMap<Integer, ArrayList<Move>> moveSet = new HashMap<>();

        for (int shapeNumber = 0; shapeNumber < cutters.length; shapeNumber++) {
            Shape[] rotations = cutters[shapeNumber].rotations();
            ArrayList<Move> moves = new ArrayList<>();
            for (int rotNumber = 0; rotNumber < rotations.length; rotNumber++) {
                Shape tryShape = rotations[rotNumber];
                for (int i = 0; i < dough.side(); ++i) {
                    for (int j = 0; j < dough.side(); ++j) {
                        Point p = new Point(i, j);
                        if (dough.cuts(tryShape, p)){
                            moves.add(new Move(shapeNumber, rotNumber, p));
                        }
                    }
                }
            }
            moveSet.put(cutters[shapeNumber].size(), moves);
        }

        return moveSet;
    }

    public static int totalMoves(HashMap<Integer, ArrayList<Move>> moveSet){
        int count = 0;

        for(Integer i : moveSet.keySet()){
            count += moveSet.get(i).size();
        }
        return count;
    }

    public static double averageDistanceFromAllMoves(Dough dough, Point p1){

        double distance = 0;
        int points = 0;

        for (int i = 0; i < dough.side(); ++i) {
            for (int j = 0; j < dough.side(); ++j) {
                if(!dough.uncut(i, j)){
                    distance += Math.hypot(i - p1.i, j - p1.j);
                }
            }
        }

        return points > 0 ? distance / points : 1;
    }

    public static double averageDistanceFromOurMoves(Point p1, ArrayList<Move> moves, Shape[] shapes){

        if(moves.size() == 0){
            return 1;
        }

        double distance = 0;
        for(Move m : moves){
            Iterator<Point> it = shapes[m.shape].rotations()[m.rotation].iterator();
            while(it.hasNext()){
                Point p2 = it.next();
                distance += Math.hypot(p2.i - p1.i, p2.j - p1.j);
            }
        }

        return distance/moves.size();
    }

}
