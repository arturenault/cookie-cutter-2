package cc2.g7;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

	private boolean[] row_2 = new boolean [0];
	private int[] row_2_pos;

	private Random gen = new Random();
	
//	private Shape last_shape = null;
//	private Point last_pos = null;
//	private Move last_move = null;
//	private int[] transform = {2,0,0};

	private int[][][] count0;
	private int[][][] opponent_count0;
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		/*if (length == 11)
		{
			// Generate cutter of length 11
			Point[] cutter = new Point [length];
			cutter[0] = new Point(0, 0);
			cutter[1] = new Point(1, 0);
			cutter[2] = new Point(2, 0);
			cutter[3] = new Point(3, 0);
			cutter[4] = new Point(2, 1);
			cutter[5] = new Point(3, 1);
			cutter[6] = new Point(3, 2);
			cutter[7] = new Point(3, 3);
			cutter[8] = new Point(2, 3);
			cutter[9] = new Point(2, 4);
			cutter[10] = new Point(1, 4);
			return new Shape(cutter);
		}
		if (length == 8)
		{
			// Generate cutter of length 11
			Point[] cutter = new Point [length];
			cutter[0] = new Point(0, 1);
			cutter[1] = new Point(0, 2);
			cutter[2] = new Point(0, 3);
			cutter[3] = new Point(1, 1);
			cutter[4] = new Point(1, 2);
			cutter[5] = new Point(1, 3);
			cutter[6] = new Point(2, 2);
			cutter[7] = new Point(0, 4);
			return new Shape(cutter);
		}
		if (length == 5)
		{
			// Generate cutter of length 5
			Point[] cutter1 = new Point [length];
			cutter1[0] = new Point(0, 0);
			cutter1[1] = new Point(0, 1);
			cutter1[2] = new Point(0, 2);
			cutter1[3] = new Point(0, 3);
			cutter1[4] = new Point(0, 4);
			Shape shape1 = new Shape(cutter1);
			
			Point[] cutter2 = new Point [length];
			cutter2[0] = new Point(1, 0);
			cutter2[1] = new Point(1, 1);
			cutter2[2] = new Point(1, 2);
			cutter2[3] = new Point(1, 3);
			cutter2[4] = new Point(0, 3);
			Shape shape2 = new Shape(cutter2);
			
			
			if (row_2.length != 4) {
				row_2 = new boolean [4];
				return shape1;
			}
			else
			{
				Point[] cutter = new Point [length];
				int i;
				do {
					i = gen.nextInt(cutter.length - 1);
				} while (row_2[i]);
				row_2[i] = true;
				cutter[cutter.length - 1] = new Point(i, 1);
				for (i = 0 ; i != cutter.length - 1 ; ++i)
					cutter[i] = new Point(i, 0);
			}
		} */
			/*boolean sameAsOpponent = false;
			
			for (int i=0; i<opponent_shapes.length; i++)
			{
				if (opponent_shapes[i].equals(shape1))
				{
					sameAsOpponent = true;
				}
			}
			if (!sameAsOpponent)
			{
				return shape1;
			}
			else
			{
				return shape2;
			}
		}*/
		// check if first try of given cutter length
		Point[] cutter = new Point [length];
		if (row_2.length != cutter.length - 1) {
			// save cutter length to check for retries
			row_2 = new boolean [cutter.length - 1];
			row_2_pos = new int [cutter.length - 1];
			for(int i = 0 ;i < row_2.length/2; i ++) {
				//row_2_pos[2*i] = i;
				//row_2_pos[2*i+1] = row_2.length-1-i;
				row_2_pos[2*i] = row_2.length-1-i;
				row_2_pos[2*i+1] = i;
			}
			if (row_2.length%2 == 1)
				row_2_pos[row_2.length-1] = row_2.length/2;
			for (int i = 0 ; i != cutter.length ; ++i)
				cutter[i] = new Point(i, 0);
		} else {
			// pick a random cell from 2nd row but not same
			int i;
			int j=0;
			do {
				i = row_2_pos[j];
				j++;
			} while (row_2[i]);
			row_2[i] = true;
			cutter[cutter.length - 1] = new Point(i, 1);
			for (i = 0 ; i != cutter.length - 1 ; ++i)
				cutter[i] = new Point(i, 0);
		}
		return new Shape(cutter);
	}

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
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
					//Shape[] rotations = shapes[si].rotations();
//					int [][][] rotation_matrix = {{{1,0},{0,1}},{{0,1},{1,0}},{{-1,0},{0,-1}},{{0,-1},{-1,0}}};
//					for (int ri = 0 ; ri < 4; ++ri) {
//						Shape s = shapes[si];
//						int flag = 0;
//						for (Point p : s){
//							int x  = rotation_matrix[ri][0][0]*p.i+rotation_matrix[ri][0][1]*p.j;
//							int y  = rotation_matrix[ri][1][0]*p.i+rotation_matrix[ri][1][1]*p.j;
//							if (dough.uncut(x+q.i,y+q.j))
//								flag++;
//							else
//								break;
//						}
//						if (flag == s.size())
//							count0[q.i][q.j][si]++;
//					}
//					for (int ri = 0 ; ri < 4; ++ri) {
//						Shape s = opponent_shapes[si];
//						int flag = 0;
//						for (Point p : s){
//							int x  = rotation_matrix[ri][0][0]*p.i+rotation_matrix[ri][0][1]*p.j;
//							int y  = rotation_matrix[ri][1][0]*p.i+rotation_matrix[ri][1][1]*p.j;
//							if (dough.uncut(x+q.i,y+q.j))
//								flag++;
//							else
//								break;
//						}
//						if (flag == s.size())
//							opponent_count0[q.i][q.j][si]++;
//					}
				}
			}
		}
