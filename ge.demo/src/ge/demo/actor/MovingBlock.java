package ge.demo.actor;

import org.lwjgl.util.vector.Vector3f;

import ge.framework.mesh.Mesh;

public class MovingBlock extends Actor
{

	public MovingBlock()
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
			action = (int) (Math.random() * 10);
			duration = (float) (Math.random() * 5000);
		}

		// Wait
		if (action == 0)
		{
		}

		// Move forward
		if ((action == 1) || (action == 4) || (action == 5) || (action == 6) || (action == 7) || (action == 8) || (action == 9))
		{
			double ry = Math.toRadians(rotation.y);
			position.x += (float) Math.sin(ry) * 0.05f;
			position.z += (float) Math.cos(ry) * 0.05f;
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
