package cc2.g6;

import cc2.sim.Point;
import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Shape;

import java.util.*;

/**
 * Created by rbtying on 10/26/15.
 */
public class Player implements cc2.sim.Player {

    private static final int UNDECOMINO = 0;
    private static final int OCTOMINO = 1;
    private static final int PENTOMINO = 2;


    private int cutter_attempts[] = new int[3];
    private long tick = -1;
    private Set<Integer> attempted_octominoes = new HashSet<>();
    private Set<Integer> attempted_pentominoes = new HashSet<>();

    private Random rng = new Random(0);

    private MagicDough md = new MagicDough(50);

    private Shape undecomino(Shape cutters[], Shape oppo_cutters[]) {
        // try to make an L
        // make most of the L using the first 9 points
        // *****
        // *
        // *
        // *
        // *
        // *
        Point p[] = new Point[11];
        p[0] = new Point(0, 0);

        for (int i = 1; i < 5; ++i) {
            p[2 * i] = new Point(0, i);
            p[2 * i - 1] = new Point(i, 0);
        }

        switch (cutter_attempts[UNDECOMINO]) {
            case 0:
                // symmetric L
                p[9] = new Point(0, 5);
                p[10] = new Point(5, 0);
                break;
            case 1:
                // long i
                p[9] = new Point(0, 5);
                p[10] = new Point(0, 6);
                break;
            case 2:
                // long j
                p[9] = new Point(5, 0);
                p[10] = new Point(6, 0);
                break;
            case 3:
                // center point and j
                p[9] = new Point(1, 1);
                p[10] = new Point(5, 0);
                break;
            case 4:
                // center point and i
                p[9] = new Point(1, 1);
                p[10] = new Point(0, 5);
                break;
            default:
                System.err.println("Should have only had five tries");
        }

        return new Shape(p);
    }

    private Shape octomino(Shape cutters[], Shape oppo_cutters[]) {
        // try to fit our undecomino
        System.out.println("Selecting octomino");
        Shape undec = cutters[0];
        assert (undec != null);

        int oct_id = Util.findArgMin(0, Util.ALL_OCTOMINOES.length, (idx) -> {
            // minimize total manhattan distance between shapes
            if (attempted_octominoes.contains(idx)) {
                return Integer.MAX_VALUE;
            }

            return -Util.evaluateTilingShape(undec, Util.getOctomino(idx), 11);
        });

        attempted_octominoes.add(oct_id);

        return Util.getOctomino(oct_id);
    }

    private Shape pentomino(Shape cutters[], Shape oppo_cutters[]) {
        // try to fit their undecomino
        System.out.println("Selecting pentomino");
        Shape undec = oppo_cutters[0];
        assert (undec != null);

        int pent_id = Util.findArgMin(0, Util.ALL_PENTOMINOES.length, (idx) -> {
            // minimize total manhattan distance between shapes
            if (attempted_pentominoes.contains(idx)) {
                return Integer.MAX_VALUE;
            }
            return -Util.evaluateTilingShape(undec, Util.getPentomino(idx), 11);
        });

        attempted_pentominoes.add(pent_id);

        return Util.getPentomino(pent_id);
    }

    @Override
    public Shape cutter(int length, Shape[] your_cutters, Shape[] oppo_cutters) {
        Shape s = null;
        switch (length) {
            case 11:
                s = undecomino(your_cutters, oppo_cutters);
                ++cutter_attempts[UNDECOMINO];
                break;
            case 8:
                s = octomino(your_cutters, oppo_cutters);
                ++cutter_attempts[OCTOMINO];
                break;
            case 5:
                s = pentomino(your_cutters, oppo_cutters);
                ++cutter_attempts[PENTOMINO];
                break;
            default:
                System.err.println("Unexpected length!");
        }
        return s;
    }

    @Override
    public Move cut(Dough dough, Shape[] your_cutters, Shape[] oppo_cutters) {
        ++tick;

        md.diffFromDough(dough);

        if (dough.uncut()) {
            // can only use min
            int si = Util.findArgMin(0, your_cutters.length, (i) -> your_cutters[i].size());

            for (Move m : Util.getValidMoves(dough, your_cutters[si], si)) {
                if (m.point.i > 0 && m.point.j > 0) {
                    return m;
                }
            }
        } else {

            List<Move> undec_moves = Util.getValidMoves(dough, new Shape[]{your_cutters[0]}, new int[]{0});
            List<Move> oct_moves = Util.getValidMoves(dough, new Shape[]{your_cutters[1]}, new int[]{1});
            List<Move> pent_moves = Util.getValidMoves(dough, new Shape[]{your_cutters[2]}, new int[]{2});

            //List<Move> all_valid_moves = Util.getValidMoves(dough, your_cutters);

            List<Move> all_valid_moves = new ArrayList<>();
            all_valid_moves.addAll(undec_moves);
            all_valid_moves.addAll(oct_moves);
            if (undec_moves.isEmpty()) {
                all_valid_moves.addAll(pent_moves);
            }

            Map<MagicDough.Window, Long> pre_move_scores = md.scoreAllWindows(oppo_cutters);
            long scores[] = new long[all_valid_moves.size()];

            System.out.println("Evaluating " + all_valid_moves.size() + " valid moves...");

            for (int i = 0; i < all_valid_moves.size(); ++i) {
                Move m = all_valid_moves.get(i);
                MagicDough.Window w = md.effectWindow(m.point.i, m.point.j);

                if (pre_move_scores.get(w) == 0) {
                    scores[i] = 0;
                    continue;
                }

                if (md.makeMove(m, your_cutters, false)) {
                    long score = md.countSpaces(w, oppo_cutters);
                    md.undoLastMove(your_cutters);
                    scores[i] = score - pre_move_scores.get(w);
                } else {
                    scores[i] = 0;
                }
            }
            // most negative is best

            long minscore = Long.MAX_VALUE;
            long maxscore = Long.MIN_VALUE;
            Move m = null;
            for (int i = 0; i < all_valid_moves.size(); ++i) {
                if (scores[i] < minscore) {
                    minscore = scores[i];
                    m = all_valid_moves.get(i);
                }

                if (scores[i] > maxscore) {
                    maxscore = scores[i];
                }
            }

            System.out.println("Picking move with score " + minscore);

            md.makeMove(m, your_cutters, false);

            return m;
        }
        return null;
    }
}
