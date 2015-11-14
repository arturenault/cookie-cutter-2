package cc2.g7;

import cc2.g7.*;
import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;


public class Player implements cc2.sim.Player {
    private boolean[] row_2 = new boolean [0];
    private int[] row_2_pos;

    private Random gen = new Random();

    private Strategy strategy = null;

    private int[][][] count0;
    private int[][][] opponent_count0;
    private ArrayList<HashSet<Integer>> set0;
    private ArrayList<HashSet<Integer>> opponent_set0;
    public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
    {
        // check if first try of given cutter length
        Point[] cutter = new Point [length];
        if (row_2.length != cutter.length - 1) {
            // save cutter length to check for retries
            row_2 = new boolean [cutter.length - 1];
            row_2_pos = new int [cutter.length - 1];
            for(int i = 0 ;i < row_2.length/2; i ++) {
                if (length == 5) {
                    int y = -1;
                    for (Point p : shapes[0]) {
                        if (p.j == 1){
                            y = p.i;
                        }
                    }
                    if (y < row_2.length%2){
                        row_2_pos[2*i] = row_2.length-1-i;
                        row_2_pos[2*i+1] = i;
                    }
                    else {
                        row_2_pos[2*i] = i;
                        row_2_pos[2*i+1] = row_2.length-1-i;
                    }
                }
                else{
//					row_2_pos[2*i] = i;
//					row_2_pos[2*i+1] = row_2.length-1-i;
                    row_2_pos[2*i] = row_2.length-1-i;
                    row_2_pos[2*i+1] = i;
                }
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
        // select strategy
        if(strategy == null) {
            for (Shape opponent_shape : opponent_shapes) {
                if (opponent_shape.size() == 11) {
                    int min_i = Integer.MAX_VALUE;
                    int min_j = Integer.MAX_VALUE;
                    int max_i = Integer.MIN_VALUE;
                    int max_j = Integer.MIN_VALUE;
                    for (Point p : opponent_shape) {
                        if (min_i > p.i) min_i = p.i;
                        if (max_i < p.i) max_i = p.i;
                        if (min_j > p.j) min_j = p.j;
                        if (max_j < p.j) max_j = p.j;
                    }
                    if ((Math.abs(max_i - min_i) >= 9) || (Math.abs(max_j - min_j) >= 9)) {
                        strategy = Strategy.aggressive;
                        // set strategy
                    } else {
                        strategy = Strategy.set;
                    }
                }
            }
        }

        if(strategy == Strategy.set) {
            return useSetStrategy(dough, shapes, opponent_shapes);
        } else {
            return useAggressiveStrategy(dough, shapes, opponent_shapes);
        }
    }

    private Move useAggressiveStrategy(Dough dough, Shape[] shapes, Shape[] opponent_shapes) {
        System.out.println("useAggressiveStrategy");
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

//        System.out.println("Printing my count for 11 shape ");
//        for(int i=0;i<side;i++) {
//            for(int j=0;j<side;j++) {
//                System.out.print(count0[i][j][0]+" ");
//            }
//            System.out.println();
//        }

//        System.out.println("Printing opponents count for 11 shape ");

//        System.out.println();
//        for(int i=0;i<side;i++) {
//            for(int j=0;j<side;j++) {
//                System.out.print(opponent_count0[i][j][0]+" ");
//            }
//            System.out.println();
//        }

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
                            int sum = 0;
                            int sum11 = 0;
                            int sum8 = 0;
                            int sum5 = 0;
                            for (Point q : s){

                                sum11 -= count0[p.i+q.i][p.j+q.j][0];
                                sum8 -= count0[p.i+q.i][p.j+q.j][1];
                                sum5 -= count0[p.i+q.i][p.j+q.j][2];

                                sum11 += opponent_count0[p.i+q.i][p.j+q.j][0];
                                sum8 += opponent_count0[p.i+q.i][p.j+q.j][1];
                                sum5 += opponent_count0[p.i+q.i][p.j+q.j][2];
                            }
                            sum = sum11 + sum8 + sum5;
                            if (sum > difference){
                                difference = sum;
                                moves.clear();
                                moves.offer(new MoveWrapper(new Move(si, ri, p), sum11, sum8, sum5, 0));
                            }
                            else if (sum == difference){
                                moves.offer(new MoveWrapper(new Move(si, ri, p), sum11, sum8, sum5, 0));
                            }
                        }
                    }
                }
            }
            if (moves.size() > 0)
                break;
        }

        return moves.peek().move;

