package cc2.sim;

public interface Player {

	// return cutter shape (length given by simulator)
	// the positions can be given in any order but the
	// point coordinates must be positive the cutters
	// chosen before are shown are shown (normalized)
	public Shape cutter(int length, Shape[] your_cutters,
	                                Shape[] oppo_cutters);

	// return cutter id, rotation (id) and point offset
	public Move cut(Dough dough, Shape[] your_cutters,
	                             Shape[] oppo_cutters);
}
