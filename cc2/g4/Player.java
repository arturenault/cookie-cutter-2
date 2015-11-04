package cc2.g4;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

  private boolean[][] mold;

  private Random gen = new Random();

  private int lastLength = 0;

  public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
  {
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
      mold[gen.nextInt(1)+1][gen.nextInt(2)+1] = true;
      int count = 0;
      for (int i = 0; i < mold.length; i++) {
        for (int j = 0; j < mold[i].length; j++) {
          if (mold[i][j]) {
            cutter[count++] = new Point(i,j);
          }
        }
      }

      /* Create just one long general shape for 8 */
    } else if (length == 8) {
      if (lastLength != 8) {
        lastLength = 8;

        Shape opponent11 = opponent_shapes[0];

        int min_i = Integer.MAX_VALUE;
        int min_j = Integer.MAX_VALUE;
        int max_i = Integer.MIN_VALUE;
        int max_j = Integer.MIN_VALUE;

        Iterator<Point> iter = opponent11.iterator();

        while(iter.hasNext()) {
          Point p = iter.next();
          if (min_i > p.i) min_i = p.i;
          if (max_i < p.i) max_i = p.i;
          if (min_j > p.j) min_j = p.j;
          if (max_j < p.j) max_j = p.j;
        }

        int maxPadding = 4
        int height = max_i - min_i + maxPadding;
        int width = max_j - min_j + maxPadding;

        System.out.println("height: " + height +", width: " + width);
        boolean[][] opp = new boolean[height][width];

        iter = opponent11.iterator();
        while (iter.hasNext()) {
          Point p = iter.next();
          System.out.println("point i: " + p.i + ", width: " + p.j);
          opp[p.i - min_i][p.j - min_j] = true;
        }

        int count = 0;
        for (int threshold = 0; threshold < maxPadding; threshold++) {
          for (int i = 0; i < height + threshold && count < length; i++) {
            for (int j = 0; j < width + threshold && count < length; j++) {
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
      for (int i = 0; i < length; i++) {
        cutter[i] = new Point(i / 4, i % 4);
      }
      return new Shape(cutter);

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
        for (int i = 0; i < 5; i++) {
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
}