//		for(int i=0;i<side;i++) {
//			for(int j=0;j<side;j++) {
//				System.out.print(count[i][j][0]+" ");
//			}
//			System.out.println();
//		}
//		System.out.println();
		/*for(int i=0;i<side;i++) {
			for(int j=0;j<side;j++) {
				System.out.print(opponent_count0[i][j][0]+" ");
			}
			System.out.println();
		}*/
		System.out.println();
//		if (!dough.uncut()) {
//			if (last_move != null) {
//				Shape[] rotations = shapes[last_move.shape].rotations();
//				Shape s = rotations[last_move.rotation];
//				int min_i = Integer.MAX_VALUE;
//				int min_j = Integer.MAX_VALUE;
//				int max_i = Integer.MIN_VALUE;
//				int max_j = Integer.MIN_VALUE;
//				for (Point p : s) {
//					if (min_i > p.i) min_i = p.i;
//					if (max_i < p.i) max_i = p.i;
//					if (min_j > p.j) min_j = p.j;
//					if (max_j < p.j) max_j = p.j;
//				}
//				int[] transform = new int[2];
//				if (max_i > max_j) {
//					transform[0]= 0;
//					transform[1]= 2>max_i+1?2:max_i+1;
//				}
//				else {
//					transform[0]= 2>max_i+1?2:max_i+1;
//					transform[1]= 0;
//				}
//
//				Point pos = new Point(last_move.point.i+transform[0],last_move.point.j+transform[1]);
//				if (dough.cuts(s, pos)) {
//					last_move = new Move(last_move.shape,last_move.rotation,pos);
//					return (last_move);
//				}
//			}
//		}
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
							//Dough doughtmp = new Dough(dough.side());
							//doughtmp.cut(s, p);
							//int value = searchValue(dough,doughtmp,shapes,opponent_shapes);
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
//							if (value > difference){
//								moves.clear();
//								moves.add(new Move(si, ri, p));
//							}
//							else if (value == difference){
//								moves.add(new Move(si, ri, p));
//							}
						}
					}
				}
			}
			if (moves.size() > 0)
				break;
		}
		// return a cut randomly
		Move rand_move = moves.get(gen.nextInt(moves.size()));
//		if (!dough.uncut()) {
//			last_move = rand_move;
//		}
		return rand_move;
	}
//	private int searchValue (Dough dough, Dough doughtmp, Shape[] shapes, Shape[] opponent_shapes){
//		int value = 0;
//		int side = dough.side();
//		int[][][] count =new int [side][side][shapes.length];
//		int[][][] opponent_count =new int [side][side][shapes.length];
//		for (int si = 0 ; si != shapes.length ; ++si) {
//			for (int i = 0 ; i != dough.side() ; ++i){
//				for (int j = 0 ; j != dough.side() ; ++j) {
//					//Shape[] rotations = shapes[si].rotations();
//					int [][][] rotation_matrix = {{{1,0},{0,1}},{{0,1},{1,0}},{{-1,0},{0,-1}},{{0,-1},{-1,0}}};
//					for (int ri = 0 ; ri < 4; ++ri) {
//						Shape s = shapes[si];
//						int flag = 0;
//						for (Point p : s){
//							int x  = rotation_matrix[ri][0][0]*p.i+rotation_matrix[ri][0][1]*p.j;
//							int y  = rotation_matrix[ri][1][0]*p.i+rotation_matrix[ri][1][1]*p.j;
//							if (dough.uncut(x+i,y+j) && doughtmp.uncut(x+i,y+j))
//								flag++;
//							else
//								break;
//						}
//						if (flag == s.size())
//							count[i][j][si]++;
//						
//					}
//					//value -=shapes[si].size()*(count0[i][j][si]-count[i][j][si]);
//					for (int ri = 0 ; ri < 4; ++ri) {
//						Shape s = opponent_shapes[si];
//						int flag = 0;
//						for (Point p : s){
//							int x  = rotation_matrix[ri][0][0]*p.i+rotation_matrix[ri][0][1]*p.j;
//							int y  = rotation_matrix[ri][1][0]*p.i+rotation_matrix[ri][1][1]*p.j;
//							if (dough.uncut(x+i,y+j) && doughtmp.uncut(x+i,y+j))
//								flag++;
//							else
//								break;
//						}
//						if (flag == s.size())
//							opponent_count[i][j][si]++;
//					}
//					value +=shapes[si].size()*(opponent_count0[i][j][si]-opponent_count[i][j][si]);
//				}
//			}
//		}
//		return 0;
//	}
}
