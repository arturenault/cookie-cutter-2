
package cc2.g8;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class DummyDough extends Dough {

	public DummyDough(int side) {
		super(side);
	}

	public boolean[][] getDough() {
		return super.dough;
	}
}
