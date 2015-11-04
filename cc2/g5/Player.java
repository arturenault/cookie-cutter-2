package cc2.g5;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

    public Shape cutter(int length, Shape[] shapes, Shape[] opponentShapes) {
        Shape shape = null;
        if (length == 11) {
            shape = ShapeGenerator.getNextElevenShape(shapes, opponentShapes);
        } else if (length == 8) {
            shape = ShapeGenerator.getNextEightShape(shapes, opponentShapes);
        } else if (length == 5) {
            shape = ShapeGenerator.getNextFiveShape(shapes, opponentShapes);
        }

        System.out.println("Shape:" + shape);
        return shape;
    }

    public Move getBestMove(Dough dough, ArrayList<Move> elevenMoves, ArrayList<Move> eightMoves, ArrayList<Move> fiveMoves){
        if(dough.uncut()){
        	return fiveMoves.get(0);
        }
    	if(elevenMoves.size() > 0) {
    		if(elevenMoves.size() > 1)
    			return elevenMoves.get(1);
            return elevenMoves.get(0);
        } else if(eightMoves.size() > 0){
        	if(eightMoves.size() > 1)
    			return eightMoves.get(1);
            return eightMoves.get(0);
        } else {
        	if(fiveMoves.size() > 1)
    			return fiveMoves.get(1);
            return fiveMoves.get(0);
        }
    }

    public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes) {

        ArrayList<Move> elevenMoves = new ArrayList<>();
        ArrayList<Move> eightMoves = new ArrayList<>();
        ArrayList<Move> fiveMoves = new ArrayList<>();

        for (int shapeNumber = 0; shapeNumber < shapes.length; shapeNumber++) {
        	Shape[] rotations = shapes[shapeNumber].rotations();
        	 for (int rotNumber = 0; rotNumber < rotations.length; rotNumber++) {
		        for (int i = 0; i < dough.side(); i++) {
		            for (int j = 0; j < dough.side(); j++) {
		                Point p = new Point(i, j);
                        if (dough.cuts(rotations[rotNumber], p)) {
                            switch (shapes[shapeNumber].size()) {
                                case 11:
                                    elevenMoves.add(new Move(shapeNumber, rotNumber, p));
                                    break;
                                case 8:
                                    eightMoves.add(new Move(shapeNumber, rotNumber, p));
                                    break;
                                case 5:
                                default:
                                    fiveMoves.add(new Move(shapeNumber, rotNumber, p));
                                    break;
                            }
                        }
                    }
                }
            }
        }

        return getBestMove(dough, elevenMoves, eightMoves, fiveMoves);

    }
}
