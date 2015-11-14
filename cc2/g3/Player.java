package cc2.g3;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.g3.gameState;
import cc2.g3.rectDough;

import java.util.*;

public class Player implements cc2.sim.Player {

    private static final int SIDE = 50;

    private boolean[] row_2 = new boolean [0];

    private Random gen = new Random();

    private Dough opponent, self;

    private boolean denied;
   
    private int minimax_cutter_index;
    private int minimax_search_depth;
    private int switch_cutter_threshold;
    private int switch_depth_threshold;
    private int switch_strategy_threshold;
    private boolean use_minimax;

    public Player() {
	opponent = new Dough(SIDE);
	self = new Dough(SIDE);
	denied = false; // if opponent's 11 piece has a nice fitting companion, whether we've denied them that piece
	minimax_cutter_index = 0; // only start off searching through 11-piece space
	minimax_search_depth = 1; // 2 is very slow initially
	switch_depth_threshold = 0; // don't use depth-2 search, it's not good
	switch_strategy_threshold = 1000; // only if against another line team. abandon blocking their convex hull prematurely. 
	switch_cutter_threshold = 10; // only if against another line team. playing smaller pieces to block lines isn't worth it.
	use_minimax = false; // only used if against another line team
    }    

    public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
    {
	if (opponent_shapes.length == 0 || getMinWidth(opponent_shapes[0]) <= 2 || denied == true) {
		// return randLinearCutter(length, shapes, opponent_shapes);
		return linearCutter(length);
	}
	Point dimensions = getBoundingBox(opponent_shapes[0]);
	rectDough space = new rectDough(dimensions);
	space.cut(opponent_shapes[0], new Point(0,0));
	Stack<Point> conn_comp = new Stack<Point>();

	while (!space.saturated()) { // analyze convex hull of opponent's 11-piece and try to block their smaller pieces
	    Point init = findAvailablePoint(space);
	    conn_comp.push(init);
	    ArrayList<Point> points = new ArrayList<Point>(); 
	    points.add(init);
	    space.cut(init);
	    while (!conn_comp.isEmpty()) { 
		Point next = conn_comp.pop();
		Point[] neighbors = next.neighbors();
		for (int i=0; i<neighbors.length; i++) {
		    if (space.uncut(neighbors[i])) {
			conn_comp.push(neighbors[i]);
			space.cut(neighbors[i]);
			points.add(neighbors[i]);
		    }
		}
	    }
	    if (points.size() == length) { 
		System.out.println("Denied!");
		denied = true;
		Point[] cutter = new Point[points.size()];
		return new Shape(points.toArray(cutter));
	    }
	}
	// return randLinearCutter(length, shapes, opponent_shapes);
	return linearCutter(length);
    }

    // find a cuttable point to start a BFS with
    private Point findAvailablePoint(rectDough space) {
	int h = space.height;
	int w = space.width;
	for (int i=0; i<h; i++) {
	    for (int j=0; j<w; j++) {
		if (space.uncut(i,j)) {return new Point(i,j);}
	    }
	}
	System.out.println("No available points");
	return null;
    }

	HashMap<Integer, Integer> shape_tries = new HashMap<>();
	// Returns for any size a stick the first time, and then a hockey shape moving the extra piece in gradually.
	public Shape linearCutter(int length) {
		Point[] points = new Point[length];
		for (int i = 0; i < length-1; i++) {
			points[i] = new Point(i, 0);
		}

		int tries = shape_tries.getOrDefault(length, 0);
		if (tries == 0) {
			points[length-1] = new Point(length-1, 0);
		} else if (tries % 2 != 0){
			points[length-1] = new Point(tries/2, 1);
		} else {
			points[length-1] = new Point(length-tries/2-1, 1);
		}
		shape_tries.put(length, tries + 1);
		return new Shape(points);
	}

    // default cutter generation
    public Shape randLinearCutter(int length, Shape[] shapes, Shape[] opponent_shapes) {
	// check if first try of given cutter length
	Point[] cutter = new Point [length];
	if (row_2.length != cutter.length - 1) {
	    // save cutter length to check for retries
	    row_2 = new boolean [cutter.length - 1];
	    for (int i = 0 ; i != cutter.length ; ++i)
		cutter[i] = new Point(i, 0);
	} else {
	    // pick a random cell from 2nd row but not same
	    int i = 0;
	    int n = cutter.length-1;
	    do {
		i = gen.nextInt(n*10000); //bias towards endpoints
		if (i < 4999*n) {i = 0;}
		else if (i >= 5000*n) {i = n-1;}
		else {i = i - 4999*n;}
	    } while (row_2[i]);
	    row_2[i] = true;
	    cutter[cutter.length - 1] = new Point(i, 1);
	    for (i = 0 ; i != cutter.length - 1 ; ++i)
		cutter[i] = new Point(i, 0);
	}
	return new Shape(cutter);
    }

