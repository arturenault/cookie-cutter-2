package cc2.g5;

import cc2.sim.Shape;
import cc2.sim.Point;

import java.util.Iterator;
import java.util.Stack;
import java.util.ArrayList;

public class ShapeGenerator {

    // add a pi
    private static boolean fittedShapeUsed = false;
    private static Shape[] elevenShapes = {generateDiag(11), generateE(), generateS(), generateF(11), generateH(), generateLine(11)};
    private static int elevenPos = 0;

    private static Shape[] eightShapes = {generateCondensedDiag(true), generateCondensedDiag(false), generateDiag(8), generateF(8), generateY(), generateLine(8), generateL()};
    private static int eightPos = 0;

    private static Shape[] fiveShapes = {generateBlock(true), generateBlock(false), generateDiag(5), generatePlus(), generateT(), generateLine(5), generateU()};
    private static int fivePos = 0;

    public static Shape getNextElevenShape(Shape[] shapes, Shape[] opponentShapes) {
        return elevenShapes[elevenPos++];
    }

    public static Shape getNextEightShape(Shape[] shapes, Shape[] opponentShapes) {
        /*Shape ad_shape = getFittedShape(opponentShapes[0], 8, 0);
        if (ad_shape != null) {
            System.out.println("Returning it for 8 *- " + opponentShapes[0].size());
            return ad_shape;
        } else {
            System.out.println("Returning default");
        }*/

        return eightShapes[eightPos++];
    }

    public static Shape getNextFiveShape(Shape[] shapes, Shape[] opponentShapes) {
        Shape ad_shape = getFittedShape(opponentShapes[0], 5, 0);
        if (ad_shape != null) {
            System.out.println("Returning it for 5 *- " + opponentShapes[0].size());
            return ad_shape;
        } else {
            System.out.println("Returning default");
        }
        return fiveShapes[fivePos++];
    }

    private static Shape generateSqueege(int size) {
        Point[] shape = new Point[size];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(1, 1);
        shape[4] = new Point(2, 0);
        shape[5] = new Point(2, 1);
        shape[6] = new Point(2, 2);
        shape[7] = new Point(3, 1);
        if (size > 8) {
            shape[8] = new Point(4, 0);
            shape[9] = new Point(4, 1);
            shape[10] = new Point(4, 2);
        }
        return new Shape(shape);
    }

    private static Shape generateE() {
        Point[] shape = new Point[11];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(1, 0);
        shape[4] = new Point(2, 0);
        shape[5] = new Point(2, 1);
        shape[6] = new Point(2, 2);
        shape[7] = new Point(3, 0);
        shape[8] = new Point(4, 0);
        shape[9] = new Point(4, 1);
        shape[10] = new Point(4, 2);

        return new Shape(shape);
    }

    private static Shape generateS() {
        Point[] shape = new Point[11];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(1, 0);
        shape[4] = new Point(2, 0);
        shape[5] = new Point(2, 1);
        shape[6] = new Point(2, 2);
        shape[7] = new Point(3, 2);
        shape[8] = new Point(4, 0);
        shape[9] = new Point(4, 1);
        shape[10] = new Point(4, 2);

        return new Shape(shape);
    }

    private static Shape generateF(int size) {
        Point[] shape = new Point[size];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(0, 3);
        shape[4] = new Point(1, 0);
        shape[5] = new Point(2, 0);
        shape[6] = new Point(1, 2);
        shape[7] = new Point(2, 2);
        if (size == 11) {
            shape[8] = new Point(0, 4);
            shape[9] = new Point(3, 0);
            shape[10] = new Point(3, 2);
        }

        return new Shape(shape);
    }

    private static Shape generateH() {
        Point[] shape = new Point[11];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(0, 3);
        shape[4] = new Point(0, 4);
        shape[5] = new Point(2, 0);
        shape[6] = new Point(2, 1);
        shape[7] = new Point(2, 2);
        shape[8] = new Point(2, 3);
        shape[9] = new Point(2, 4);
        shape[10] = new Point(1, 2);

        return new Shape(shape);
    }

    private static Shape generateLine(int size) {
        Point[] shape = new Point[size];

        for (int i = 0; i < shape.length; i++) {
            shape[i] = new Point(0, i);
        }
        return new Shape(shape);
    }

    private static Shape generateY() {
        Point[] shape = new Point[8];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(1, 2);
        shape[4] = new Point(1, 3);
        shape[5] = new Point(2, 0);
        shape[6] = new Point(2, 1);
        shape[7] = new Point(2, 2);

        return new Shape(shape);
    }

    private static Shape generateU() {
        Point[] shape = new Point[5];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(1, 0);
        shape[4] = new Point(1, 2);

        return new Shape(shape);
    }

    private static Shape generateBlock(boolean left) {
        Point[] shape = new Point[5];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(1, 0);
        shape[3] = new Point(1, 1);
        if(left)
            shape[4] = new Point(2, 0);
        else
            shape[4] = new Point(2, 1);

        return new Shape(shape);
    }

    private static Shape generatePlus() {
        Point[] shape = new Point[5];

        shape[0] = new Point(1, 0);
        shape[1] = new Point(1, 1);
        shape[2] = new Point(1, 2);
        shape[3] = new Point(0, 1);
        shape[4] = new Point(2, 1);

        return new Shape(shape);
    }

