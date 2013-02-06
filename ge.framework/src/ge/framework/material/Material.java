package ge.framework.material;

/**
 * Represents a material.
 * @author user
 */
public class Material
{
	private final float edge = (1.0f / 1920.0f);

	private float x1;
	private float x2;
	private float y1;
	private float y2;

	/**
	 * Constructor.
	 */
	public Material(
		final float x1,
		final float x2,
		final float y1,
		final float y2)
	{
		this.x1 = x1 + edge;
		this.x2 = x2 - edge;
		this.y1 = y1 + edge;
		this.y2 = y2 - edge;
	}

	/**
	 * Constructor.
	 */
	public Material(
		final int xd,
		final int yd,
		final int xc,
		final int yc)
	{
		x1 = xc / (float) xd + edge;
		x2 = (xc + 1) / (float) xd - edge;
		y1 = yc / (float) yd + edge;
		y2 = (yc + 1) / (float) yd - edge;
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