	// Getting the board for greedy. The number on each grid is number of opponent move on it minus
	// number of own move on it.
	private int[][] getScoreBoard(Dough d, Shape[] shapes, Shape[] opponent_shapes) {
		int[][] score = new int[d.side()][d.side()];
		for (int i = 0 ; i != SIDE ; ++i) {
			for (int j = 0 ; j != SIDE ; ++j) {
				Point p = new Point(i, j);
				for (int si = 0 ; si <= 2 ; ++si) {
					if (shapes[si] != null) {
						Shape[] rotations = shapes[si].rotations();
						for (int ri = 0 ; ri != rotations.length ; ++ri) {
							Shape s = rotations[ri];
							if (d.cuts(s,p)) {
								for (Point sp: s) {
									score[i + sp.i][j + sp.j] --;
								}
							}
						}
					}
					if (opponent_shapes[si] != null) {
						Shape[] rotations = opponent_shapes[si].rotations();
						for (int ri = 0; ri != rotations.length; ++ri) {
							Shape s = rotations[ri];
							if (d.cuts(s, p)) {
								for (Point sp: s) {
									score[i + sp.i][j + sp.j] ++;
								}
							}
						}
					}
				}
			}
		}
		return score;
	}

	// Score of a move. It is sum of all the number on the score. Larger means obstructing more opponent moves.
	private int getScoreOfMove(int[][] scoreBoard, Shape[] shapes, Move m) {
		Shape s = shapes[m.shape].rotations()[m.rotation];
		Point p = m.point;

		int ret = 0;
		for (Point sp: s) {
			ret += scoreBoard[p.i + sp.i][p.j + sp.j];
		}
		return ret;
	}

    private void set_minimax_pieces(int index) {
	minimax_cutter_index = Math.min(index,2);
    }

    private void set_minimax_depth(int depth) {
	minimax_search_depth = depth;
    }

    // minimax or greedy search (depth 2 or depth 1)
    private gameState minimax(gameState initial_state, int searchDepth, int maxCutterIndex) {
	initial_state.computeCuttable();
	int bestScore = Integer.MIN_VALUE;
	gameState bestStrategy = initial_state.copy();
	Stack<gameState> gameTree = new Stack<gameState>();
	gameTree.push(initial_state);

	while (!gameTree.isEmpty()) {
	    gameState state = gameTree.pop();
	    ArrayList<Move> moves = find_possible_moves(state, maxCutterIndex);
	    if (moves.size() < switch_cutter_threshold && maxCutterIndex != 2) {
		set_minimax_pieces(maxCutterIndex+1);
		set_minimax_depth(1);
	    }
	    else if (moves.size() < switch_depth_threshold) { // when already using all pieces, or when playing vs line (where switch_cutter_threshold = 10)
		set_minimax_depth(2);
	    }
	    Iterator<Move> it = moves.iterator();

	    if (state.turns_played == 0) { //play all our possible moves
		while (it.hasNext()) {		
		    gameState next_state = state.play(it.next());
		    gameTree.push(next_state);
		}
	    }
	    else if (state.turns_played == 1 && searchDepth == 2) { //depth 2 minimax with alpha-beta pruning
		int branchScore = Integer.MAX_VALUE;
		while (it.hasNext() && branchScore > bestScore) {
		    gameState next_state = state.play(it.next());
		    branchScore = Math.min(branchScore, next_state.score);
		}

		if (branchScore > bestScore) {
		    bestScore = branchScore;
		    bestStrategy = state.copy();
		}
	    }
	    else if (state.turns_played == 1 && searchDepth == 1) { //depth 1 greedy
		if (state.score > bestScore) {
		    bestScore = state.score;
		    bestStrategy = state.copy();
		}
	    }
	    else {
		System.out.println("Fail");
		return null;
	    }
	}
	return bestStrategy;
    }
    
    private int getMinWidth(Shape cutter) {
	Point b = getBoundingBox(cutter);
	return Math.min( b.i,b.j );
    }

