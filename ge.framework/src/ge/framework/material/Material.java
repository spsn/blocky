package ge.framework.material;

/**
 * Represents a material.
 * @author user
 *
 */
public class Material
{
	private float x1;
	private float x2;
	private float y1;
	private float y2;

	/**
	 * Constructor.
	 */
	public Material(
		final int xd,
		final int yd,
		final int xc,
		final int yc)
	{
		x1 = xc / (xd * 1.0f) + (1.0f / 1920.0f);
		x2 = (xc + 1) / (xd * 1.0f) - (1.0f / 1920.0f);
//		y1 = (yd - yc - 1) / (yd * 1.0f);
//		y2 = (yd - yc) / (yd * 1.0f);
		y1 = yc / (yd * 1.0f) + (1.0f / 1080.0f);
		y2 = (yc + 1) / (yd * 1.0f) - (1.0f / 1080.0f);
	}

	/**
	 * @return Returns the x1.
	 */
	public float getX1()
	{
		return x1;
	}

	/**
	 * @return Returns the x2.
	 */
	public float getX2()
	{
		return x2;
	}

	/**
	 * @return Returns the y1.
	 */
	public float getY1()
	{
		return y1;
	}

	/**
	 * @return Returns the y2.
	 */
	public float getY2()
	{
		return y2;
	}

}
