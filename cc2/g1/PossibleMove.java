package cc2.g1;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Shape;

public class PossibleMove {
	Move m;
	int score;
	
	
	
	public PossibleMove(Move m, int score) {
		super();
		this.m = m;
		this.score = score;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m == null) ? 0 : m.hashCode());
		result = prime * result + score;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PossibleMove other = (PossibleMove) obj;
		if (m == null) {
			if (other.m != null)
				return false;
		} else if (!m.equals(other.m))
			return false;
		if (score != other.score)
			return false;
		return true;
	}
	

}
