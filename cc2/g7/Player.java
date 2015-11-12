package cc2.g7;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;


public class Player implements cc2.sim.Player {

	private static final String MoveWrapper = null;
	private boolean[] row_2 = new boolean [0];
	private int[] row_2_pos;

	private Random gen = new Random();
	
//	private Shape last_shape = null;
//	private Point last_pos = null;
//	private Move last_move = null;
//	private int[] transform = {2,0,0};
	
	BufferedWriter bWriter = null;
	
	boolean sameShape = false;
	

	private int[][][] count0;
	private int[][][] opponent_count0;
	private ArrayList<HashSet<Integer>> set0;
	private ArrayList<HashSet<Integer>> opponent_set0;
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		//riter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("filename.txt"), "utf-8"))) {
	   //writer.write("something");    
	
		
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
//				row_2_pos[2*i] = i;
//				row_2_pos[2*i+1] = row_2.length-1-i;
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
			
			if (cutter.length == 11)
			{
				sameShape = true;
			}
		}
		return new Shape(cutter);
	}

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		int side = dough.side();
		count0 =new int [side][side][shapes.length];
		opponent_count0 =new int [side][side][shapes.length];
		for (int si = 0 ; si != shapes.length ; ++si) {
			for (int i = 0 ; i != side ; ++i){
				for (int j = 0 ; j != side ; ++j) {
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
//		set0 = new ArrayList<HashSet<Integer>>();
//		opponent_set0 = new ArrayList<HashSet<Integer>>();
//		for (int si = 0 ; si != shapes.length ; ++si) {
//			for (int i = 0 ; i != side; ++i){
//				for (int j = 0 ; j != side; ++j) {
//					set0.add(new HashSet<Integer>());
//					opponent_set0.add(new HashSet<Integer>());
//				}
//			}
//		}
//		for (int si = 0 ; si != shapes.length ; ++si) {
//			for (int i = 0 ; i != side; ++i){
//				for (int j = 0 ; j != side; ++j) {
//					Point q = new Point(i, j);
//					Shape[] rotations = shapes[si].rotations();
//					for (int ri = 0 ; ri != rotations.length; ++ri) {
//						Shape s = rotations[ri];
//						if (dough.cuts(s, q)){
//							for (Point p : s){
//								int idx;
//								idx = si * side * side + (p.i+q.i) * side + p.j+q.j;
//								set0.get(idx).add(ri*shapes.length*side*side + si * side * side + i * side +j);
//								//count0[p.i+q.i][p.j+q.j][si]++;
//							}
//						}
//					}
//					rotations = opponent_shapes[si].rotations();
//					for (int ri = 0 ; ri != rotations.length; ++ri) {
//						Shape s = rotations[ri];
//						if (dough.cuts(s, q)){
//							for (Point p : s){
//								int idx;
//								idx = si * side * side + (p.i+q.i) * side + p.j+q.j;
//								opponent_set0.get(idx).add(ri*shapes.length*side*side + si*side*side + i*side + j);
//								//count0[p.i+q.i][p.j+q.j][si]++;
//							}
//						}
//					}
//				}
//			}
//		}
		
		System.out.println("Printing my count for 11 shape ");
		for(int i=0;i<side;i++) {
			for(int j=0;j<side;j++) {
				System.out.print(count0[i][j][0]+" ");
			}
			System.out.println();
		}
		
		System.out.println("Printing opponents count for 11 shape ");
		
		System.out.println();
		for(int i=0;i<side;i++) {
			for(int j=0;j<side;j++) {
				System.out.print(opponent_count0[i][j][0]+" ");
			}
			System.out.println();
		}
//		System.out.println();
//		for(int i=0;i<side;i++) {
//		for(int j=0;j<side;j++) {
//			System.out.print(set0.get ( i * side +j).size()+" ");
//		}
//			System.out.println();
//		}
//		System.out.println();
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
		//ArrayList <Move> moves = new ArrayList <Move> ();
		
		
		PriorityQueue<MoveWrapper> moves = new PriorityQueue<MoveWrapper>(
				new Comparator<MoveWrapper>()
				{
					public int compare( MoveWrapper x, MoveWrapper y )
			        {
			            if ( (y.sum11 - x.sum11 ) != 0)
			            {
			            	return (y.sum11 - x.sum11);
			            }
			            else if ( (y.sum8 - x.sum8 ) != 0)
			            {
			            	return (y.sum8 - x.sum8);
			            }
			            else if ( (y.sum5 - x.sum5 ) != 0)
			            {
			            	return (y.sum5 - x.sum5);
			            }
			            else
			            {
			            	return 0;
			            }
			        }
				});
		
		
		int difference = Integer.MIN_VALUE;
		for (int si = 0 ; si != shapes.length ; ++si) {
			if (si != minidx && dough.uncut()) continue;
			for (int i = 0 ; i != side ; ++i){
				for (int j = 0 ; j != side ; ++j) {
					Point p = new Point(i, j);
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p)) {
							//Dough doughtmp = new Dough(side);
							//doughtmp.cut(s, p);
							//int value = searchValue(dough,doughtmp,shapes,opponent_shapes);
							//int sum = s.size();
							int sum = 0;
							int sum11 = 0;
							int sum8 = 0;
							int sum5 = 0;
							
//							HashSet<Integer> s0 = new HashSet<Integer>();
//							HashSet<Integer> s1 = new HashSet<Integer>();
//							HashSet<Integer> s2 = new HashSet<Integer>();
//							HashSet<Integer> o0 = new HashSet<Integer>();
//							HashSet<Integer> o1 = new HashSet<Integer>();
//							HashSet<Integer> o2 = new HashSet<Integer>();
							for (Point q : s){
								//sum -= count0[p.i+q.i][p.j+q.j][0]*11/s.size();
								//sum -= count0[p.i+q.i][p.j+q.j][1]*8/s.size();
								//sum -= count0[p.i+q.i][p.j+q.j][2]*5/s.size();
								
								if (sameShape)
								{
									sum11 -= count0[p.i+q.i][p.j+q.j][0];
									sum8 -= count0[p.i+q.i][p.j+q.j][1];
									sum5 -= count0[p.i+q.i][p.j+q.j][2];
								}
								
								sum11 += opponent_count0[p.i+q.i][p.j+q.j][0];
								sum8 += opponent_count0[p.i+q.i][p.j+q.j][1];
								sum5 += opponent_count0[p.i+q.i][p.j+q.j][2]; 
								
								
								//sum -= count0[p.i+q.i][p.j+q.j][si];
								//sum += opponent_count0[p.i+q.i][p.j+q.j][si];
								
								
//								int idx;
//								idx = 0 * side * side + (p.i+q.i) * side + p.j+q.j;
//								s0.addAll(set0.get(idx));
//								o0.addAll(opponent_set0.get(idx));
//								idx = 1 * side * side + (p.i+q.i) * side + p.j+q.j;
//								s1.addAll(set0.get(idx));
//								o1.addAll(opponent_set0.get(idx));
//								idx = 2 * side * side + (p.i+q.i) * side + p.j+q.j;
//								s1.addAll(set0.get(idx));
//								o2.addAll(opponent_set0.get(idx));
							}
//							sum = s.size() + o0.size()*11+o1.size()*8+o2.size()*5
//									- (s0.size()*11*11/s.size()+s1.size()*8*8/s.size()+s2.size()*5*5/s.size());
							
							sum = sum11 + sum8 + sum5;
							if (sum > difference){
								difference = sum;
								moves.clear();
								//moves.add(new Move(si, ri, p));
								moves.offer(new MoveWrapper(new Move(si, ri, p), sum11, sum8, sum5));
								
							}
							else if (sum == difference){
								moves.offer(new MoveWrapper(new Move(si, ri, p), sum11, sum8, sum5));
								//moves.add(new Move(si, ri, p));
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
		
		//Move rand_move = moves.get(gen.nextInt(moves.size()));
		
		Move rand_move = moves.peek().move;
		
		System.out.println("Size of the moves : " + moves.size());
		
		
//		if (!dough.uncut()) {
//			last_move = rand_move;
//		}
		return rand_move;
	}

}
