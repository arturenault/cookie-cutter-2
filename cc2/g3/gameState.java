package cc2.g3;

import cc2.sim.Dough;
import cc2.sim.Shape;
import cc2.sim.Point;
import cc2.sim.Move;
import java.util.ArrayList;

public class gameState {

    public int score;
    public Dough board;
    public boolean our_turn;
    public Shape[] shapes;
    public Shape[] opponent_shapes;
    public int turns_played;
    public gameState previous_state;
    ArrayList<Move> move_history;

    public gameState( Dough board, boolean our_turn, Shape[] shapes, Shape[] opponent_shapes ) {
	this.board = board;
	this.shapes = shapes;
	this.opponent_shapes = opponent_shapes;
	this.our_turn = our_turn;
	this.turns_played = 0;
	move_history = new ArrayList<Move>();
    }

    public gameState copy() {
	Dough board_copy = new Dough(board.side());
	for (int i=0; i<board_copy.side(); i++) {
	    for (int j=0; j<board_copy.side(); j++) {
		if (!board.uncut(i,j)) {
		    board_copy.cut(new Shape(new Point[]{new Point(0,0)}), new Point(i,j));
		}
	    }
	}
	gameState output = new gameState(board_copy, this.our_turn, this.shapes, this.opponent_shapes);
	output.turns_played = this.turns_played;
	output.move_history = new ArrayList<Move>();
	for (int i=0; i<this.move_history.size(); i++) {
	    output.move_history.add(this.move_history.get(i));
	}
	return output;
    }

    public void nextTurn() {
	our_turn = !our_turn;
	turns_played++;
    }

    public void play(Move move) {
	if (our_turn) {	    
	    board.cut(shapes[move.shape].rotations()[move.rotation], move.point);
	}
	else {
	    board.cut(opponent_shapes[move.shape].rotations()[move.rotation], move.point);
	}
	move_history.add(move);
	nextTurn();
    }
    
}
