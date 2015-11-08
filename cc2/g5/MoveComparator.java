package cc2.g5;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Shape;

public class MoveComparator implements Comparator<MoveCosts> {
    public Shape[] self;
    public Shape[] oponent;
    public Dough dough;
    public ArrayList<Move> moveHistory;

    public MoveComparator(Shape[] self, Shape[] oponet, Dough dough, ArrayList<Move> moveHistory) {
        this.self = self;
        this.oponent = oponet;
        this.dough = dough;
        this.moveHistory = moveHistory;
    }

    @Override
    public int compare(MoveCosts o1, MoveCosts o2) {
        return o1.cost > o2.cost ? 1 : -1;
    }

    // Input required: Current dough, move to be made, set of all shapes(by opponent/ours) depending on the one we are computing for
    public int getOptMove(Move move, Shape[] shapes, Dough dough) {
        ModdableDough curr_dough = new ModdableDough(dough);
        ModdableDough cut_dough = cut_with_move(curr_dough, move, shapes);
        HashMap<Integer, ArrayList<Move>> move_set = Utils.generateMoves(cut_dough, shapes);
        int totalMoves = Utils.totalMoves(move_set);
        return totalMoves;
    }


    public ModdableDough cut_with_move(ModdableDough dough, Move move, Shape[] shapes) {
        try {
            Shape shape = shapes[move.shape].rotations()[move.rotation];
            dough.cut(shape, move.point);

        } catch (Exception e) {
            System.out.println(move.shape + " " + move.rotation + " len: " + shapes.length + "; " + shapes[2].rotations().length);

        }
        return dough;
    }
}
