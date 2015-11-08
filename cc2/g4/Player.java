package cc2.g4;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

  private boolean[][] mold;

  private Random gen = new Random();

  private boolean firstRun = false;

  private int lastLength = 0;

  private Queue<Shape> backup8shapes;

  public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
  {
    if (firstRun = true) {
      firstRun = false;

      populateBackup8Shapes();
    }

    Point[] cutter = new Point[length];
    /* If length is 11, build the mold we want to use for the
     * big and small shapes */
    if (length == 11) {
      lastLength = 11;
      mold = new boolean[4][4];
      for (int i = 0; i < mold.length; i++) {
        mold[0][i] = true;
        mold[i][0] = true;
        mold[i][3] = true;
      }
      mold[gen.nextInt(3)+1][gen.nextInt(2)+1] = true;
      int count = 0;
      for (int i = 0; i < mold.length; i++) {
        for (int j = 0; j < mold[i].length; j++) {
          if (mold[i][j]) {
            cutter[count++] = new Point(i,j);
          }
        }
      }

      /* Create a shape that fits with the other team's 11 for 8 */
    } else if (length == 8) {
      if (lastLength != 8) {
        lastLength = 8;

        Shape opponent11 = opponent_shapes[0];


        /* Build "convex hull" of 11-shape */
        int min_i = Integer.MAX_VALUE;
        int min_j = Integer.MAX_VALUE;
        int max_i = Integer.MIN_VALUE;
        int max_j = Integer.MIN_VALUE;
        Iterator<Point> iter = opponent11.iterator();
        while(iter.hasNext()) {
          Point p = iter.next();
          if (p.i < min_i) min_i = p.i;
          if (p.i > max_i) max_i = p.i;
          if (p.j < min_j) min_j = p.j;
          if (p.j > max_j) max_j = p.j;
        }

        int maxPadding = 4;
        int height = max_i - min_i;
        int width = max_j - min_j;

        boolean[][] opp = new boolean[height + maxPadding][width + maxPadding];

        iter = opponent11.iterator();
        while (iter.hasNext()) {
          Point p = iter.next();
          opp[p.i - min_i][p.j - min_j] = true;
        }

        int count = 0;
        for (int thresholdi = 0; thresholdi < maxPadding; thresholdi++) {
          for (int thresholdj = 0; thresholdj < maxPadding; thresholdj++) {
            for (int i = 0; i < height + thresholdi && count < length; i++) {
              for (int j = 0; j < width + thresholdj && count < length; j++) {
                if(!opp[i][j]) {
                  cutter[count++] = new Point(i,j);
                }
              }
            }
            if (Point.shape(cutter)) {
              return new Shape(cutter);
            }
          }
        }
      }
      Shape shape = backup8shapes.poll();
      if (shape == null) {
        int count;
        for (count = 0; count < length - 1; count++) {
          cutter[count] = new Point(count, 0);
        }
        cutter[count] = new Point(gen.nextInt(count), 1);
        shape = new Shape(cutter);
      }
      return shape;
      /* Build opposite of 11-sized shape */ 
    } else {
      if (lastLength != 5) {
        lastLength = 5;
        int count = 0;
        for (int i = 0; i < mold.length; i++) {
          for (int j = 0; j < mold.length; j++) {
            if (!mold[i][j]) {
              cutter[count++] = new Point(i,j);
            }
          }
        }
      } else {
        int count = 0;
        for (int i = 0; i < 4; i++) {
          cutter[count++] = new Point(i, 0);
        }
        int extra = gen.nextInt(4);
        cutter[count] = new Point(1, extra);
      }
    }
    return new Shape(cutter);
  }

  public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
  {
    // prune larger shapes if initial move
    if (dough.uncut()) {
      int min = Integer.MAX_VALUE;
      for (Shape s : shapes)
        if (min > s.size())
          min = s.size();
      for (int s = 0 ; s != shapes.length ; ++s)
        if (shapes[s].size() != min)
          shapes[s] = null;
    }
    // find all valid cuts
    ArrayList <Move> moves = new ArrayList <Move> ();
    for (int si = 0 ; si != shapes.length ; ++si) {
      if (shapes[si] == null) continue;
      for (int i = 0 ; i != dough.side() ; ++i) {
        for (int j = 0 ; j != dough.side() ; ++j) {
          Point p = new Point(i, j);
          Shape[] rotations = shapes[si].rotations();
          for (int ri = 0 ; ri != rotations.length ; ++ri) {
            Shape s = rotations[ri];
            if (dough.cuts(s, p)) {
              if (s.size() == 11 && ri == 2) {
                int latitude = i - 1;
                if (latitude < 0) {
                  return new Move(si, ri, p);
                } else {
                  boolean ideal = true;
                  for (int longitude = p.j + 1; longitude < p.j + 3; longitude++) {  
                    if (!dough.uncut(latitude + 1, longitude)) {
                      ideal = false;
                      break;
                    }
                  }
                  if (ideal) {
                    return new Move(si, ri, p);
                  }
                }
              }
              moves.add(new Move(si, ri, p));
            }
          }
        }
      }
    }
    // return a cut randomly
    return moves.get(0);
  }

  private void populateBackup8Shapes() {
    backup8shapes = new java.util.concurrent.ConcurrentLinkedQueue<Shape>();

    Point[] points = new Point[8];
    Shape shape;

    for(int i = 0; i < points.length; i++) {
      points[i] = new Point(i, 0);
    }
    shape = new Shape(points);
    backup8shapes.add(shape);

    for(int i = 0; i < points.length; i++) {
      points[i] = new Point(i / 4, i % 4);
    }
    shape = new Shape(points);
    backup8shapes.add(shape);

    for(int i = 0; i < points.length; i++) {
      points[i] = new Point(i / 3, i % 3);
    }
    shape = new Shape(points);
    backup8shapes.add(shape);
  }
}
