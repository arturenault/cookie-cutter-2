package cc2.g4;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

  private boolean[][] mold;

  private Random gen = new Random();

  private boolean firstRun = true;

  private int lastLength = 0;
  private boolean lineAvailable = true;
  private int mode = 0;

  private Queue<Shape> backup8shapes;

  // aggressive cuts variables
  ArrayList<Move> oppMoves = new ArrayList<>();
  Dough lastDough = new Dough(50);

  public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
  {
    if (firstRun) {
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

        for (int thresholdi = 0; thresholdi < maxPadding; thresholdi++) {
          for (int thresholdj = 0; thresholdj < maxPadding; thresholdj++) {
            int count = 0;
            for (int i = 0; i < height + thresholdi && count < length; i++) {
              for (int j = 0; j < width + thresholdj && count < length; j++) {
                if(!opp[i][j]) {
                  cutter[count++] = new Point(i,j);
                }
              }
            }
            if (Point.shape(cutter)) {
              Shape shape = new Shape(cutter);
              iter = opponent11.iterator();
              while (iter.hasNext()) {
                System.out.println(iter.next());
              } 
              if (shape.equals(backup8shapes.peek())) {
                backup8shapes.poll();
              }
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
      System.out.println("g4 (2) " + length + ": " + shape);
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
        mode = 1;
        int count = 0;
        for (int i = 0; i < 4; i++) {
          cutter[count++] = new Point(i, 0);
        }
        if (lineAvailable) {
          cutter[count] = new Point(4, 0);
          lineAvailable = false;
        } else {
          int extra = gen.nextInt(4);
          cutter[count] = new Point(extra, 1);
        }
      }
    }
    System.out.println("g4 (3) " + length + ": " + new Shape(cutter));
    return new Shape(cutter);
  }

  public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
  {
    Move cut = realCut(dough, shapes, opponent_shapes);

    // cut on our simulated dough
    // System.out.println("Our move: ("+cut.shape+", "+cut.rotation+", "+cut.point+")");
    lastDough.cut(shapes[cut.shape].rotations()[cut.rotation], cut.point);

    return cut;
  }

  public Move realCut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
  {
    boolean oppPlayed = getOppMove(dough, opponent_shapes);
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
    ArrayList <Move> idealMoves = new ArrayList <Move> ();
    for (int si = 0 ; si != shapes.length ; ++si) {
      if (shapes[si] == null) continue;
      for (int i = 0 ; i != dough.side() ; ++i) {
        for (int j = 0 ; j != dough.side() ; ++j) {
          Point p = new Point(i, j);
          Shape[] rotations = shapes[si].rotations();
          for (int ri = 0 ; ri != rotations.length ; ++ri) {
            Shape s = rotations[ri];
            if (dough.cuts(s, p)) {
              if (mode == 0 && s.size() == 11) {
                if (isIdeal(dough, ri, i, j)) {
                  idealMoves.add(new Move(si, ri, p));
                }
              } else if (mode == 1 && s.size() == 11) {
                return new Move(si, ri, p);
              }
              moves.add(new Move(si, ri, p));
            }
          }
        }
      }
    }
    if (!idealMoves.isEmpty()) {
      if (oppPlayed) {
        return aggressiveCut(shapes, opponent_shapes, idealMoves);
      }
      // return idealMoves.get(idealMoves.size() - 1);
      // return idealMoves.get(0);
    }
    return moves.get(0);
  }

  private boolean isIdeal(Dough dough, int rotation, int i, int j) {
    switch (rotation) {
      case 0:
        if (i + 3 == dough.side() - 1 ||
          (!dough.uncut(i + 4, j + 1) && !dough.uncut(i + 4, j + 2)))
          return true;
        return false;
      case 1:
        if (j == 0 ||
          (!dough.uncut(i + 1, j - 1) && !dough.uncut(i + 2, j - 1)))
          return true;
        return false;
      case 2:
        if (i == 0 ||
          (!dough.uncut(i - 1, j + 1) && !dough.uncut(i - 1, j + 2)))
          return true;
        return false;
      case 3:
        if (j + 3 == dough.side() - 1 ||
          (!dough.uncut(i + 1, j + 4) && !dough.uncut(i + 2, j + 4)))
          return true;
        return false;
    }
    return false;
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

  /**
   * Get the most aggressive move. Designed for 11 shapes
   * @param  moves: possible move to choose from
   */
  private Move aggressiveCut(Shape[] shapes, Shape[] opponent_shapes, ArrayList<Move> moves) {
    Move m1 = oppMoves.get(oppMoves.size() - 1);  // opponent last move

    Set<Point> aggressivePoints = getAggressivePoints(opponent_shapes[m1.shape].rotations()[m1.rotation], m1.point);
    for (Move move : moves) {
      Set<Point> ourPoints = getAggressivePoints(shapes[move.shape].rotations()[move.rotation], move.point);
      for (Point p : ourPoints) {
        if (aggressivePoints.contains(p)) {
          System.out.println("found aggressive move!");
          return move;
        }
      }
    }

    System.out.println("can't find aggressive move");
    return moves.get(0);
  }

  private int distance(Point p1, Point p2) {
    return (int) Math.sqrt(Math.pow((p1.i - p2.i),2) + Math.pow((p1.j - p2.j),2));
  }

  /**
   * Get the neighbors of a shape at point q
   */
  private Set<Point> getAggressivePoints(Shape shape, Point q) {
    Set<Point> res = new HashSet<Point>();
    for (Point p : shape) {
      Point target = new Point(p.i + q.i, p.j + q.j);
      Collections.addAll(res, target.neighbors());
    }

    return res;
  }

  /**
   * Get the last move of opponent, put it in lastDough
   * Return true if succeed, false otherwise
   */
  private boolean getOppMove(Dough dough, Shape[] opponent_shapes) {
    int mShape;
    int mRotation = -1;
    Point mPoint;

    ArrayList<Point> oppPoints = new ArrayList<>();
    Shape oppShape;

    // shape match between size and index
    HashMap<Integer, Integer> shapeMatch = new HashMap<>();
    shapeMatch.put(11, 0);
    shapeMatch.put(8, 1);
    shapeMatch.put(5, 2);

    boolean first = true;
    for (int x = 0; x < dough.side(); x++) {
      for (int y = 0; y < dough.side(); y++) {
        if (lastDough.uncut(x, y) && !dough.uncut(x, y)) {
          oppPoints.add(new Point(x, y));

          if (first) {
            mPoint = new Point(x, y);
            first = false;
          }
        }
      }
    }

    if (oppPoints.size() == 0) {
      System.out.println("Opp didn't cut");
      return false;
    }

    // find mShape
    mShape = shapeMatch.get(oppPoints.size());

    // find mRotation
    oppShape = new Shape(oppPoints.toArray(new Point[oppPoints.size()]));
    mRotation = findRotation(opponent_shapes[mShape], oppShape);

    // find mPoint
    int min_i = Integer.MAX_VALUE;
    int min_j = Integer.MAX_VALUE;
    int max_i = Integer.MIN_VALUE;
    int max_j = Integer.MIN_VALUE;
    for (Point p : oppPoints) {
      if (p.i < min_i) min_i = p.i;
      if (p.i > max_i) max_i = p.i;
      if (p.j < min_j) min_j = p.j;
      if (p.j > max_j) max_j = p.j;
    }
    mPoint = new Point(min_i, min_j);

    // cut on simulated dough
    lastDough.cut(opponent_shapes[mShape].rotations()[mRotation], mPoint);

    // System.out.println("Opp last move: ("+mShape+", "+mRotation+", "+mPoint+")");
    oppMoves.add(new Move(mShape, mRotation, mPoint));
    return true;
  }

  /**
   * Find if s2 is any rotation of s1.
   * If yes, return the rotation index. Otherwise return -1
   */
  private int findRotation(Shape s1, Shape s2) {
    Shape[] rotShapes = s1.rotations();
    for (int i = 0; i < 4; i++) {
      Set<Point> points1 = new HashSet<>();
      for (Point p : rotShapes[i]) {
        points1.add(p);
      }

      Set<Point> points2 = new HashSet<>();
      for (Point p : s2) {
        points2.add(p);
      }

      if (points1.equals(points2)) {
        return i;
      }
    }

    return -1;
  }

}
