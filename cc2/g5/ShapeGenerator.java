package cc2.g5;

import cc2.sim.Shape;
import cc2.sim.Point;

public class ShapeGenerator {

    // add a pi
    private static Shape[] elevenShapes = {generateDiag(11), generateE(), generateS(), generateF(11), generateH(), generateLine(11)};
    private static int elevenPos = 0;

    private static Shape[] eightShapes = {generateDiag(8), generateF(8), generateY(), generateLine(8), generateL()};
    private static int eightPos = 0;

    private static Shape[] fiveShapes = { generateBlock(), generateDiag(5), generatePlus(), generateT(), generateLine(5), generateBlock(), generateU()};
    private static int fivePos = 0;

    public static Shape getNextElevenShape(Shape[] shapes, Shape[] opponentShapes) {
        return elevenShapes[elevenPos++];
    }

    public static Shape getNextEightShape(Shape[] shapes, Shape[] opponentShapes) {
        return eightShapes[eightPos++];
    }

    public static Shape getNextFiveShape(Shape[] shapes, Shape[] opponentShapes) {
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
        if(size > 8) {
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

    private static Shape generateLine(int size){
        Point[] shape = new Point[size];

        for(int i = 0; i < shape.length; i++){
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

    private static Shape generateBlock() {
        Point[] shape = new Point[5];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(1, 0);
        shape[3] = new Point(1, 1);
        shape[4] = new Point(2, 0);

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

    private static Shape generateDiag(int size){
        Point[] shape = new Point[size];

        shape[0] = new Point(0, 0);
        shape[1] = new Point(0, 1);
        shape[2] = new Point(1, 1);
        shape[3] = new Point(1, 2);
        shape[4] = new Point(2, 2);
        if(size > 5) {
            shape[5] = new Point(2, 3);
            shape[6] = new Point(3, 3);
            shape[7] = new Point(3, 4);
        }
        if(size > 8) {
            shape[8] = new Point(4, 4);
            shape[9] = new Point(4, 5);
            shape[10] = new Point(5, 5);
        }
        return new Shape(shape);
    }


}
