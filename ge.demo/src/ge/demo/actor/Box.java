package ge.demo.actor;

import ge.framework.material.Material;
import ge.framework.mesh.Mesh;

public class Box
{

	/**
	 * Constructor.
	 */
	private Box()
	{
		// Call super constructor
		super();
	}

	public static void draw(
		final Mesh mesh,
		final float positionX1,
		final float positionX2,
		final float positionY1,
		final float positionY2,
		final float positionZ1,
		final float positionZ2,
		final Material[] materials,
		final float[] light,
		final boolean top,
		final boolean bottom,
		final boolean front,
		final boolean back,
		final boolean left,
		final boolean right)
	{
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
					positionX1, positionY1, positionZ2,
					0.0f, 1.0f, 0.0f,
					color1, color1, color1, 1.0f,
					materials[0].getX2(), materials[0].getY1());
			// Top left
			mesh.addVertex(
					positionX2, positionY1, positionZ2,
					0.0f, 1.0f, 0.0f,
					color2, color2, color2, 1.0f,
					materials[0].getX1(), materials[0].getY1());
			// Botton left
			mesh.addVertex(
					positionX2, positionY1, positionZ1,
					0.0f, 1.0f, 0.0f,
					color3, color3, color3, 1.0f,
					materials[0].getX1(), materials[0].getY2());
			// Botton rught
			mesh.addVertex(
					positionX1, positionY1, positionZ1,
					0.0f, 1.0f, 0.0f,
					color4, color4, color4, 1.0f,
					materials[0].getX2(), materials[0].getY2());
		}

		// Bottom
		if (bottom == true)
		{
			// Top right
			mesh.addVertex(
					positionX1, positionY2, positionZ1,
					0, 0, 0,
					//color6, color6, color6, 1.0f,
					color8, color8, color8, 1.0f,
					materials[1].getX2(), materials[1].getY1());
			// Top left
			mesh.addVertex(
					positionX2, positionY2, positionZ1,
					0, 0, 0,
					//color5, color5, color5, 1.0f,
					color7, color7, color7, 1.0f,
					materials[1].getX1(), materials[1].getY1());
			// Botton left
			mesh.addVertex(
					positionX2, positionY2, positionZ2,
					0, 0, 0,
					//color8, color8, color8, 1.0f,
					color6, color6, color6, 1.0f,
					materials[1].getX1(), materials[1].getY2());
			// Bottom right
			mesh.addVertex(
					positionX1, positionY2, positionZ2,
					0, 0, 0,
					//color7, color7, color7, 1.0f,
					color5, color5, color5, 1.0f,
					materials[1].getX2(), materials[1].getY2());
		}

		// Front
		if (front == true)
		{
			// Top right
			mesh.addVertex(
					positionX1, positionY1, positionZ1,
					0.0f, 0.0f, 1.0f,
					color4, color4, color4, 1.0f,
					materials[2].getX2(), materials[2].getY1());
			// Top left
			mesh.addVertex(
					positionX2, positionY1, positionZ1,
					0.0f, 0.0f, 1.0f,
					color3, color3, color3, 1.0f,
					materials[2].getX1(), materials[2].getY1());
			// Bottom left
			mesh.addVertex(
					positionX2, positionY2, positionZ1,
					0.0f, 0.0f, 1.0f,
					color7, color7, color7, 1.0f,
					materials[2].getX1(), materials[2].getY2());
			// Bottom right
			mesh.addVertex(
					positionX1, positionY2, positionZ1,
					0.0f, 0.0f, 1.0f,
					color8, color8, color8, 1.0f,
					materials[2].getX2(), materials[2].getY2());
		}

		// Back
		if (back == true)
		{
			// Top right
			mesh.addVertex(
					positionX2, positionY1, positionZ2,
					0, 0, 0,
					color2, color2, color2, 1.0f,
					materials[3].getX2(), materials[3].getY1());
			// Top let
			mesh.addVertex(
					positionX1, positionY1, positionZ2,
					0, 0, 0,
					color1, color1, color1, 1.0f,
					materials[3].getX1(), materials[3].getY1());
			// Bottom left
			mesh.addVertex(
					positionX1, positionY2, positionZ2,
					0, 0, 0,
					color5, color5, color5, 1.0f,
					materials[3].getX1(), materials[3].getY2());
			// Bottom right
			mesh.addVertex(
					positionX2, positionY2, positionZ2,
					0, 0, 0,
					color6, color6, color6, 1.0f,
					materials[3].getX2(), materials[3].getY2());
		}

		// Left
		if (left == true)
		{
			// Top right
			mesh.addVertex(
					positionX2, positionY1, positionZ1,
					0, 0, 0,
					color3, color3, color3, 1.0f,
					materials[4].getX2(), materials[4].getY1());
			// Top left
			mesh.addVertex(
					positionX2, positionY1, positionZ2,
					0, 0, 0,
					color2, color2, color2, 1.0f,
					materials[4].getX1(), materials[4].getY1());
			// Bottom left
			mesh.addVertex(
					positionX2, positionY2, positionZ2,
					0, 0, 0,
					color6, color6, color6, 1.0f,
					materials[4].getX1(), materials[4].getY2());
			// Bottom right
			mesh.addVertex(
					positionX2, positionY2, positionZ1,
					0, 0, 0,
					color7, color7, color7, 1.0f,
					materials[4].getX2(), materials[4].getY2());
		}

		// Right
		if (right == true)
		{
			// Top right
			mesh.addVertex(
					positionX1, positionY1, positionZ2,
					0, 0, 0,
					color1, color1, color1, 1.0f,
					materials[5].getX2(), materials[5].getY1());
			// Top left
			mesh.addVertex(
					positionX1, positionY1, positionZ1,
					0, 0, 0,
					color4, color4, color4, 1.0f,
					materials[5].getX1(), materials[5].getY1());
			// Bottom left
			mesh.addVertex(
					positionX1, positionY2, positionZ1,
					0, 0, 0,
					color8, color8, color8, 1.0f,
					materials[5].getX1(), materials[5].getY2());
			// Bottom right
			mesh.addVertex(
					positionX1, positionY2, positionZ2,
					0, 0, 0,
					color5, color5, color5, 1.0f,
					materials[5].getX2(), materials[5].getY2());
		}

	}

}
