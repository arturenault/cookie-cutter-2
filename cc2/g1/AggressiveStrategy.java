package cc2.g1;

import java.util.ArrayList;
import java.util.Random;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Point;
import cc2.sim.Shape;

public class AggressiveStrategy implements ICutStrategy {
	private Random gen = new Random();

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{	
	    int[][][] count0;
	    int[][][] opponent_count0;
		int side = dough.side();
		count0 =new int [side][side][shapes.length];
		opponent_count0 =new int [side][side][shapes.length];
		for (int si = 0 ; si != shapes.length ; ++si) {
			for (int i = 0 ; i != dough.side() ; ++i){
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point q = new Point(i, j);
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, q)){
							for (Point p : s)
								count0[p.i+q.i][p.j+q.j][si]++;
						}
					}
					rotations = opponent_shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, q)){
							for (Point p : s)
								opponent_count0[p.i+q.i][p.j+q.j][si]++;
						}
					}
				}
			}
		}
		System.out.println();

		// prune larger shapes if initial move
		int minidx = -1;
		if (dough.uncut()) {
			int min = Integer.MAX_VALUE;
			for (Shape s : shapes)
				if (min > s.size())
					min = s.size();
			for (int s = 0 ; s != shapes.length ; ++s)
				if (shapes[s].size() == min)
					minidx = s;
		}
		// find all valid cuts
		ArrayList <Move> moves = new ArrayList <Move> ();
		int difference = Integer.MIN_VALUE;
		for (int si = 0 ; si != shapes.length ; ++si) {
			if (si != minidx && dough.uncut()) continue;
			for (int i = 0 ; i != dough.side() ; ++i){
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point p = new Point(i, j);
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p)) {
							int sum = 0;
							for (Point q : s){
								sum -= count0[p.i+q.i][p.j+q.j][0];
								sum -= count0[p.i+q.i][p.j+q.j][1];
								sum -= count0[p.i+q.i][p.j+q.j][2];
								sum += opponent_count0[p.i+q.i][p.j+q.j][0];
								sum += opponent_count0[p.i+q.i][p.j+q.j][1];
								sum += opponent_count0[p.i+q.i][p.j+q.j][2];
							}
							if (sum > difference){
								difference = sum;
								moves.clear();
								moves.add(new Move(si, ri, p));
							}
							else if (sum == difference){
								moves.add(new Move(si, ri, p));
							}
						}
					}
				}
			}
			if (moves.size() > 0)
				break;
		}
		// return a cut randomly
		Move rand_move = moves.get(gen.nextInt(moves.size()));
		return rand_move;
	}


}
