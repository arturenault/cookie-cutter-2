package cc2.g5;

import cc2.sim.Move;

public class MoveCosts {

    public Move move;
    public int playerMoves;
    public int opponentMoves;
    public float cost;

    public MoveCosts(Move m, int pm, int om){
        this.move = m;

        this.playerMoves = pm;
        this.opponentMoves = om;

        cost = (float)opponentMoves/(float)playerMoves;
    }
}
