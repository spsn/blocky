package ge.demo.actor;

import org.lwjgl.util.vector.Vector3f;

import ge.framework.mesh.Mesh;

public class FlyingBlock extends Actor
{

	public FlyingBlock()
	{
		// TODO Auto-generated constructor stub
	}
	
	public void draw(Mesh mesh)
	{
		
	}

	public void act(long delta)
	{
		duration -= delta;

		if (duration <= 0)
		{
			action = (int) (Math.random() * 12);
			duration = (float) (Math.random() * 5000);
		}

		// Move forward
		if ((action == 0) || (action == 1) || (action == 2) || (action == 3) || (action == 4) || (action == 5) || (action == 6)
			|| (action == 7) || (action == 8) || (action == 9) || (action == 10) || (action == 11))
		{
			double ry = Math.toRadians(rotation.y);
			double rx = Math.toRadians(rotation.x);
			float sx = -(float) Math.sin(rx - 90);
			position.x += (float) Math.sin(ry) * 0.05f * sx;
			position.z += (float) Math.cos(ry) * 0.05f * sx;
			position.y -= (float) Math.sin(rx) * 0.05f;
		}

		// Turn left
		if ((action == 2) || (action == 5))
		{
			rotation.y -= 1.0f;

			if (rotation.y < 0)
			{
				rotation.y = 360;
			}

		}

		// Turn right
		if ((action == 3) || (action == 6))
		{
			rotation.y += 1.0f;

			if (rotation.y > 360)
			{
				rotation.y = 0;
			}

		}

		// Turn up
		if ((action == 10) || (action == 7) || (action == 5) || (action == 6))
		{
			rotation.x -= 1.0f;

			if (rotation.x < 0)
			{
				rotation.x = 360;
			}

		}

		// Turn down
		if ((action == 11) || (action == 8) || (action == 5) || (action == 6))
		{
			rotation.x += 1.0f;

			if (rotation.x > 360)
			{
				rotation.x = 0;
			}

		}
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;
	}

	public void setRotation(Vector3f rotation)
	{
		this.rotation = rotation;
	}

}
