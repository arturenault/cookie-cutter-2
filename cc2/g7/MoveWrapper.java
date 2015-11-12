package cc2.g7;

import cc2.sim.Dough;
import cc2.sim.Move;

public class MoveWrapper {
	
	Move move;
	int sum11;
	int sum8;
	int sum5;
	
	public MoveWrapper(Move move, int sum11, int sum8, int sum5)
	{
		this.move = move;
		this.sum11 = sum11;
		this.sum8 = sum8;
		this.sum5 = sum5;
	}
}
