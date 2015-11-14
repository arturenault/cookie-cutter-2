package cc2.g3;

import cc2.sim.Dough;
import cc2.sim.Shape;
import cc2.sim.Point;
import cc2.sim.Move;
import java.util.ArrayList;
import java.util.Iterator;

public class gameState {

    public int score;
    public Dough board;
    public boolean our_turn;
    public Shape[] shapes;
    public Shape[] opponent_shapes;
    public int turns_played;
    public gameState previous_state;
    ArrayList<Move> move_history;
    public int cuttable[][];
    public int opponent_cuttable[][];

    public gameState( Dough board, boolean our_turn, Shape[] shapes, Shape[] opponent_shapes ) {
	this.board = board;
	this.shapes = shapes;
	this.opponent_shapes = opponent_shapes;
	this.our_turn = our_turn;
	this.turns_played = 0;
	this.score = 0;
	this.move_history = new ArrayList<Move>();
	this.cuttable = new int[board.side()][board.side()];
	this.opponent_cuttable = new int[board.side()][board.side()];
    }

    // make a new gameState to be used in minimax search recursively
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
	/*output.cuttable = new int[board.side()][board.side()];
	output.opponent_cuttable = new int[board.side()][board.side()];
	for (int i=0; i<board.side(); i++) {
	    for (int j=0; j<board.side(); j++) {
		output.cuttable[i][j] = this.cuttable[i][j];
		output.opponent_cuttable[i][j] = this.opponent_cuttable[i][j];
	    }
	}*/
	output.turns_played = this.turns_played;
	output.move_history = new ArrayList<Move>();
	for (int i=0; i<this.move_history.size(); i++) {
	    output.move_history.add(this.move_history.get(i));
	}
	output.score = this.score;
	return output;
    }
    
    public void nextTurn() {
	our_turn = !our_turn;
	turns_played++;
    }

    public void computeCuttable() {
	computeCuttable(0,board.side(),0,board.side());
    }

    // score the board by determining the max cutter size that can claim a point on the grid
    public void computeCuttable(int istart, int iend, int jstart, int jend) {
	for (int i=istart; i<iend; i++) {
	    for (int j=jstart; j<jend; j++) {
		cuttable[i][j] = 0;
		opponent_cuttable[i][j] = 0;
	    }
	}
	for (int i = istart ; i < iend ; ++i) {
	    for (int j = jstart; j < jend ; ++j) {
		Point p = new Point(i, j);
		for (int si = 0 ; si <= 2 ; ++si) {
		    if (shapes[si] == null) continue;
		    Shape[] rotations = shapes[si].rotations();
		    for (int ri = 0 ; ri != rotations.length ; ++ri) {
			Shape s = rotations[ri];
			if (board.cuts(s,p)) {
			    Iterator<Point> it = s.iterator();
			    while (it.hasNext()) {
				Point q = it.next();
				int ii = q.i + p.i;
				int jj = q.j + p.j;
				cuttable[ii][jj] = Math.max(cuttable[ii][jj], pieceSize(si));
			    }
			}
		    }
		}
	    }
	}
	for (int i = istart ; i < iend ; ++i) {
	    for (int j = jstart; j < jend ; ++j) {
		Point p = new Point(i, j);
		for (int si = 0 ; si <= 2 ; ++si) {
		    if (opponent_shapes[si] == null) continue;
		    Shape[] rotations = opponent_shapes[si].rotations();
		    for (int ri = 0 ; ri != rotations.length ; ++ri) {
			Shape s = rotations[ri];
			if (board.cuts(s,p)) {
			    Iterator<Point> it = s.iterator();
			    while (it.hasNext()) {
				Point q = it.next();
				int ii = q.i + p.i;
				int jj = q.j + p.j;
				opponent_cuttable[ii][jj] = Math.max(opponent_cuttable[ii][jj], pieceSize(si));
			    }
			}
		    }
		}
 	    }
	}	
    }
    
    private int pieceSize(int index) {
	switch (index) {
	case 0: return 11;
	case 1: return 8;
	case 2: return 5;
	default: return 0;
	}
    }

    public gameState play(Move move) {
	gameState output = this.copy();
	Shape s;
	Point p = move.point;
	if (our_turn) {
	    output.board.cut(shapes[move.shape].rotations()[move.rotation], move.point);
	    s = shapes[move.shape].rotations()[move.rotation];
	}
	else {
	    output.board.cut(opponent_shapes[move.shape].rotations()[move.rotation], move.point);
	    s = opponent_shapes[move.shape].rotations()[move.rotation];
	}
	output.move_history.add(move);	
	output.nextTurn();
	//output.computeCuttable(Math.max(p.i - 11,0), Math.min(p.i + 22,board.side()-1), Math.max(p.j - 11,0), Math.min(p.j + 22,board.side()-1));
	output.computeCuttable();

	Iterator<Point> it = s.iterator();
	while (it.hasNext()) { // don't consider the active move as blocking the active player
	    Point q = it.next();
	    int i = q.i + p.i;
	    int j = q.j + p.j;
	    if (our_turn) {
		output.cuttable[i][j] = this.cuttable[i][j];
	    }
	    else {
		output.opponent_cuttable[i][j] = this.opponent_cuttable[i][j];
	    }
	}

	// subtract scores of the two boards before/after playing to compute the change in score
	int deltaScore = 0;
	for (int i=0; i<board.side(); i++) {
	    for (int j=0; j<board.side(); j++) {
		deltaScore -= this.cuttable[i][j] - output.cuttable[i][j];
		deltaScore += this.opponent_cuttable[i][j] - output.opponent_cuttable[i][j];
	    }
	}

	output.score = this.score + deltaScore;
	
	return output;
    }
    
}
