package ge.framework.util;
import org.lwjgl.util.vector.Vector3f;

public class Ray
{
	private Vector3f position;
	private Vector3f direction;

	/*
	 * Constructor.
	 */
	public Ray(
		final Vector3f position,
		final Vector3f direction)
	{
		this.position = position;
		this.direction = direction;
	}

	public Vector3f getPosition()
	{
		return position;
	}

	public Vector3f getDirection()
	{
		return direction;
	}

}