    private static Shape generateL() {
        Point[] shape = new Point[8];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(0, 3);
        shape[4] = new Point(0, 4);
        shape[5] = new Point(0, 5);
        shape[6] = new Point(1, 1);
        shape[7] = new Point(1, 2);

        return new Shape(shape);
    }

    private static Shape generateT() {
        Point[] shape = new Point[5];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(1, 1);
        shape[4] = new Point(2, 1);

        return new Shape(shape);
    }

    private static Shape generateTree() {
        Point[] shape = new Point[8];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(0, 2);
        shape[3] = new Point(2, 0);
        shape[4] = new Point(2, 1);
        shape[5] = new Point(2, 2);
        shape[6] = new Point(1, 1);
        shape[7] = new Point(3, 1);

        return new Shape(shape);
    }

    private static Shape generateCondensedDiag(boolean left){
        Point[] shape = new Point[8];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(1, 0);
        shape[3] = new Point(1, 1);
        if(left){
            shape[4] = new Point(2, 0);
            shape[5] = new Point(2, 1);
            shape[6] = new Point(3, 0);
            shape[7] = new Point(1, 2);
        } else {
            shape[4] = new Point(0, 2);
            shape[5] = new Point(1, 2);
            shape[6] = new Point(0, 3);
            shape[7] = new Point(2, 1);
        }
        

        return new Shape(shape);
    }

    private static Shape generateDiag(int size) {
        Point[] shape = new Point[size];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(1, 1);
        shape[3] = new Point(1, 2);
        shape[4] = new Point(2, 2);
        if (size > 5) {
            shape[5] = new Point(2, 3);
            shape[6] = new Point(3, 3);
            shape[7] = new Point(3, 4);
        }
        if (size > 8) {
            shape[8] = new Point(4, 4);
            shape[9] = new Point(4, 5);
            shape[10] = new Point(5, 5);
        }
        return new Shape(shape);
    }

    public static Point get_coordinate(int retry) {
        Point co_ordinate = null;
        switch (retry) {
            case 0:
                co_ordinate = new Point(0, 0);
                break;
            case 1:
                co_ordinate = new Point(1, 0);
                break;
            case 2:
                co_ordinate = new Point(0, 1);
                break;
            case 3:
                co_ordinate = new Point(1, 1);
                break;
            default:
                co_ordinate = new Point(0, 0);
                break;
        }
        System.out.println("get_coordinate: " + co_ordinate);
        return co_ordinate;
    }


    public static Shape getFittedShape(Shape f_shape, int length, int retry) {
        // For a given shape, finds it's min and max value i.e its dimensions; Makes a dough of that dimension.
        if (fittedShapeUsed == true){
            return null;
        }
        ArrayList<Point> maxPoints = new ArrayList<Point>();
        int padding = retry;
        Point dimensions = getDimensions(f_shape);
        ModdableDough mDough = new ModdableDough(dimensions.i + padding, dimensions.j + padding); // default: retry = 0 | Used to add padding;

        // Finds all uncut points; if connected pushes points into stack
        mDough.cut(f_shape, get_coordinate(retry));
        Stack<Point> conn_comp = new Stack<Point>();
        while (!mDough.saturated()) {
            Point init = findAvailablePoint(mDough);
            conn_comp.push(init);
            ArrayList<Point> points = new ArrayList<Point>();
            points.add(init);
            mDough.cut(init);

            // conn_comp_loop:
            while (!conn_comp.isEmpty()) {
                Point next = conn_comp.pop();
                Point[] neighbors = next.neighbors();
                for (int i = 0; i < neighbors.length; i++) {
                    if (mDough.uncut(neighbors[i])) {
                        conn_comp.push(neighbors[i]);
                        mDough.cut(neighbors[i]);
                        points.add(neighbors[i]);
                    }
                }
            }
            if (points.size() == length) {
                fittedShapeUsed = true;
                Point[] cutter = new Point[points.size()];
                return new Shape(points.toArray(cutter));
            }
        }
        if (retry < 3)
            return getFittedShape(f_shape, length, retry + 1);
        return null;
    }


    public static Point getDimensions(Shape cutter) {
        // For a given shape, returns it's min and max width i.e its dimensions.
        int minI = Integer.MAX_VALUE;
        int minJ = Integer.MAX_VALUE;
        int maxI = Integer.MIN_VALUE;
        int maxJ = Integer.MIN_VALUE;
        Iterator<Point> pointsInShape = cutter.iterator();
        while (pointsInShape.hasNext()) {
            Point p = pointsInShape.next();
            minI = Math.min(minI, p.i);
            maxI = Math.max(maxI, p.i);
            minJ = Math.min(minJ, p.j);
            maxJ = Math.max(maxJ, p.j);
        }
        return new Point(maxI - minI + 1, maxJ - minJ + 1);
    }


    private static Point findAvailablePoint(ModdableDough mDough) {
        for (int i = 0; i < mDough.width; i++) {
            for (int j = 0; j < mDough.height; j++) {
                if (mDough.uncut(i, j)) {
                    return new Point(i, j);
                }
            }
        }
        System.out.println("No available points");
        return null;
    }


}
