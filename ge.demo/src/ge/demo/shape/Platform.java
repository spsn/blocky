package ge.demo.shape;

import ge.framework.material.Material;
import ge.framework.mesh.Mesh;

public class Platform extends Shape
{

	/**
	 * Constructor.
	 * @param id The identifier
	 * @param textures The textures
	 */
	public Platform(
		final Material[] materials)
	{
		// Call super constructor
		// top, bottom, front, back, left, right
		super(materials, true, true, true, true, true, true);
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

		float x2 = x * unit - 0.5f;
		float x1 = x2 + unit;
		float y2 = y + (0.75f * unit) - 0.5f;
		float y1 = y2 + (0.25f * unit);
		float z2 = z * unit - 0.5f;
		float z1 = z2 + unit;

//		float x2 = x * unit - 0.5f;
//		float x1 = x2 + unit;
//		float y2 = y * unit - 0.5f;
//		float y1 = y2 + unit;
//		float z2 = z * unit - 0.5f;
//		float z1 = z2 + unit;

		float base = 0.0f;
		float ratio = (1.0f - base) / 128;

		float color1 = light[0] * ratio + base;
		float color2 = light[1] * ratio + base;
		float color3 = light[2] * ratio + base;
		float color4 = light[3] * ratio + base;
		float color5 = light[4] * ratio + base;
		float color6 = light[5] * ratio + base;
		float color7 = light[6] * ratio + base;
		float color8 = light[7] * ratio + base;

		// Top
		if (top == true)
		{
			// Top right
			mesh.addVertex(
					x1, y1, z2,
					0.0f, 1.0f, 0.0f,
					color1, color1, color1, 1.0f,
					materials[0].getX2(), materials[0].getY1());
			// Top left
			mesh.addVertex(
					x2, y1, z2,
					0.0f, 1.0f, 0.0f,
					color2, color2, color2, 1.0f,
					materials[0].getX1(), materials[0].getY1());
			// Botton left
			mesh.addVertex(
					x2, y1, z1,
					0.0f, 1.0f, 0.0f,
					color3, color3, color3, 1.0f,
					materials[0].getX1(), materials[0].getY2());
			// Botton rught
			mesh.addVertex(
					x1, y1, z1,
					0.0f, 1.0f, 0.0f,
					color4, color4, color4, 1.0f,
					materials[0].getX2(), materials[0].getY2());

			count++;
		}

		// Bottom
//		if (bottom == true)
		{
			// Top right
			mesh.addVertex(
					x1, y2, z1,
					0, 0, 0,
					//color6, color6, color6, 1.0f,
					color8, color8, color8, 1.0f,
					materials[1].getX2(), materials[1].getY1());
			// Top left
			mesh.addVertex(
					x2, y2, z1,
					0, 0, 0,
					//color5, color5, color5, 1.0f,
					color7, color7, color7, 1.0f,
					materials[1].getX1(), materials[1].getY1());
			// Botton left
			mesh.addVertex(
					x2, y2, z2,
					0, 0, 0,
					//color8, color8, color8, 1.0f,
					color6, color6, color6, 1.0f,
					materials[1].getX1(), materials[1].getY2());
			// Bottom right
			mesh.addVertex(
					x1, y2, z2,
					0, 0, 0,
					//color7, color7, color7, 1.0f,
					color5, color5, color5, 1.0f,
					materials[1].getX2(), materials[1].getY2());

			count++;
		}

		// Front
		if (front == true)
		{
			// Top right
			mesh.addVertex(
					x1, y1, z1,
					0.0f, 0.0f, 1.0f,
					color4, color4, color4, 1.0f,
					materials[2].getX2(), materials[2].getY1());
			// Top left
			mesh.addVertex(
					x2, y1, z1,
					0.0f, 0.0f, 1.0f,
					color3, color3, color3, 1.0f,
					materials[2].getX1(), materials[2].getY1());
			// Bottom left
			mesh.addVertex(
					x2, y2, z1,
					0.0f, 0.0f, 1.0f,
					color7, color7, color7, 1.0f,
					materials[2].getX1(), materials[2].getY1() + (materials[2].getY2() - materials[2].getY1()) / 4.0f);
			// Bottom right
			mesh.addVertex(
					x1, y2, z1,
					0.0f, 0.0f, 1.0f,
					color8, color8, color8, 1.0f,
					materials[2].getX2(), materials[2].getY1() + (materials[2].getY2() - materials[2].getY1()) / 4.0f);

			count++;
		}

		// Back
		if (back == true)
		{
			// Top right
			mesh.addVertex(
					x2, y1, z2,
					0, 0, 0,
					color2, color2, color2, 1.0f,
					materials[3].getX2(), materials[3].getY1());
			// Top let
			mesh.addVertex(
					x1, y1, z2,
					0, 0, 0,
					color1, color1, color1, 1.0f,
					materials[3].getX1(), materials[3].getY1());
			// Bottom left
			mesh.addVertex(
					x1, y2, z2,
					0, 0, 0,
					color5, color5, color5, 1.0f,
					materials[3].getX1(), materials[3].getY1() + (materials[3].getY2() - materials[3].getY1()) / 4.0f);
			// Bottom right
			mesh.addVertex(
					x2, y2, z2,
					0, 0, 0,
					color6, color6, color6, 1.0f,
					materials[3].getX2(), materials[3].getY1() + (materials[3].getY2() - materials[3].getY1()) / 4.0f);

			count++;
		}

		// Left
		if (left == true)
		{
			// Top right
			mesh.addVertex(
					x2, y1, z1,
					0, 0, 0,
					color3, color3, color3, 1.0f,
					materials[4].getX2(), materials[4].getY1());
			// Top left
			mesh.addVertex(
					x2, y1, z2,
					0, 0, 0,
					color2, color2, color2, 1.0f,
					materials[4].getX1(), materials[4].getY1());
			// Bottom left
			mesh.addVertex(
					x2, y2, z2,
					0, 0, 0,
					color6, color6, color6, 1.0f,
					materials[4].getX1(), materials[4].getY1() + (materials[4].getY2() - materials[4].getY1()) / 4.0f);
			// Bottom right
			mesh.addVertex(
					x2, y2, z1,
					0, 0, 0,
					color7, color7, color7, 1.0f,
					materials[4].getX2(), materials[4].getY1() + (materials[4].getY2() - materials[4].getY1()) / 4.0f);

			count++;
		}

		// Right
		if (right == true)
		{
			// Top right
			mesh.addVertex(
					x1, y1, z2,
					0, 0, 0,
					color1, color1, color1, 1.0f,
					materials[5].getX2(), materials[5].getY1());
			// Top left
			mesh.addVertex(
					x1, y1, z1,
					0, 0, 0,
					color4, color4, color4, 1.0f,
					materials[5].getX1(), materials[5].getY1());
			// Bottom left
			mesh.addVertex(
					x1, y2, z1,
					0, 0, 0,
					color8, color8, color8, 1.0f,
					materials[5].getX1(), materials[5].getY1() + (materials[5].getY2() - materials[5].getY1()) / 4.0f);
			// Bottom right
			mesh.addVertex(
					x1, y2, z2,
					0, 0, 0,
					color5, color5, color5, 1.0f,
					materials[5].getX2(), materials[5].getY1() + (materials[5].getY2() - materials[5].getY1()) / 4.0f);

			count++;
		}

		return count;
	}

}
