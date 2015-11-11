package cc2.g10;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

  private boolean[] row_2 = new boolean [0];

  private Random gen = new Random();

  int currShape = 0;

  public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
  {
    // check if first try of given cutter length
    Point[] cutter = new Point [length];

    if (length == 11) {
      cutter[0] = new Point(0,0);
      cutter[1] = new Point(0,1);
      cutter[2] = new Point(0,2);
      cutter[3] = new Point(1,0);
      cutter[4] = new Point(1,2);
      cutter[5] = new Point(2,0);
      cutter[6] = new Point(2,1);
      cutter[7] = new Point(2,2);
      cutter[8] = new Point(3,0);
      cutter[9] = new Point(4,0);
      cutter[10] = new Point(5,0);
    }

    if (length == 8) {
      cutter[0] = new Point(3,0);
      cutter[1] = new Point(3,1);
      cutter[2] = new Point(3,2);
      cutter[3] = new Point(2,2);
      cutter[4] = new Point(1,2);
      cutter[5] = new Point(0,2);
      cutter[6] = new Point(0,3);
      cutter[7] = new Point(0,4);
    }

    if (length == 5) {
      cutter[0] = new Point(0,1);
      cutter[1] = new Point(1,0);
      cutter[2] = new Point(1,1);
      cutter[3] = new Point(1,2);
      cutter[4] = new Point(2,1);
    }

    return new Shape(cutter);
  }

  public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
  {
    if (currShape == 2) 
      currShape = 3; 
    else if (currShape == 0)
      currShape = 2;
    else if (currShape == 3)
      currShape = 1;
    else if (currShape == 1)
      currShape = 0;

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
            if (dough.cuts(shapes[currShape%3].rotations()[0], p))
              moves.add(new Move(currShape%3, 0, p));
        }
    // return a cut randomly
    return moves.get(0);
  }
}