    // get convex hull of opponent's 11 piece
    private Point getBoundingBox(Shape cutter) {
	int minI = Integer.MAX_VALUE;
	int minJ = Integer.MAX_VALUE;
	int maxI = Integer.MIN_VALUE;
	int maxJ = Integer.MIN_VALUE;
	Iterator<Point> pointsInShape = cutter.iterator();
	while (pointsInShape.hasNext()) {
	    Point p = pointsInShape.next();
	    minI = Math.min(minI, p.i);
	    maxI = Math.max(maxI, p.i);
	    minJ = Math.min(minJ, p.j);
	    maxJ = Math.max(maxJ, p.j);
	}
	return new Point(maxI - minI + 1, maxJ - minJ + 1);
    }

    public ArrayList<Move> find_possible_moves(gameState state, boolean our_turn, int maxCutterIndex) {
	Dough dough = state.board;
	ArrayList <Move> moves = new ArrayList <Move> ();
	Shape[] search_space;
	if (our_turn) {
	    search_space = state.shapes;
	}
	else {
	    search_space = state.opponent_shapes;
	}
	int bound = SIDE;
	if (dough.uncut()) {
	    bound = 15;
	}
	while (moves.isEmpty() ) {	
	    for (int i = 0 ; i != bound ; ++i) {
		for (int j = 0 ; j != bound ; ++j) {
		    Point p = new Point(i, j);
		    for (int si = 0 ; si <= maxCutterIndex ; ++si) {
			if (search_space[si] == null) continue;
			Shape[] rotations = search_space[si].rotations();
			for (int ri = 0 ; ri != rotations.length ; ++ri) {
			    Shape s = rotations[ri];
			    if (dough.cuts(s,p)) {
				moves.add(new Move(si, ri, p));
			    }
			}
		    }
		}
	    }
	    if (maxCutterIndex == 2) {break;}
	    maxCutterIndex++;	    
	}
	return moves;
    }

    public ArrayList<Move> find_possible_moves(gameState state, int maxCutterIndex) { // used to propogate game tree
	return find_possible_moves(state, state.our_turn, maxCutterIndex);
    }

    public ArrayList<Move> find_possible_moves(gameState state, boolean our_turn) { // used to compute score function
	return find_possible_moves(state, our_turn, 2);
    }

    private Move random_move(gameState state) {
	ArrayList<Move> moves = find_possible_moves(state, true);
	return moves.get(0);
    }
    
    // function that will be called multiple times in real_cut with different parameters. set searchDough to opponent for behavior from last submission
    public Move find_cut(Dough dough, Dough searchDough, Shape[] shapes, Shape[] opponent_shapes, int maxCutterIndex) { 
	ArrayList <ComparableMove> moves = new ArrayList <ComparableMove> ();
	int[][] scoreBoard = getScoreBoard(dough, shapes, opponent_shapes);
	for (int i = 0 ; i != searchDough.side() ; ++i)
	    for (int j = 0 ; j != searchDough.side() ; ++j) {
		Point p = new Point(i, j);
		for (int si = 0 ; si <= maxCutterIndex ; ++si) {
		    if (shapes[si] == null) continue;
		    Shape[] rotations = shapes[si].rotations();
		    for (int ri = 0 ; ri != rotations.length ; ++ri) {
			Shape s = rotations[ri];
			if (dough.cuts(s,p) && searchDough.cuts(s,p)) {
			    Move m = new Move(si, ri, p);
			    moves.add(new ComparableMove(m, getScoreOfMove(scoreBoard, shapes, m),touched_edges(s,p,searchDough)));
			    // moves.add(new ComparableMove(new Move(si, ri, p), touched_edges(s,p,searchDough), s.size()));
			}
		    }
		}
	    }
	if (moves.size() != 0 && moves.size() < switch_strategy_threshold && getMinWidth(opponent_shapes[0]) <= 2) {
	    use_minimax = true;
	}
	if (moves.size() >= 1) {
	    Collections.sort(moves);
	    return moves.get(moves.size() - 1).move;
	}
	else {
	    return null;
	}
    }
    
