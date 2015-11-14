package cc2.g5;

import cc2.sim.Move;

public class MoveCosts {

    public Move move;
    public float playerMoves;
    public float opponentMoves;
    public float cost;

    public MoveCosts(Move m, int opDiff, int ourDiff, float distance){
    	this.move = m;
        playerMoves = ourDiff;
        opponentMoves = opDiff;
    	if(opDiff == 0){
    		this.cost = Integer.MAX_VALUE;
    	}else{
    		this.cost = distance * ourDiff / opDiff;
    	}
    }
}
