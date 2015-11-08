package cc2.g7;

import cc2.sim.Dough;

public class NewDough extends Dough {
	public NewDough(NewDough oridough)
	{
		super(oridough.dough.length);
		dough = oridough.dough;
		n_cuts = oridough.n_cuts;;
	}
}
