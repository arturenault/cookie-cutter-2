package cc2.g5;

import cc2.sim.Dough;
import cc2.sim.Point;
import cc2.sim.Shape;

public class ModdableDough extends Dough {

	public ModdableDough(Dough d) {
		super(d.side());
        Point[] s = {new Point(0,0)};
		Shape shape = new Shape(s);
		for(int i = 0; i < d.side(); ++i) {
			for(int j = 0; j < d.side(); ++j) {
				if(!d.uncut(i, j))
					cut(shape, new Point(i,j));
			}
		}
	}

}
