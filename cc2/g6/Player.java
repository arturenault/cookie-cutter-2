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
    private boolean fit_opponent;
    private long tick = -1;
    private int max_undec_moves = -1;
    private Set<Integer> attempted_octominoes = new HashSet<>();
    private Set<Integer> attempted_pentominoes = new HashSet<>();

    private Random rng = new Random(0);

    private MagicDough md = new MagicDough(50, 21);

    private Shape undecomino(Shape cutters[], Shape oppo_cutters[]) {
        Point p[] = new Point[11];

        // First attempt is to get a line
        if (cutter_attempts[UNDECOMINO] == 0) {
            for (int i = 0; i < 11; ++i) {
                p[i] = new Point(0, i);
            }
            return new Shape(p);
        }

        // Second attempt is a diagonal
        if (cutter_attempts[UNDECOMINO] == 1) {
            for(int i = 0; i < 11; ++i) {
                p[i] = new Point(6-i/2, (i/2+i%2));
            }
            return new Shape(p);
        }

        // Third attempt onwards are variants on an L
        p[0] = new Point(0, 0);

        for (int i = 1; i < 5; ++i) {
            p[2 * i] = new Point(0, i);
            p[2 * i - 1] = new Point(i, 0);
        }

        switch (cutter_attempts[UNDECOMINO]) {
            case 2:
                // symmetric L
                p[9] = new Point(0, 5);
                p[10] = new Point(5, 0);
                break;
            case 3:
                // long i
                p[9] = new Point(0, 5);
                p[10] = new Point(0, 6);
                break;
            case 4:
                // long j
                p[9] = new Point(5, 0);
                p[10] = new Point(6, 0);
                break;
            default:
                System.err.println("Should have only had five tries");
        }

        return new Shape(p);
    }

    private Shape octomino(Shape cutters[], Shape oppo_cutters[]) {
        System.out.println("Selecting octomino");

        int oct_id;
        Shape undec;

        // Try to fit our undecomino as first choice or if we can't get best one to match opponent's undecomino
        if (cutter_attempts[OCTOMINO] != 1) {
            undec = cutters[0];
            fit_opponent = true;
        } else { // Else attempt try to fit opponent's undecomino if we didn't get the best one of ours
            undec = oppo_cutters[0];
            fit_opponent = false;
        }

        assert (undec != null);

        oct_id = Util.findArgMin(0, Util.ALL_OCTOMINOES.length, (idx) -> {
            if (attempted_octominoes.contains(idx)) {
                return Integer.MAX_VALUE;
            }
            return -Util.evaluateTilingShape(undec, Util.getOctomino(idx), 11);
        });

        attempted_octominoes.add(oct_id);

        return Util.getOctomino(oct_id);
    }

    private Shape pentomino(Shape cutters[], Shape oppo_cutters[]) {
        System.out.println("Selecting pentomino");

        int pent_id;
        Shape undec;

        // Attempt to fit opponent undecomino if we made our 8 fit our 11
        if (fit_opponent) {
            undec = oppo_cutters[0];
        } else { // Else attempt to fit our undecomino if we made our 8 fit their 11
            undec = cutters[0];
        }

        assert (undec != null);

        pent_id = Util.findArgMin(0, Util.ALL_PENTOMINOES.length, (idx) -> {
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
        if (tick == 0) {
            md.setCutters(your_cutters, oppo_cutters);

            List<Move> undec_moves = Util.getValidMoves(dough, new Shape[]{your_cutters[0]}, new int[]{0});
            max_undec_moves = undec_moves.size();
        }

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

            Map<Point, Set<Move>> enemy_move_lookup = new HashMap<>();
            Map<Point, Set<Move>> move_lookup = new HashMap<>();

            for (int i = 0; i < dough.side(); ++i) {
                for (int j = 0; j < dough.side(); ++j) {
                    enemy_move_lookup.put(new Point(i, j), new HashSet<>());
                    move_lookup.put(new Point(i, j), new HashSet<>());
                }
            }

            List<Move> enemy_moves = Util.getValidMoves(dough, oppo_cutters, new int[]{0, 1, 2});

            for (Move m : enemy_moves) {
                Shape s = oppo_cutters[m.shape].rotations()[m.rotation];
                for (Point p : s) {
                    Point pp = new Point(p.i + m.point.i, p.j + m.point.j);
                    enemy_move_lookup.get(pp).add(m);
                }
            }

            List<Move> moves = Util.getValidMoves(dough, your_cutters, new int[]{0, 1, 2});

            for (Move m : moves) {
                Shape s = your_cutters[m.shape].rotations()[m.rotation];
                for (Point p : s) {
                    Point pp = new Point(p.i + m.point.i, p.j + m.point.j);
                    move_lookup.get(pp).add(m);
                }
            }

            double scores[] = new double[moves.size()];
            System.out.println("Evaluating " + moves.size() + " valid moves...");

            Move m = null; // output value

            for (int i = 0; i < moves.size(); ++i) {
                m = moves.get(i);

                Shape s = your_cutters[m.shape].rotations()[m.rotation];

                Set<Move> blockedEnemyMoves = new HashSet<>();
                Set<Move> blockedMoves = new HashSet<>();

                for (Point p : s) {
                    Point pp = new Point(p.i + m.point.i, p.j + m.point.j);

                    blockedEnemyMoves.addAll(enemy_move_lookup.get(pp));
                    blockedMoves.addAll(move_lookup.get(pp));
                }

                scores[i] = -s.size();

                if (blockedEnemyMoves.size() == 0) {
                    scores[i] += 10e6;
                }

                for (Move em : blockedEnemyMoves) {
                    scores[i] -= oppo_cutters[em.shape].size();
                }

                for (Move mm : blockedMoves) {
                    scores[i] += Math.sqrt(your_cutters[mm.shape].size());
                }
            }

            double minscore = Double.MAX_VALUE;
            double maxscore = Double.MIN_VALUE;

            m = null;

            for (int i = 0; i < moves.size(); ++i) {
                if (scores[i] < minscore) {
                    minscore = scores[i];
                    m = moves.get(i);
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
