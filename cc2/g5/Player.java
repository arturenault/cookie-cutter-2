package cc2.g5;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

    private ArrayList<Move> moveHistory = new ArrayList<>();
    private PriorityQueue<MoveCosts> priorityQueue;

    public static final int NUMBER_OF_MOVES = 250;

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
            priorityQueue.add(new MoveCosts(fiveMoves.get(0), 0, 0));
        } else if (elevenMoves.size() != 0) {
            pushToPriorityQueue(priorityQueue, elevenMoves, dough, shapes, opponentShapes);

        } else if (eightMoves.size() != 0) {
            pushToPriorityQueue(priorityQueue, eightMoves, dough, shapes, opponentShapes);

        } else if (fiveMoves.size() != 0) {
            pushToPriorityQueue(priorityQueue, fiveMoves, dough, shapes, opponentShapes);

        }

        Move nextMove = priorityQueue.poll().move;

        moveHistory.add(nextMove);

        return nextMove;
    }

    public void pushToPriorityQueue(PriorityQueue<MoveCosts> priorityQueue, ArrayList<Move> moves, Dough dough, Shape[] cutters, Shape[] oppCutters){

        int i = 0;
        for(Move m : moves){
            if(i++ == NUMBER_OF_MOVES)
                break;

            ModdableDough mDough = new ModdableDough(dough);
            mDough.cut(cutters[m.shape].rotations()[m.rotation], m.point);

            int playerMoves = Utils.totalMoves(Utils.generateMoves(mDough, cutters));
            int opponentMoves = Utils.totalMoves(Utils.generateMoves(mDough, oppCutters));

            priorityQueue.add(new MoveCosts(m, playerMoves, opponentMoves));

        }

    }

    public void pushToPriorityQueue(ArrayList<Move> moves, PriorityQueue<MoveCosts> priorityQueue) {
        int count = 0;


        for (Move move : moves) {
            if (count > 20)
                break;
            priorityQueue.add(new MoveCosts(move, 0, 0));
            count++;
            System.out.println(move.shape + " " + move.rotation + " " + move.point.i + " " + move.point.j);
        }
    }
}
