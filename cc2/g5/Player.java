package cc2.g5;

import cc2.g6.Util;
import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class Player implements cc2.sim.Player {

    private ArrayList<Move> moveHistory = new ArrayList<>();
    private PriorityQueue<MoveCosts> priorityQueue;
    private ModdableDough doughCache = null;
    public static final int NUMBER_OF_MOVES = 5000;
    public static int maxSide = 0;
    private boolean isStraightLine = false;
    private Set<Point> oppMoveNeighborSet = new HashSet<>();

    public Shape cutter(int length, Shape[] shapes, Shape[] opponentShapes) {
        Shape shape = null;
        if (length == 11) {
            shape = ShapeGenerator.getNextElevenShape(shapes, opponentShapes);
        } else if (length == 8) {
            isStraightLine = LineShaped(opponentShapes[0]);
            shape = ShapeGenerator.getNextEightShape(shapes, opponentShapes);
        } else if (length == 5) {
            shape = ShapeGenerator.getNextFiveShape(shapes, opponentShapes);
        }

        return shape;
    }

    public int getMaxDimensions(Shape[] opponentShapes) {
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < opponentShapes.length; i++) {
            Point dimensions = ShapeGenerator.getDimensions(opponentShapes[i]);
            if (Math.max(dimensions.i, dimensions.j) > max)
                max = Math.max(dimensions.i, dimensions.j);
        }
        return max;
    }


    public Move cut(Dough dough, Shape[] shapes, Shape[] opponentShapes) {

        priorityQueue = new PriorityQueue<>(new MoveComparator(shapes, opponentShapes, dough, moveHistory));
        maxSide = getMaxDimensions(opponentShapes);
        HashMap<Integer, ArrayList<Move>> moveSet = null;

        if (isStraightLine && !dough.uncut()) {
            if (doughCache == null)
                doughCache = new ModdableDough(dough);

            Set<Point> opponentRecentMove = getOpponentRecentMove(dough);

            Set<Point> neighborSet = getNeighbors(opponentRecentMove);
            oppMoveNeighborSet.addAll(neighborSet);
            if (neighborSet.size() != 0) {
                moveSet = Utils.generateMovesNearOpponetMove(dough, shapes, neighborSet);
                if (moveSet.get(11).size() == 0) {
                    moveSet = Utils.generateMoves(dough, shapes);
                }
            }

        }

        if (isStraightLine && (moveSet == null || isMoveSetEmpty(moveSet))) {
            moveSet = Utils.generateMovesNearOpponetMove(dough, shapes, oppMoveNeighborSet);
        }

        if (moveSet == null || isMoveSetEmpty(moveSet)) {
            moveSet = Utils.generateMoves(dough, shapes);
        }

        ArrayList<Move> elevenMoves = moveSet.get(11);
        ArrayList<Move> eightMoves = moveSet.get(8);
        ArrayList<Move> fiveMoves = moveSet.get(5);

        if (dough.uncut()) {
            priorityQueue.add(new MoveCosts(fiveMoves.get(0), 0, 0, 0));
        } else if (elevenMoves.size() != 0) {
            pushToPriorityQueue(priorityQueue, elevenMoves, dough, shapes, opponentShapes);

        } else if (eightMoves.size() != 0) {
            pushToPriorityQueue(priorityQueue, eightMoves, dough, shapes, opponentShapes);

        } else if (fiveMoves.size() != 0) {
            pushToPriorityQueue(priorityQueue, fiveMoves, dough, shapes, opponentShapes);

        } else {
        }

        Move nextMove = priorityQueue.poll().move;

        moveHistory.add(nextMove);
        // debugMove(nextMove);
        if (isStraightLine) {
            doughCache = new ModdableDough(dough);
            doughCache.cut(shapes[nextMove.shape].rotations()[nextMove.rotation], nextMove.point);
        }
        return nextMove;
    }

    public boolean isMoveSetEmpty(HashMap<Integer, ArrayList<Move>> map) {
        int[] nums = {11, 8, 5};
        int count = 0;
        //System.out.println(map);
        for (int n : nums) {
            //System.out.println("n: " + n);
            count += map.get(n) != null ? map.get(n).size() : 0;
        }
        return count == 0;
    }

    public void pushToPriorityQueue(PriorityQueue<MoveCosts> priorityQueue, ArrayList<Move> moves, Dough dough, Shape[] cutters, Shape[] oppCutters) {

        int i = 0;
        Point centerPoint = Utils.centerOfMass(dough);
        ModdableDough mDough = new ModdableDough(dough);

        for (Move m : moves) {

            if (i++ == NUMBER_OF_MOVES)
                break;

            Point moveCenterPoint = Utils.getCenterOfAMove(cutters, m);
            double distance = Utils.distance(moveCenterPoint, centerPoint);
            Point ourMoveDimentionPoint = ShapeGenerator.getDimensions(cutters[m.shape].rotations()[m.rotation]);
            int minX = Math.max(0, m.point.i - maxSide);
            int maxX = Math.min(dough.side(), m.point.i + ourMoveDimentionPoint.i + maxSide);

            int minY = Math.max(0, m.point.j - maxSide);
            int maxY = Math.min(dough.side(), m.point.j + ourMoveDimentionPoint.j + maxSide);
            //TODO should be modified to the real box region, add height and width to x and y

            HashMap<Integer, ArrayList<Move>> playerMovesBeforeHashMap = Utils.generateMoves(mDough, minX, maxX, minY, maxY, cutters);
            HashMap<Integer, ArrayList<Move>> opponentMovesBeforeHashMap = Utils.generateMoves(mDough, minX, maxX, minY, maxY, oppCutters);
            mDough.cut(cutters[m.shape].rotations()[m.rotation], m.point);

            HashMap<Integer, ArrayList<Move>> playerMovesAfterHashMap = Utils.generateMoves(mDough, minX, maxX, minY, maxY, cutters);
            HashMap<Integer, ArrayList<Move>> opponentMovesAfterHashMap = Utils.generateMoves(mDough, minX, maxX, minY, maxY, oppCutters);
            int opDiff = Utils.calDiffWithWeight(opponentMovesBeforeHashMap, opponentMovesAfterHashMap);
            int ourDiff = Utils.calDiffWithWeight(playerMovesBeforeHashMap, playerMovesAfterHashMap);

            mDough.undoCut(cutters[m.shape].rotations()[m.rotation], m.point);

            priorityQueue.add(new MoveCosts(m, opDiff, ourDiff, (float) distance));

        }

    }

    private Set<Point> getOpponentRecentMove(Dough dough) {
        Set<Point> opponetMove = new HashSet<Point>();
        for (int i = 0; i < dough.side(); i++) {
            for (int j = 0; j < dough.side(); j++) {
                if (doughCache.uncut(i, j) && !dough.uncut(i, j)) {
                    opponetMove.add(new Point(i, j));
                }
            }
        }
        return opponetMove;
    }

    public static boolean LineShaped(Shape opponentShape) {
        if (opponentShape.equals(ShapeGenerator.generateLine(11)) || opponentShape.equals(ShapeGenerator.generateLongL(11, true)) || opponentShape.equals(ShapeGenerator.generateLongL(11, false)))
            return true;
        return false;
    }

    private Set<Point> getNeighbors(Set<Point> points) {

        Set<Point> neighbors = new HashSet<>();
        for (Point point : points) {
            neighbors.addAll(new HashSet<>(Arrays.asList(point.neighbors())));
        }
        return neighbors;
    }
}
