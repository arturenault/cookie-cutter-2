package cc2.g2;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

	private Random gen = new Random();
	private static boolean tileStraight = true;
	int ri = 2;
	
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		return ShapeFactory.getNext(length);
	}
	
	protected static void tileDiagonal() {
		tileStraight = false;
	}
	
	private int getConvexHullMinSide(Shape eleven) {
		int minX, minY, maxX, maxY;
		minX = minY = Integer.MAX_VALUE;
		maxX = maxY = 0;
		Iterator<Point> iter = eleven.iterator();
		while(iter.hasNext()) {
			Point p = iter.next();
			if(p.i < minX) minX = p.i;
			if(p.i > maxX) maxX = p.i;
			if(p.j < minY) minY = p.j;
			if(p.j > maxY) maxY = p.j;
		}
		return Math.min(maxX - minX, maxY - minY) + 1;
	}

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		int opp_hull_min = getConvexHullMinSide(opponent_shapes[0]);
		
		int si = 0;
		
		// prune larger shapes if initial move
		
		if (dough.uncut()) {
			si = 2;
		}
		
		//Stack 11s
		Shape shape = shapes[si];
		Shape[] rotations = shape.rotations();
		
		int increment = 6;
		if(opp_hull_min == 2) {
			increment = 2;
		} else if(opp_hull_min <= 4) {
			increment = 4;
		}
		
		HashMap<Point, HashSet<Move>> points2moves = getAllPossibleMoves(dough, opponent_shapes);
		
		if(tileStraight) {
			for(int ri = 0; ri < rotations.length; ++ri) {
				for(int inc = 0; inc < increment/3+1; ++inc) {
					for(int j = increment-(inc*2+1); j < dough.side(); j += increment) {
						for(int i = 0; i < dough.side(); ++i) {
							Point p = new Point(i, j);
							Shape s = rotations[ri];
							if (dough.cuts(s, p)) {
								if(canOpponentInterfere(s, p, points2moves)) {
									System.out.println("r1");
									return new Move(si, ri, p);
								}
							}
						}
					}
				}
			}
		} else {
//			int hookType = getHookType(shape);
//			ri = 0;
//			int gap = getConvexHullMinSide(opponent_shapes[0]);
//			if(hookType == 1) {
//				int base_i = 39;
//				int base_j = 48;
//				while(base_i >= 0) {
//					int i = base_i;
//					int j = base_j;
//					base_i -= 11;
//					while(i >= 0 && j >= 0) {
//						Point p = new Point(i,j);
//						Shape s = rotations[ri];
//						if(dough.cuts(s, p))
//							if(canOpponentInterfere(s, p, points2moves))
//								return new Move(si, ri, p);
//						i -= 2;
//						j -= 2;
//					}
//				}
//				base_i = 39;
//				base_j = 30;
//				while(base_j >= 0) {
//					int i = base_i;
//					int j = base_j;
//					base_j -= 11;
//					while(i >= 0 && j >= 0) {
//						Point p = new Point(i,j);
//						Shape s = rotations[ri];
//						if(dough.cuts(s, p))
//							if(canOpponentInterfere(s, p, points2moves))
//								return new Move(si, ri, p);						
//						i -= 2;
//						j -= 2;
//					}
//				}
//			}
			
			for(int ri = 0; ri < rotations.length; ri += 2) {
				for(int inc = 7; inc > 0; --inc) {
					if(inc < 7) inc = 1;
					for(int j = 0; j < 2*dough.side(); j += inc) {
						for(int i = Math.max(0, j-dough.side()); j-i >= 0; i += 1) {
							Point p = new Point(i, j-i);
							Shape s = rotations[ri];
							if (dough.cuts(s, p)) {
								if(canOpponentInterfere(s, p, points2moves))
									return new Move(si, ri, p);
							}
						}
					}
				}
			}
		}
		
		//After there's no place left to stack
		for (si = 0; si < shapes.length; si++) {
			shape = shapes[si];
			for (int i = 0; i < dough.side(); i++) {
				for (int j = 0; j < dough.side(); j++) {
					rotations = shape.rotations();
					for (int ri = 0; ri < rotations.length; ri++) {
						Point p = new Point(i, j);
						Shape s = rotations[ri];
						if(si == 2 && i >= 45 && j >= 22) {
							System.out.println("shape: "+s.size());
							System.out.println("nrots: "+rotations.length);
							System.out.println(i+", "+j+": "+ri+": "+dough.cuts(s, p)+", "+canOpponentInterfere(s,p,points2moves));
						}
						if (dough.cuts(s, p)) {
							if(canOpponentInterfere(s, p, points2moves)) {
								System.out.println("r2");
								return new Move(si, ri, p);
							}
						}
					}
				}
			}
		}
		
		System.out.println("r3");
		
		return firstLargestCut(dough, shapes);
	}
	
	private boolean canOpponentInterfere(Shape ours, Point place, HashMap<Point, HashSet<Move>> points2moves) {
		for(Point p : ours) {
			Point affectedPoint = new Point(p.i + place.i, p.j + place.j);
			if(points2moves.containsKey(affectedPoint))
				return true;
		}
		return false;
	}
		
	public Move firstLargestCut(Dough dough, Shape[] shapes) {
		ArrayList <Move> moves = new ArrayList <Move> ();
		for(int si = 0; si < shapes.length; ++si) {
			if (shapes[si] == null)
				continue;
			for (int i = 0 ; i != dough.side() ; ++i) {
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point p = new Point(i, j);
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length ; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p))
							return new Move(si, ri, p);
					}
				}
			}
			if(moves.size() > 0)
				break;
		}
		return moves.get(gen.nextInt(moves.size()));
	}
	
	private int getHookType(Shape eleven) {
		Point hook = null;
		for(Point p : eleven) {
			if(p.j > 1)
				return 0;
			if(p.j == 1) {
				if(p.i != 0 && p.i != eleven.size()-2) {
					return 0;
				}
				if(hook != null) {
					return 0;
				}
				hook = p;
			}
		}
		if(hook == null)
			return 0;
		if(hook.i == 0)
			return 1;
		return -1;
	}
	
	public HashMap<Point, HashSet<Move>> getAllPossibleMoves(Dough dough, Shape[] shapes) {
		HashMap<Point, HashSet<Move>> points2moves = new HashMap<Point, HashSet<Move>>();
		for(int si = 0; si < shapes.length; ++si) {
			Shape[] rotations = shapes[si].rotations();
			for(int ri = 0; ri < rotations.length; ++ri) {
				for(int i = 0; i < dough.side(); ++i) {
					for(int j = 0; j < dough.side(); ++j) {
						Shape s = rotations[ri];
						Point p = new Point(i,j);
						if (dough.cuts(s, p)) {
							for(Point sp : s) {
								Point affectedPoint = new Point(sp.i + p.i, sp.j + p.j);
								if(!points2moves.containsKey(affectedPoint))
									points2moves.put(affectedPoint, new HashSet<Move>());
								points2moves.get(affectedPoint).add(new Move(si, ri, p));
							}
						}
					}
				}
			}
		}
		return points2moves;
	}
	
	private HashSet<Move> union(HashSet<Move> s1, HashSet<Move> s2) {
		if(s2.size() < s1.size()) {
			HashSet<Move> tmp = s2;
			s2 = s1;
			s1 = tmp;
		}
		HashSet<Move> res = new HashSet<Move>(s2.size());
		res.addAll(s2);
		for(Move m : s1) {
			res.add(m);
		}
		return res;
	}
	
	public int score(Dough dough, Shape[] opponent_shapes) {
		for(Shape nextLargestShape : opponent_shapes) {
			int nmoves = 0;
			for (int i = 0 ; i != dough.side() ; ++i) {
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point p = new Point(i, j);
					Shape[] rotations = nextLargestShape.rotations();
					for (int ri = 0 ; ri != rotations.length ; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p))
							nmoves++;
					}
				}
			}
			if(nmoves > 0)
				return nmoves*nextLargestShape.size();
		}
		return 0;
	}
}
