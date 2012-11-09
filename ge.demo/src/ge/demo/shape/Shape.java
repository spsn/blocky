package ge.demo.shape;

import ge.framework.material.Material;
import ge.framework.mesh.Mesh;

public abstract class Shape
{
	protected Material[] materials;

	private boolean top;
	private boolean bottom;
	private boolean front;
	private boolean back;
	private boolean left;
	private boolean right;

	/**
	 * Constructor.
	 * top, bottom, front, back, left, right
	 */
	public Shape(
		final Material[] materials,
		final boolean top,
		final boolean bottom,
		final boolean front,
		final boolean back,
		final boolean left,
		final boolean right)
	{
		// Call super constructor
		super();

		this.materials = materials;
		this.top = top;
		this.bottom = bottom;
		this.front = front;
		this.back = back;
		this.left = left;
		this.right = right;
	}

	public Material[] getMaterials()
	{
		return materials;
	}

	public boolean hasTop()
	{
		return top;
	}

	public boolean hasBottom()
	{
		return bottom;
	}

	public boolean hasFront()
	{
		return front;
	}

	public boolean hasBack()
	{
		return back;
	}

	public boolean hasLeft()
	{
		return left;
	}

	public boolean hasRight()
	{
		return right;
	}

	public abstract byte draw(
		final Mesh mesh,
		final int x,
		final int y,
		final int z,
		final float[] light,
		final boolean _top,
		final boolean _bottom,
		final boolean _front,
		final boolean _back,
		final boolean _left,
		final boolean _right,
		final boolean _d1,
		final boolean _d2,
		final boolean _d3,
		final boolean _d4);

}
