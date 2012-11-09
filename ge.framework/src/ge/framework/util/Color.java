package ge.framework.util;

public class Color
{
	private float red;
	private float green;
	private float blue;
	private float alpha;

	/*
	 * Constructor.
	 */
	public Color(
		final float red,
		final float green,
		final float blue,
		final float alpha)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public float getRed()
	{
		return red;
	}

	public float getGreen()
	{
		return green;
	}

	public float getBlue()
	{
		return blue;
	}

	public float getAlpha()
	{
		return alpha;
	}

}