//        System.out.println("Size of the moves : " + moves.size());


//		if (!dough.uncut()) {
//			last_move = rand_move;
//		}
//        return rand_move;
    }

    private Move useSetStrategy(Dough dough, Shape[] shapes, Shape[] opponent_shapes) {
        System.out.println("useSetStrategy");
        int side = dough.side();
        set0 = new ArrayList<HashSet<Integer>>();
        opponent_set0 = new ArrayList<HashSet<Integer>>();
        for (int si = 0 ; si != shapes.length ; ++si) {
            for (int i = 0 ; i != side; ++i){
                for (int j = 0 ; j != side; ++j) {
                    set0.add(new HashSet<Integer>());
                    opponent_set0.add(new HashSet<Integer>());
                }
            }
        }
        for (int si = 0 ; si != shapes.length ; ++si) {
            for (int i = 0 ; i != side; ++i){
                for (int j = 0 ; j != side; ++j) {
                    Point q = new Point(i, j);
                    Shape[] rotations = shapes[si].rotations();
                    for (int ri = 0 ; ri != rotations.length; ++ri) {
                        Shape s = rotations[ri];
                        if (dough.cuts(s, q)){
                            for (Point p : s){
                                int idx;
                                idx = si * side * side + (p.i+q.i) * side + p.j+q.j;
                                set0.get(idx).add(ri*shapes.length*side*side + si * side * side + i * side +j);
                            }
                        }
                    }
                    rotations = opponent_shapes[si].rotations();
                    for (int ri = 0 ; ri != rotations.length; ++ri) {
                        Shape s = rotations[ri];
                        if (dough.cuts(s, q)){
                            for (Point p : s){
                                int idx;
                                idx = si * side * side + (p.i+q.i) * side + p.j+q.j;
                                opponent_set0.get(idx).add(ri*shapes.length*side*side + si*side*side + i*side + j);
                            }
                        }
                    }
                }
            }
        }

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
        PriorityQueue<MoveWrapper> moves = new PriorityQueue<MoveWrapper>(
                new Comparator<MoveWrapper>()
                {
                    public int compare(MoveWrapper x, MoveWrapper y )
                    {
                        if(y.cutter_size != x.cutter_size) {
                            return y.cutter_size - x.cutter_size;
                        }
                        else if ( (y.sum11 - x.sum11 ) != 0)
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
                            int sum = 0;
                            HashSet<Integer> s0 = new HashSet<Integer>();
                            HashSet<Integer> s1 = new HashSet<Integer>();
                            HashSet<Integer> s2 = new HashSet<Integer>();
                            HashSet<Integer> o0 = new HashSet<Integer>();
                            HashSet<Integer> o1 = new HashSet<Integer>();
                            HashSet<Integer> o2 = new HashSet<Integer>();
                            for (Point q : s){
                                int idx;
                                idx = 0 * side * side + (p.i+q.i) * side + p.j+q.j;
                                s0.addAll(set0.get(idx));
                                o0.addAll(opponent_set0.get(idx));
                                idx = 1 * side * side + (p.i+q.i) * side + p.j+q.j;
                                s1.addAll(set0.get(idx));
                                o1.addAll(opponent_set0.get(idx));
                                idx = 2 * side * side + (p.i+q.i) * side + p.j+q.j;
                                s2.addAll(set0.get(idx));
                                o2.addAll(opponent_set0.get(idx));
                            }
                            int sum11 = o0.size()*11 - s0.size()*11*11/s.size()/s.size();
                            int sum8 = o1.size()*8 - s1.size()*8*8/s.size()/s.size();
                            int sum5 = o2.size()*5 - s2.size()*5*5/s.size()/s.size();
                            sum = s.size() + o0.size()*11+o1.size()*8+o2.size()*5
                                    - (s0.size()*11*11/s.size()/s.size()+s1.size()*8*8/s.size()/s.size()+s2.size()*5*5/s.size()/s.size());

                            if (sum > difference){
                                difference = sum;
                                moves.clear();
                                moves.offer(new MoveWrapper(new Move(si, ri, p), sum11, sum8, sum5, s.size()));
                            }
                            else if (sum == difference){
                                moves.offer(new MoveWrapper(new Move(si, ri, p), sum11, sum8, sum5, s.size()));
                            }
                        }
                    }
                }
            }
        }

        return moves.peek().move;
    }

}
