package cc2.g2;

import cc2.sim.Dough;
import cc2.sim.Point;
import cc2.sim.Shape;

public class ModdableDough extends Dough {
	public ModdableDough(Dough dough) {
		super(dough.side());
		Point[] s = {new Point(0,0)};
		Shape shape = new Shape(s);
		for(int i = 0; i < dough.side(); ++i) {
			for(int j = 0; j < dough.side(); ++j) {
				if(!dough.uncut(i, j))
					cut(shape, new Point(i,j));
			}
		}
	}
}
