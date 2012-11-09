package ge.demo.shape;

import ge.framework.mesh.Mesh;

public class HalfWedge extends Shape
{
	private int orientation;

	/**
	 * Constructor.
	 * @param id The identifier
	 * @param textures The textures
	 */
	public HalfWedge(
		final int orientation)
	{
		// Call super constructor
		// top, bottom, front, back, left, right
		super(null, true, true, true, true, true, true);

		this.orientation = orientation;
	}

	public byte draw(
		final Mesh mesh,
		final int x,
		final int y,
		final int z,
		final float[] light,
		final boolean top,
		final boolean bottom,
		final boolean front,
		final boolean back,
		final boolean left,
		final boolean right,
		final boolean xd1,
		final boolean xd2,
		final boolean xd3,
		final boolean xd4)
	{
		byte count = 0;
		float unit = 1.0f;

		float x2 = x * unit;
		float x1 = x2 + unit;
		float y2 = y * unit;
		float y1 = y2 + unit;
		float z2 = z * unit;
		float z1 = z2 + unit;

		float ty1 = 0.0f;
		float ty2 = 0.0f;
		float ty3 = 0.0f;
		float ty4 = 0.0f;

		if (orientation == 1)
		{
			ty1 = 1.0f;
		}

		if (orientation == 2)
		{
			ty2 = 1.0f;
		}

		if (orientation == 3)
		{
			ty3 = 1.0f;
		}

		if (orientation == 4)
		{
			ty4 = 1.0f;
		}

		float tc = 0.15f;

		// Top
		if (top == true)
		{
			// Top right
			mesh.addVertex(
					x1, y1 - ty1, z2,
					0.0f, 1.0f, 0.0f,
					1.0f - tc, 1.0f - tc, 1.0f - tc, 1.0f,
					0.5f, 1.0f);
			// Top left
			mesh.addVertex(
					x2, y1 - ty2, z2,
					0.0f, 1.0f, 0.0f,
					1.0f - tc, 1.0f - tc, 1.0f - tc, 1.0f,
					0.0f, 1.0f);
			// Botton left
			mesh.addVertex(
					x2, y1 - ty3, z1,
					0.0f, 1.0f, 0.0f,
					1.0f - tc, 1.0f - tc, 1.0f - tc, 1.0f,
					0.0f, 0.0f);
			// Botton rught
			mesh.addVertex(
					x1, y1 - ty4, z1,
					0.0f, 1.0f, 0.0f,
					1.0f - tc, 1.0f - tc, 1.0f - tc, 1.0f,
					0.5f, 0.0f);

			count++;
		}

		// Bottom
		if (bottom == true)
		{
			// Top right
			mesh.addVertex(
					x1, y2, z1,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 1.0f);
			// Top left
			mesh.addVertex(
					x2, y2, z1,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 1.0f);
			// Botton left
			mesh.addVertex(
					x2, y2, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 0.0f);
			// Bottom right
			mesh.addVertex(
					x1, y2, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 0.0f);

			count++;
		}

		// Front
		if (front == true)
		{
			// Top right
			mesh.addVertex(
					x1, y1 - ty4, z1,
					0.0f, 0.0f, 1.0f,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 0.0f);
			// Top left
			mesh.addVertex(
					x2, y1 - ty3, z1,
					0.0f, 0.0f, 1.0f,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 0.0f);
			// Bottom left
			mesh.addVertex(
					x2, y2, z1,
					0.0f, 0.0f, 1.0f,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 1.0f);
			// Bottom right
			mesh.addVertex(
					x1, y2, z1,
					0.0f, 0.0f, 1.0f,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 1.0f);

			count++;
		}

		// Back
		if (back == true)
		{
			// Top right
			mesh.addVertex(
					x2, y1 - ty2, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 0.0f);
			// Top let
			mesh.addVertex(
					x1, y1 - ty1, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 0.0f);
			// Bottom left
			mesh.addVertex(
					x1, y2, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 1.0f);
			// Bottom right
			mesh.addVertex(
					x2, y2, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 1.0f);

			count++;
		}

		// Left
		if (left == true)
		{
			// Top right
			mesh.addVertex(
					x2, y1 - ty3, z1,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 0.0f);
			// Top left
			mesh.addVertex(
					x2, y1 - ty2, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 0.0f);
			// Bottom left
			mesh.addVertex(
					x2, y2, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 1.0f);
			// Bottom right
			mesh.addVertex(
					x2, y2, z1,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 1.0f);

			count++;
		}

		// Right
		if (right == true)
		{
			// Top right
			mesh.addVertex(
					x1, y1 - ty1, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 0.0f);
			// Top left
			mesh.addVertex(
					x1, y1 - ty4, z1,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 0.0f);
			// Bottom left
			mesh.addVertex(
					x1, y2, z1,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					0.5f, 1.0f);
			// Bottom right
			mesh.addVertex(
					x1, y2, z2,
					0, 0, 0,
					0.5f, 0.5f, 0.5f, 1.0f,
					1.0f, 1.0f);

			count++;
		}

		return count;
	}

}
