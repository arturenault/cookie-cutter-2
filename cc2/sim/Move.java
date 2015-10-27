package cc2.sim;

public class Move {

	public final int shape;
	public final int rotation;
	public final Point point;

	public Move(int shape, int rotation, Point point)
	{
		this.shape = shape;
		this.rotation = rotation;
		this.point = point;
	}
}
