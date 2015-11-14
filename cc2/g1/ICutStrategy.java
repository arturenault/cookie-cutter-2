package cc2.g1;

import cc2.sim.Dough;
import cc2.sim.Move;
import cc2.sim.Shape;

public interface ICutStrategy {
	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes);
}
