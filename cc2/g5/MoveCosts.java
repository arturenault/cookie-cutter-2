package cc2.g5;

import cc2.sim.Move;

public class MoveCosts {

    public Move move;
    public float playerMoves;
    public float opponentMoves;
    public float cost;

    public MoveCosts(Move m, float pm, float om, double distance){
        this.move = m;

        this.playerMoves = pm;
        this.opponentMoves = om;

        cost = opponentMoves / playerMoves; // + 1 / (float)distance);
    }
}