    // computes the cut to be made
    public Move real_cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes) {
	// prune larger shapes if initial move	
	if (dough.uncut()) {
	    int min = Integer.MAX_VALUE;
	    for (Shape s : shapes)
		if (min > s.size())
		    min = s.size();
	    for (int s = 0 ; s != shapes.length ; ++s)
		if (shapes[s].size() != min)
		    shapes[s] = null;
	}
	int minWidth = getMinWidth(opponent_shapes[0]);
	if (minWidth > 2) {
	    switch_cutter_threshold = 200;
	}
	Move A = find_cut(dough, createPaddedBoard(dough, minWidth-1, minWidth-1, minWidth-1), shapes, opponent_shapes, 0); // pad all directions with minwidth
	if (A != null && !use_minimax) {
	    System.out.println("Move A");
	    return A;
	}
	else {
	    Move B = find_cut(dough, createPaddedBoard(dough, minWidth / 2 - 1, minWidth / 2 - 1, getMinWidth(opponent_shapes[1]) / 2 - 1), shapes, opponent_shapes, 0); // pad all directions with minwidth / 2
	    if (B != null && minWidth / 2 - 1 > 0 && !use_minimax) {
		System.out.println("Move B");
		return B;
	    }
	    else {
		gameState state = new gameState(dough, true, shapes, opponent_shapes);
		gameState opt_state = minimax(state, minimax_search_depth, minimax_cutter_index);
		
		if (opt_state.move_history.size() == 0) {
		    System.out.println("Move F");
		    return random_move(state);
		}
		System.out.println("Move D");		
		Move D = opt_state.move_history.get(0);
		return D;
	    }		
	}
    }
    
    private Dough createPaddedBoard(Dough dough, int verticalPadding, int horizontalPadding, int borderPadding) {
	Dough padded = new Dough(SIDE);
	for (int i=0; i<SIDE; i++) {
	    for (int j=0; j<SIDE; j++) {
		if (!dough.uncut(i,j)) {
		    cutPadding(padded, i,j,verticalPadding, horizontalPadding);
		}
	    }
	}
	cutBorder(padded, borderPadding-1);
	return padded;
    }
    
    private void cutPadding(Dough padded, int i, int j, int verticalPadding, int horizontalPadding) {
	for (int x = Math.max(0,i-horizontalPadding); x<=Math.min(SIDE-1,i+horizontalPadding); x++) {
	    for (int y=Math.max(0,j-verticalPadding); y<=Math.min(SIDE-1,j+verticalPadding); y++) {
		padded.cut(new Shape(new Point[] {new Point(0, 0)}), new Point(x, y));
	    }
	}
    }

    private void cutBorder(Dough padded, int w) {
	for (int i=0; i<SIDE; i++) {cutPadding(padded, i,0,w,w);}
	for (int i=0; i<SIDE; i++) {cutPadding(padded, i,SIDE-1,w,w);}
	for (int j=0; j<SIDE; j++) {cutPadding(padded, 0,j,w,w);}
	for (int j=0; j<SIDE; j++) {cutPadding(padded, SIDE-1,j,w,w);}
    }

    // function called by simulator
    public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
    {
	// Get cut done by opponent
	for (int i = 0; i < SIDE; i++) {
	    for (int j = 0; j < SIDE; j++) {
		if (!dough.uncut(i, j) && opponent.uncut(i, j) && self.uncut(i, j)) {
		    opponent.cut(new Shape(new Point[] {new Point(0, 0)}), new Point(i, j));
		}
	    }
	}
	Move move = real_cut(dough, shapes, opponent_shapes);
	// Get cut done by ourselves
	if (move != null) 
	    self.cut(shapes[move.shape].rotations()[move.rotation], move.point);
	return move;
    }
    
    private long touched_edges(Shape s, Point p, Dough d) {
	long sum = 0;
	for (Point q : s) {
	    if (cut(d, p.i + q.i + 1, p.j + q.j)) sum += 1;
	    if (cut(d, p.i + q.i - 1, p.j + q.j)) sum += 1;
	    if (cut(d, p.i + q.i, p.j + q.j + 1)) sum += 1;
	    if (cut(d, p.i + q.i, p.j + q.j - 1)) sum += 1;
	}
	return sum;
    }
    
    private boolean cut(Dough d, int i, int j) {
	return  i >= 0 && i < d.side() && j >= 0 && j < d.side() && !d.uncut(i, j);
    }

    private class ComparableMove implements Comparable<ComparableMove> {

	public Move move;
	public long key1;
	public long key2;
	public int randomized;

	public ComparableMove(Move move, long key1, long key2) {
	    this.move = move;
	    this.key1 = key1;
	    this.key2 = key2;
	    this.randomized = gen.nextInt();
	}

	@Override
	public int compareTo(ComparableMove o) {
	    int c = Long.compare(this.key2, o.key2);
	    if (c != 0) {
		return c;
	    }
	    c = Long.compare(this.key1, o.key1);
	    if (c != 0) {
		return c;
	    }
	    return Integer.compare(this.randomized, o.randomized);
	}
    }
}
