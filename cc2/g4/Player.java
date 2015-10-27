package cc2.g4;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

  private boolean[][] mold;

  private Random gen = new Random();

  public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
  {
    Point[] cutter = new Point[length];
    /* If length is 11, build the mold we want to use for the
     * big and small shapes */
    if (length == 11) {
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
      int count = 0;
      for (int i = 0; i < length; i++) {
        cutter[count++] = new Point(i / 4, i % 4);
      }

    /* Build opposite of 11-sized shape */ 
    } else {
      int count = 0;
      for (int i = 0; i < mold.length; i++) {
        for (int j = 0; j < mold.length; j++) {
          if (!mold[i][j]) {
            cutter[count++] = new Point(i,j);
          }
        }
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
    for (int i = 0 ; i != dough.side() ; ++i)
      for (int j = 0 ; j != dough.side() ; ++j) {
        Point p = new Point(i, j);
        for (int si = 0 ; si != shapes.length ; ++si) {
          if (shapes[si] == null) continue;
          Shape[] rotations = shapes[si].rotations();
          for (int ri = 0 ; ri != rotations.length ; ++ri) {
            Shape s = rotations[ri];
            if (dough.cuts(s, p))
              moves.add(new Move(si, ri, p));
          }
        }
      }
    // return a cut randomly
    return moves.get(gen.nextInt(moves.size()));
  }
}
