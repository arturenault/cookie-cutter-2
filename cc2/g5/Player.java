package cc2.g5;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

    private ArrayList<Move> moveHistory = new ArrayList<>();
    private PriorityQueue<MoveCosts> priorityQueue;

    public static final int NUMBER_OF_MOVES = 1000;

    public Shape cutter(int length, Shape[] shapes, Shape[] opponentShapes) {
        Shape shape = null;
        if (length == 11) {
            shape = ShapeGenerator.getNextElevenShape(shapes, opponentShapes);
        } else if (length == 8) {
            shape = ShapeGenerator.getNextEightShape(shapes, opponentShapes);
        } else if (length == 5) {
            shape = ShapeGenerator.getNextFiveShape(shapes, opponentShapes);
        }

        return shape;
    }

    public Move cut(Dough dough, Shape[] shapes, Shape[] opponentShapes) {
        priorityQueue = new PriorityQueue<>(new MoveComparator(shapes, opponentShapes, dough, moveHistory));
        
        HashMap<Integer, ArrayList<Move>> moveSet = Utils.generateMoves(dough, shapes);

        ArrayList<Move> elevenMoves = moveSet.get(11);
        ArrayList<Move> eightMoves = moveSet.get(8);
        ArrayList<Move> fiveMoves = moveSet.get(5);

        if (dough.uncut()) {
            priorityQueue.add(new MoveCosts(fiveMoves.get(0), 0, 0, 0));
        } else if (elevenMoves.size() != 0) {
            pushToPriorityQueue(shapes, priorityQueue, elevenMoves, dough, shapes, opponentShapes);

        } else if (eightMoves.size() != 0) {
            pushToPriorityQueue(shapes, priorityQueue, eightMoves, dough, shapes, opponentShapes);

        } else if (fiveMoves.size() != 0) {
            pushToPriorityQueue(shapes, priorityQueue, fiveMoves, dough, shapes, opponentShapes);

        }

        Move nextMove = priorityQueue.poll().move;

        moveHistory.add(nextMove);
        debugMove(nextMove);
        return nextMove;
    }

    public void debugMove(Move move){
    	//System.out.println("i: " + move.point.i + " j: " + move.point.j);
    }

    public void pushToPriorityQueue(Shape[] shapes, PriorityQueue<MoveCosts> priorityQueue, ArrayList<Move> moves, Dough dough, Shape[] cutters, Shape[] oppCutters){

        int i = 0;
        Point centerPoint = Utils.centerOfMass(dough);
        ModdableDough mDough = new ModdableDough(dough);

        for(Move m : moves){

            if(i++ == NUMBER_OF_MOVES)
                break;

            Point moveCenterPoint = Utils.getCenterOfAMove(shapes, m);
            double distance = Utils.distance(moveCenterPoint, centerPoint);

            Point dimens = ShapeGenerator.getDimensions(oppCutters[0]);
            int maxSide = Math.max(dimens.i, dimens.j);

            int minX = Math.max(0, m.point.i - maxSide);
            int maxX = Math.min(dough.side(), m.point.i + maxSide);

            int minY = Math.max(0, m.point.j - maxSide);
            int maxY = Math.min(dough.side(), m.point.j + maxSide);

            int playerMovesBefore = Utils.totalMoves(Utils.generateMoves(mDough, minX, maxX, minY, maxY, cutters));
            int opponentMovesBefore = Utils.totalMoves(Utils.generateMoves(mDough, minX, maxX, minY, maxY, cutters));

            mDough.cut(cutters[m.shape].rotations()[m.rotation], m.point);

            int playerMovesAfter = Utils.totalMoves(Utils.generateMoves(mDough, cutters));
            int opponentMovesAfter = Utils.totalMoves(Utils.generateMoves(mDough, cutters));

            float playerMoves = (float)playerMovesAfter/(float)playerMovesBefore;
            float opponentMoves = (float)opponentMovesAfter/(float)opponentMovesBefore;
            
            mDough.undoCut(cutters[m.shape].rotations()[m.rotation], m.point);

            priorityQueue.add(new MoveCosts(m, playerMoves, opponentMoves, distance));

        }

    }

}
